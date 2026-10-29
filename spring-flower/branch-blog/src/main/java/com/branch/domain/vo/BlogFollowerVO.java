package com.branch.domain.vo;

import lombok.Data;

@Data
public class BlogFollowerVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 关联的用户 id（被关注用户）
     */
    private Long followUserId;
}
