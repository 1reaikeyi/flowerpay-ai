package com.branch.controller.graph;

import com.branch.service.visual.VisualService;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class VisualFunction implements NodeAction {

    @Autowired
    private VisualService visualService;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String file = (String) state.value("file").orElse(("文件为空"));
        String sessionId = (String) state.value("sessionId").orElse("");
        Media media = new Media(MimeTypeUtils.IMAGE_JPEG, URI.create("data:image/jpeg;base64," + file));

        String result = visualService.chat(media,sessionId)
                .collect(Collectors.joining())
                .timeout(Duration.ofSeconds(30))
                .blockOptional(Duration.ofSeconds(40))
                .toString();
        return Map.of("visualResult", result);
    }

}