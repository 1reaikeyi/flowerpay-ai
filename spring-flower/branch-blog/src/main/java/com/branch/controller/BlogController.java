package com.branch.controller;

import com.branch.model.dto.BlogDTO;

import com.branch.model.dto.BlogPageDTO;
import com.branch.model.vo.BlogVO;
import com.branch.result.PagePart;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.branch.service.BlogService;
import com.branch.result.Result;
import com.branch.result.ScrollPart;

@RestController
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    @Autowired
    private BlogService blogService;

    @PostMapping
    public Result postBlog(@RequestBody BlogDTO blogDTO) {
        BlogDTO dto = blogService.postBlog(blogDTO);
        return Result.success(dto);
    }

    @GetMapping("/{id}")
    public Result readBlogById(@PathVariable Long id) {
        return Result.success(blogService.getById(id));
    }

    @GetMapping("/all")
    public Result readBlog(@Validated BlogPageDTO blogPageDTO) {
        PagePart<BlogVO> pagePart = blogService.readAll(blogPageDTO);
        return Result.success(pagePart);
    }

    @PostMapping("/liked")
    public Result liked(@RequestBody Long id) {
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

    @GetMapping("/liked/of/follow")
    public Result follow(@RequestParam(required = false) Long max, Long offset) {
        ScrollPart scrollPart = blogService.followScroll(max, offset);
        return Result.success(scrollPart);
    }
}
