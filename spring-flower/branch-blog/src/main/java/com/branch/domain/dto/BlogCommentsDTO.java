package com.branch.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class BlogCommentsDTO {
    /**
     * 评论主键自增ID
     */
    private Long id;

    /**
     * 评论发布用户ID
     */
    private Long userId;

    /**
     * 关联探店笔记ID
     */
    private Long blogId;

    /**
     * 回复 blog 评论值为 0，回复 blog_comments 评论值为 1
     */
    private Long parentId;

    /**
     * 回复目标评论ID
     */
    private Long answerId;

    /**
     * 评论文字内容
     */

    private String content;

    /**
     * 评论点赞数量
     */
    private Long liked;

    /**
     * 评论状态：0正常，1被举报，2禁止查看
     */
    private Long status;
}
