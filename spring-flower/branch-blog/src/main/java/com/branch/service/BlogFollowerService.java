package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.entity.BlogFollower;


import java.util.List;

public interface BlogFollowerService extends IService<BlogFollower> {

    /**
     * 关注 / 取关目标用户
     *
     * @return 操作结果描述
     */
    String follow(Long followUserId, Boolean ifFollow);

    /**
     * 当前用户是否已关注目标用户
     *
     * @return "关注" / "取关"
     */
    String followStatus(Long followUserId);

    /**
     * 当前用户与目标用户的共同关注列表
     *
     * @return 共同关注用户列表，无数据返回 null
     */
    List<String> commonFollow(Long followId);
}
