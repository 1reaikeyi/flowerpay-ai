package com.branch.controller;


import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.branch.controller.graph.NodeLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/see")
public class SeeController {
    @Autowired
    private NodeLink nodeLink;

    @PostMapping
    public Object flow(@RequestParam String question, @RequestParam MultipartFile file,String sessionId) throws Exception {
        if(file == null){
            return "文件不能为空";
        }
        CompiledGraph compiledGraph = nodeLink.toSee();

        String fileBase64 = Base64.getEncoder().encodeToString(file.getBytes());
        return compiledGraph.invoke(Map.of("file", fileBase64, "question", question,"sessionId", sessionId))
                .map(state -> "第一个"+state.value("visualResult").orElse("null") +
                        "==>第二个" + state.value("toolResult").orElse("null"))
                .orElse("执行内部有错误");
    }
}
