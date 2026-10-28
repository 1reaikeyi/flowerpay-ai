package com.branch.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.branch.service.BlogCommentsService;
import com.branch.result.Result;

@RestController
@RequestMapping("/blog/comments")
public class BlogCommentsController {
    @Autowired
    private BlogCommentsService blogCommentsService;

    /**
     * 对博客发表评论（parent_id=0）
     */
    @PostMapping("/0/{id}")
    public Result createBlog(@PathVariable("id") Long id, @RequestBody String content) {
        return Result.success(blogCommentsService.createBlog(id, content));
    }

    /**
     * 回复评论（parent_id=1，answer_id 指向目标评论）
     */
    @PostMapping("/1/{id}")
    public Result createBlogComment(@PathVariable("id") Long id, @RequestBody String content) {
        return Result.success(blogCommentsService.createBlogComment(id, content));
    }

    /**
     * 查询某博客的全部评论（按时间倒序）
     */
    @GetMapping("/of/{id}")
    public Result listByBlog(@PathVariable("id") Long id) {
        return Result.success(blogCommentsService.listByBlog(id));
    }

    @PostMapping("/view")
    public Result view(@RequestBody Long id) {
        blogCommentsService.view(id);
        return Result.success("view+1");
    }

    @GetMapping("/view/of/{id}")
    public Result count(@PathVariable Long id) {
        return Result.success(blogCommentsService.viewCount(id));
    }
}
