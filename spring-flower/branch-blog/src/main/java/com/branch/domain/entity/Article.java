package com.branch.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章分类实体类 - 对应数据库 article 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("article")
@Builder
public class Article implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    @NotEmpty
    @TableField("category_name")
    private String categoryName;

    /**
     * 分类别名
     */
    @NotEmpty
    @TableField("category_alias")
    private String categoryAlias;

    /**
     * 文章状态（瞬态字段，非 article 表字段，仅供 Service 层按状态查询时使用；
     * 真实状态位于 article_detail.state）
     */
    @TableField(exist = false)
    private String state;

    /**
     * 创建人ID，关联 user.id
     */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 更新人
     */
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
