package service.rag;

import model.vo.ChatEventVO;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface Chat {
    /**
     * 获取对话id，
     *
     * @param sessionId 会话id
     * @return 对话id
     */
    static String getConversationId(String sessionId) {
        return sessionId;
    }

    /**
     * chat
     *
     * @param question  问题
     * @param sessionId 会话id
     * @return 回答内容
     */
    Flux<ChatEventVO> chat(String question, String sessionId);
    /**
     * 停止生成
     *
     * @param sessionId 会话id
     */
    void stop(String sessionId);

    EmbeddingResponse embedForResponse(List<String> message);

    void deleteById(List<String> ids);

    List<Document> searchMatch(String message);

    Map<String, Object> searchAll(String prefix);
}

