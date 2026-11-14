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
 * 签到表 - 对应数据库 user_sign 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_sign")
@Builder
public class Sign implements Serializable {

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
     * 签到的年
     */
    @TableField("year")
    private Long year;

    /**
     * 签到的月
     */
    @TableField("month")
    private Long month;

    /**
     * 签到次数
     */
    @TableField("signed")
    private Long signed;

    /**
     * 缺勤次数
     */
    @TableField("not_signed")
    private Long notSigned;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
