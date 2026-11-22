package com.branch.service.tool;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.branch.domain.enums.ChatEventTypeEnum;
import com.branch.domain.vo.ChatEventVO;
import com.branch.service.memory.mysql.ChatRecordService;
import com.branch.service.session.SessionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
public class ToolServiceImpl implements ToolService {
    @Resource(name = "toolClient")
    private ChatClient chatClient;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private ChatRecordService chatRecordService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ChatMemory chatMemory;

    private final static String  OUTPUT_STATUS = "OUTPUT_STATUS";
    // 输出结束的标记
    private static final ChatEventVO STOP_EVENT = ChatEventVO.builder()
            .eventType(ChatEventTypeEnum.STOP.getValue())
            .build();

    @Override
    public Flux<ChatEventVO> chat(String visualValue, String question, String sessionId) {

        PromptTemplate promptTemplate = new PromptTemplate(
                        "你是一个花店查询家。根据用户描述{input}，" +
                        "自行选择信息作为查询条件，进行解决用户的问题 {question}。");
        String prompt = promptTemplate.render(
                Map.of("input", visualValue, "question", question)
        );

        // (1)大模型输出内容的缓存器，用于在输出中断后的数据存储
        var outputBuilder = new StringBuilder();
        //会话id-->转sessionId
        var conversationId = ToolService.getConversationId(sessionId);
        // 生成请求id
        var requestId = IdUtil.fastSimpleUUID();
        //控制是否stop
        var outputHash = stringRedisTemplate.boundHashOps(OUTPUT_STATUS);
        return chatClient.prompt()
                .user(prompt)
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
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

                .filter(chatResponse -> chatResponse != null
                        && chatResponse.getResult() != null
                        && chatResponse.getResult().getOutput() != null
                        && chatResponse.getResult().getOutput().getText() != null)

                .map(chatResponse -> {
                    var finishReason = chatResponse.getResult().getMetadata().getFinishReason();
                    if (StrUtil.equals("STOP", finishReason)) {
                        var messageId = chatResponse.getMetadata().getId();
                        ToolResultHolder.put(messageId, "requestId", requestId);
                    }
                    String response = chatResponse.getResult().getOutput().getText();
                    // 追加到输出内容中
                    outputBuilder.append(response);
                    ChatEventVO chatEventVO = ChatEventVO.builder()
                            .eventData(response)
                            .eventType(ChatEventTypeEnum.DATA.getValue())
                            .build();
                    return chatEventVO;
                })
                .concatWith(Flux.defer(() -> {
                    var map = ToolResultHolder.get(requestId);
                    if (CollectionUtil.isEmpty(map)) {
                        return Flux.just(STOP_EVENT);
                    }
                    ToolResultHolder.remove(requestId); // 清除参数列表
                    // 响应给前端的参数数据
                    return Flux.just(new ChatEventVO(map,ChatEventTypeEnum.PARAM.getValue()), STOP_EVENT);
                }));
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
    public void stop(String sessionId) {
        var outputHash = stringRedisTemplate.boundHashOps(OUTPUT_STATUS);
        // 移除标记
        outputHash.delete(sessionId);
    }

}
