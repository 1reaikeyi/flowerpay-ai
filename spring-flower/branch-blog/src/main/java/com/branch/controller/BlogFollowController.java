package com.branch.controller;


import com.branch.domain.dto.BlogSelfDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.branch.service.BlogFollowerService;
import com.branch.result.Result;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class BlogFollowController {
    @Autowired
    private BlogFollowerService blogFollowerService;

    /**
     * 关注
     */
    @PostMapping("/{id}/{follow}")
    public Result useFollow(@PathVariable("id") Long followUserId, @PathVariable Boolean follow) {
        return Result.success(blogFollowerService.follow(followUserId, follow));
    }

    @GetMapping("/{id}")
    public Result getUserFollow(@PathVariable("id") Long followUserId) {
        return Result.success(blogFollowerService.followStatus(followUserId));
    }

    /**
     * 共同关注
     */
    @GetMapping("/common/{id}")
    public Result getUserFollowCommon(@PathVariable("id") Long followId) {
        List<String> common = blogFollowerService.commonFollow(followId);
        if (common == null) {
            return Result.success(null);
        }
        return Result.success(common);
    }
}
