package com.branch.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.branch.service.UserFollowerService;
import com.branch.result.Result;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class UseFollowController {
    @Autowired
    private UserFollowerService userFollowerService;

    /**
     * 关注
     */
    @PostMapping("/{id}/{ifFollow}")
    public Result useFollow(@PathVariable("id") Long followUserId, @PathVariable Boolean ifFollow) {
        return Result.success(userFollowerService.follow(followUserId, ifFollow));
    }

    /**
     * 获取结果
     */
    @GetMapping("/{id}")
    public Result getUserFollow(@PathVariable("id") Long followUserId) {
        return Result.success(userFollowerService.followStatus(followUserId));
    }

    /**
     * 共同关注
     */
    @GetMapping("/common/{id}")
    public Result getUserFollowCommon(@PathVariable("id") Long followId) {
        List<?> common = userFollowerService.commonFollow(followId);
        if (common == null) {
            return Result.success(null);
        }
        return Result.success(common);
    }
}
