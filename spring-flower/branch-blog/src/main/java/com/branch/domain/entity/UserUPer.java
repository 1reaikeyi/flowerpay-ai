package com.branch.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 博主表 - 对应数据库 user_info 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_uper")
@Builder
public class UserUPer implements Serializable {

    /**
     * 主键，用户 id
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 城市名称
     */
    @TableField("city")
    private String city;

    /**
     * 个人介绍，不要超过 128 个字符
     */
    @TableField("introduce")
    private String introduce;

    /**
     * 粉丝数量
     */
    @TableField("fans")
    private Long fans;

    /**
     * 关注的人的数量
     */
    @TableField("followee")
    private Long followee;

    /**
     * 性别，0：男，1：女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 生日
     */
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 积分
     */
    @TableField("credits")
    private Long credits;

    /**
     * 会员级别，0~9级，0代表未开通会员
     */
    @TableField("level")
    private Integer level;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
