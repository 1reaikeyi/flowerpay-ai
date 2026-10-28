package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.entity.BlogComments;

import java.util.List;


public interface BlogCommentsService extends IService<BlogComments> {

    /**
     * 记录一次博客浏览（HyperLogLog 去重计数）
     */
    void view(Long id);

    /**
     * 查询博客浏览量
     */
    Long viewCount(Long id);

    /**
     * 对博客发表评论（parent_id=0）
     *
     * @param blogId  博客ID
     * @param content 评论内容
     * @return 新评论ID
     */
    Long createBlog(Long blogId, String content);

    /**
     * 回复评论（parent_id=1，answer_id 指向目标评论）
     *
     * @param commentId 目标评论ID
     * @param content   评论内容
     * @return 新评论ID
     */
    Long createBlogComment(Long commentId, String content);

    /**
     * 查询某博客的全部评论（按时间倒序）
     */
    List<BlogComments> listByBlog(Long blogId);
}
