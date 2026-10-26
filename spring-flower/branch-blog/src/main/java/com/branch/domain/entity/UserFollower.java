package com.branch.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注表 - 对应数据库 user_follower 表
 */
@Data
@TableName("user_follower")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFollower implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联的用户 id（被关注用户）
     */
    @TableField("follow_user_id")
    private Long followUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
