package service.rag;

import comom.enums.ChatEventTypeEnum;
import vo.ChatEventVO;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import service.session.SessionService;
import start.load.PromptConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatImpl implements Chat {

    @Resource(name = "chatClient")
    private ChatClient chatClient;
    @Autowired
    private PromptConfig systemPromptConfig;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private OpenAiEmbeddingModel embeddingModel;
    @Autowired
    private VectorStore vectorStore;

    private final static String OUTPUT_STATUS = "OUTPUT_STATUS";

    /**
     * chat
     *
     * @param question  问题
     * @param sessionId 会话id
     * @return 回答内容
     */
    @Override
    public Flux<ChatEventVO> chat(String question, String sessionId) {

        sessionService.updateSessionTitle(sessionId, question);
        /**
         * 实现类 memory.MysqlChatMemoryRepository.java
         * public List<Message> findByConversationId(String conversationId) {}自动查询加入session
         */
        var conversationId = Chat.getConversationId(sessionId);

        //控制是否stop+追加保存
        var outputHash = stringRedisTemplate.boundHashOps(OUTPUT_STATUS);
        var outputBuilder = new StringBuilder();
        // rag搜索
        String result = rag(question,sessionId);
        if (result == null){
            return Flux.just(
                    ChatEventVO.builder()
                            .eventData("抱歉，知识库中未找到相关信息，无法回答。")
                            .eventType(ChatEventTypeEnum.DATA.getValue())
                            .build(),
                    ChatEventVO.builder()
                            .eventType(ChatEventTypeEnum.STOP.getValue())
                            .build());
        }
        return chatClient.prompt()
                .user(result)
                .advisors(advisorSpec -> advisorSpec
                        //会话记忆
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .system(promptSystemSpec -> promptSystemSpec
                        //系统role
                        .text(systemPromptConfig.getChatSystemMessage().get())
                        .param("now", LocalDateTime.now())
                )
                .stream()
                .chatResponse()
                // 第一次输出内容时执行
                // 出现异常时，删除标识
                // 完成时执行，删除标识
                .doFirst(() -> outputHash.put(sessionId, "true"))  // 将布尔值转换为字符串存入 Redis
                .doOnError(throwable -> outputHash.delete(sessionId))
                .doOnComplete(() -> outputHash.delete(sessionId))
                .doOnCancel(() -> {
                    this.saveStopHistoryRecord(conversationId, outputBuilder.toString());
                })
                //控制是否继续
                .takeWhile(chatResponse -> outputHash.get(sessionId) != null)
                // 流式返回的部分分块（如 usage/finish 分块）getResult() 可能为 null，直接跳过
                .filter(chatResponse -> chatResponse != null
                        && chatResponse.getResult() != null
                        && chatResponse.getResult().getOutput() != null
                        && chatResponse.getResult().getOutput().getText() != null)
                .map(chatResponse -> {
                    String response = chatResponse.getResult().getOutput().getText();
                    // 追加到输出内容中
                    outputBuilder.append(response);
                    ChatEventVO chatEventVO = ChatEventVO.builder()
                            .eventData(response)
                            .eventType(ChatEventTypeEnum.DATA.getValue())
                            .build();
                    return chatEventVO;
                })
                .onErrorResume(e -> {
                    log.error("对话流式输出异常, sessionId={}", sessionId, e);
                    outputHash.delete(sessionId);
                    return Flux.just(ChatEventVO.builder()
                            .eventData("生成失败，请稍后重试")
                            .eventType(ChatEventTypeEnum.DATA.getValue())
                            .build());
                })
                .concatWith(Flux.just(ChatEventVO.builder()
                                .eventType(ChatEventTypeEnum.STOP.getValue())
                                .build()));
    }
    private String rag(String question, String sessionId) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .similarityThreshold(0.6d)
                .topK(6)
                .build();
        List<Document> retrievedDocs = vectorStore.similaritySearch(searchRequest);
        // topK 中任意一条相似度 ≥ 0.6 即命中
        List<Document> hitDocs = retrievedDocs.stream()
                .filter(d -> d.getScore() != null && d.getScore() >= 0.6d)
                .toList();
        if (hitDocs.isEmpty()) {
            log.info("RAG未命中, question={}, sessionId={}, 召回数={}, 最高分={}",
                    question, sessionId, retrievedDocs.size(),
                    retrievedDocs.stream()
                            .map(Document::getScore)
                            .filter(java.util.Objects::nonNull)
                            .max(Double::compareTo)
                            .map(String::valueOf)
                            .orElse("无召回"));
            return null;
        }
        log.info("RAG命中, question={}, 召回数={}, 达标数={}, top1分数={}",
                question, retrievedDocs.size(), hitDocs.size(),
                retrievedDocs.get(0).getScore());

        // context 只拼达标文档，不达标的别喂给模型（省 token、降低干扰）
        String context = hitDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));
        String prompt = """
                         请根据以下参考上下文回答问题。如果上下文中没有答案，请明确说"不知道"。
                         ## 参考上下文
                         %s
                         ## 问题
                         %s
                        """ .formatted(context, question);
        return prompt;
    }
    /**
     * 保存停止输出的记录
     *
     * @param conversationId 会话id
     * @param content        大模型输出的内容
     */
    private void saveStopHistoryRecord(String conversationId, String content) {
        chatMemory.add(conversationId, new AssistantMessage(content));
    }

    /**
     * 停止生成
     *
     * @param sessionId 会话id
     */
    @Override
    public void stop(String sessionId) {
        var outputHash = stringRedisTemplate.boundHashOps(OUTPUT_STATUS);
        // 移除标记
        outputHash.delete(sessionId);
    }

    @Override
    public EmbeddingResponse embedForResponse(List<String> message) {
        return embeddingModel.embedForResponse(message);
    }

    @Override
    public List<Document> searchMatch(String message) {
        return vectorStore.similaritySearch(SearchRequest.builder().query(message).topK(2).build());
    }


    @Override
    public void deleteById(List<String> ids) {
        vectorStore.delete(ids);
    }

}
