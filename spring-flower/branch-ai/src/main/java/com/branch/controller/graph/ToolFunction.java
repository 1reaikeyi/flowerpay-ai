package com.branch.controller.graph;

import cn.hutool.core.lang.UUID;
import com.branch.service.tool.ToolService;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ToolFunction implements NodeAction {

    @Autowired
    private ToolService toolService;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Object sessionId = state.value("sessionId").orElse(UUID.randomUUID(true));
        Object visualValue = state.value("visualResult").orElse(null);
        Object questionValue = state.value("question").orElse(null);

        String result = toolService.chat(visualValue.toString(), questionValue.toString(), sessionId.toString())
                .map(event -> String.valueOf(event.getEventData()))
                .collect(Collectors.joining())
                .timeout(Duration.ofSeconds(30))
                .blockOptional(Duration.ofSeconds(40))
                .toString();

        return Map.of("toolResult", result);
    }

}
