package start.controller;

import common.result.Result;
import model.dto.ChatDTO;
import start.vo.ChatEventVO;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import service.rag.Chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;


@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private Chat chatService;
    /**
     * 对话
     * @param chatDTO
     * @return
     */
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatEventVO> chat(@RequestBody ChatDTO chatDTO) {
        return chatService.chat(chatDTO.getQuestion(), chatDTO.getSessionId());
    }
    /**
     * stop_chat
     */
    @PostMapping("/stop")
    public void stop(@RequestParam String sessionId) {
        chatService.stop(sessionId);
    }
    //add
    @PostMapping("/embedding")
    public Result saveVectorStore(@RequestParam("messages") List<String> messages) {
        return Result.success("保存到向量数据库数量:"+messages.size());
    }
    //return 返回向量表示（通常是浮点数数组）
    @GetMapping("/embedding")
       public Result embedding(@RequestParam("message") String message) {
        EmbeddingResponse embeddingResponse = chatService.embedForResponse(List.of(message));
        return Result.success(embeddingResponse);
    }
    //将查询文本向量化后，在向量数据库中查找最相似的文档,最大topk
    @PostMapping("/search/match")
    public Result searchMatch(@RequestParam("message") String message) {
        List<Document> list = chatService.searchMatch(message);
        return Result.success(list);
    }
    //向量数据库删除
    @DeleteMapping
    public Result deleteVectorStore(@RequestParam("ids") List<String> ids) {
        // 删除向量数据库中的数据
        chatService.deleteById(ids);
        return Result.success(ids);
    }


}
