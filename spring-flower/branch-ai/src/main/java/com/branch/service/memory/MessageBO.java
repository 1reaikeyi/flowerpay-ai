package com.branch.service.memory;

import lombok.Data;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.content.Media;

import java.util.List;
import java.util.Map;

@Data
public class MessageBO {
    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 元数据
     */
    private Map<String, Object> metadata = Map.of();
    /**
     * 媒体内容
     */
    private List<Media> media = List.of();
    /**
     * 工具调用
     */
    private List<AssistantMessage.ToolCall> toolCalls = List.of();
    /**
     * 文本内容
     */
    private String textContent;
    /**
     * 工具响应
     */
    private List<ToolResponseMessage.ToolResponse> toolResponses = List.of();
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 参数
     */
    private Map<String, Object> params = Map.of();

}
