package com.branch.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章视图对象
 */
@Data
public class ArticleDetailVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章封面
     */
    private String coverImg;

    /**
     * 文章状态：已发布 / 草稿
     */
    private String state;

    /**
     * 文章分类ID
     */
    private Long categoryId;

    /**
     * 分类名称（联表 article.category_name 冗余返回，便于列表展示）
     */
    private String categoryName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
