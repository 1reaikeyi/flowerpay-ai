package com.branch.service.tool;

import com.branch.domain.vo.ChatEventVO;
import reactor.core.publisher.Flux;

public interface ToolService {
    static String getConversationId(String sessionId) {
        return sessionId;
    }
    Flux<ChatEventVO> chat(String visualValue, String questionm, String sessionId);
}
