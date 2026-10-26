package com.branch.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class BlogDTO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 商户ID
     */
    private Long shopId;

    /**
     * 探店笔记标题
     */
    private String title;

    /**
     * 探店照片地址，最多9张，多图逗号分隔
     */
    private String images;

    /**
     * 探店文字描述内容
     */
    private String content;

}
