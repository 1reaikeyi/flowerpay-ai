package com.branch.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ArticleDetailDTO {
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
     * 文章状态：只能是 [已发布] 或者 [草稿]
     */
    private String state;

    /**
     * 文章分类ID，关联 category.id
     */
    private Long categoryId;
}
