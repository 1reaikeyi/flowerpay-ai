package com.branch.service.Impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.common.constant.RedisPrefixConstant;
import com.branch.common.exception.BaseException;
import com.branch.domain.entity.Blog;
import com.branch.mapper.BlogCommentsMapper;
import com.branch.service.BlogCommentsService;
import com.branch.domain.entity.BlogComments;
import com.branch.service.BlogService;
import framework.security.SecurityContextParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements BlogCommentsService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BlogService blogService;

    @Override
    public Long createBlog(Long blogId, String content) {
        Long userId = SecurityContextParam.getCurrentUserId();
        BlogComments comment = BlogComments.builder()
                .userId(userId)
                .blogId(blogId)
                .parentId(0L)
                .answerId(0L)
                .content(content)
                .liked(0L)
                .status(0L)
                .build();
        super.save(comment);
        // 博客评论总数 +1
        blogService.lambdaUpdate().setSql("comments = comments + 1").eq(Blog::getId, blogId).update();
        return comment.getId();
    }

    @Override
    public Long createBlogComment(Long commentId, String content) {
        BlogComments parent = super.getById(commentId);
        if (parent == null) {
            throw new BaseException("目标评论不存在");
        }
        Long userId = SecurityContextParam.getCurrentUserId();
        BlogComments comment = BlogComments.builder()
                .userId(userId)
                .blogId(parent.getBlogId())
                .parentId(1L)
                .answerId(commentId)
                .content(content)
                .liked(0L)
                .status(0L)
                .build();
        super.save(comment);
        // 博客评论总数 +1
        blogService.lambdaUpdate().setSql("comments = comments + 1").eq(Blog::getId, parent.getBlogId()).update();
        return comment.getId();
    }

    @Override
    public List<BlogComments> listByBlog(Long blogId) {
        return lambdaQuery()
                .eq(BlogComments::getBlogId, blogId)
                .orderByDesc(BlogComments::getCreateTime)
                .list();
    }

    @Override
    public void view(Long id) {
        Long userId = SecurityContextParam.getCurrentUserId();
        stringRedisTemplate.opsForHyperLogLog().add(RedisPrefixConstant.BLOG_VIEW_PREFIX + id, userId.toString());
    }

    @Override
    public Long viewCount(Long id) {
        return stringRedisTemplate.opsForHyperLogLog().size(RedisPrefixConstant.BLOG_VIEW_PREFIX + id);
    }

}
