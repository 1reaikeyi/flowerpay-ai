package com.branch.domain.dto;

import lombok.Data;

@Data
public class BlogFollowerDTO {
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
