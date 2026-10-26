package com.branch.controller;

import com.branch.domain.dto.BlogDTO;

import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.branch.service.BlogService;
import com.branch.result.Result;
import com.branch.result.ScrollResult;

@RestController
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    @Autowired
    private BlogService blogService;

    @PostMapping
    public Result createBlog(@RequestBody BlogDTO blogDTO) {
        Long blogId = blogService.createBlog(blogDTO);
        return Result.success("createBlog::" + blogId);
    }

    @GetMapping("/{id}")
    public Result readBlogById(@PathVariable Long id) {
        return Result.success(blogService.getById(id));
    }

    @GetMapping("/all")
    public Result readBlog() {
        return Result.success(blogService.list());
    }

    @PostMapping("/liked")
    public Result isliked(@RequestBody Long id) {
        return Result.success(blogService.toggleLike(id));
    }

    @GetMapping("liked/of/{id}")
    public Result likedOf(@PathVariable long id) {
        return Result.success(blogService.isLiked(id));
    }

    @GetMapping("liked/of/all")
    public Result likedOfAll(@PathParam("id") long id) {
        return Result.success(blogService.likedAll(id));
    }

    @GetMapping("/liked/of/user")
    public Result likedHot(@PathParam("id") long id) {
        Object hot = blogService.likedHot(id);
        if (hot == null) {
            return Result.error(null);
        }
        return Result.success(hot);
    }

    /**
     * blog 收件箱
     */
    @GetMapping("/follow/of/all")
    public Result follow(@RequestParam(required = false) Long max, Long offset) {
        ScrollResult scrollResult = blogService.followScroll(max, offset);
        return Result.success(scrollResult);
    }
}
