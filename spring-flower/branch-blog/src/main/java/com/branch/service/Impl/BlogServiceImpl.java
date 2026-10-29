package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.common.constant.RedisPrefixConstant;
import com.branch.domain.dto.BlogPageDTO;
import com.branch.domain.entity.BlogFollower;
import com.branch.domain.vo.BlogVO;
import com.branch.result.PageResult;
import com.branch.service.BlogFollowerService;

import com.branch.domain.entity.Blog;
import framework.security.SecurityContextParam;
import com.branch.mapper.BlogMapper;
import com.branch.domain.dto.BlogDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.branch.service.BlogService;
import com.branch.result.ScrollResult;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BlogFollowerService blogFollowerService;


    /**
     * 发布博客：保存博客并推送到所有粉丝的收件箱
     *
     * @param blogDTO
     * @return 博客ID
     */
    @Override
    public BlogDTO postBlog(BlogDTO blogDTO) {
        Blog blog = BeanUtil.toBean(blogDTO, Blog.class);
        super.save(blog);
        // 发送给所有粉丝的收件箱
        Long userId = blog.getUserId();
        List<BlogFollower> userFollows = blogFollowerService.lambdaQuery()
                .eq(BlogFollower::getUserId,userId).list();
        for (BlogFollower userFollow : userFollows) {
            String key = RedisPrefixConstant.BLOG_FOLLOW_PREFIX + userFollow.getFollowUserId();
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        return BeanUtil.toBean(blog, BlogDTO.class);
    }
     @Override
    public PageResult<BlogVO> readAll(BlogPageDTO blogPageDTO) {
        IPage page = new Page(blogPageDTO.getPage(), blogPageDTO.getPageSize());
        IPage<Blog> blogPage = this.page(page);
        List<BlogVO> blogDTOList = blogPage.getRecords().stream()
                .map(b -> BeanUtil.toBean(b,BlogVO.class))
                .collect(Collectors.toList());
        PageResult<BlogVO> pageResult = new PageResult<>();
        pageResult.setList(blogDTOList);
        pageResult.setTotal(blogPage.getTotal());
        pageResult.setPageSize(blogPage.getSize());
        pageResult.setPageNum(blogPage.getCurrent());
        return pageResult;
    }

    @Override
    public String toggleLike(Long id) {
        Long userId = SecurityContextParam.getCurrentUserId();
        String key = RedisPrefixConstant.BLOG_FOLLOW_PREFIX + userId;
        Double liked = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (liked == null) {
            // 没点赞
            boolean success = this.lambdaUpdate()
                    .setSql("liked= liked + 1").eq(Blog::getId, id).update();
            stringRedisTemplate.opsForZSet().add(key,
                    userId.toString(),
                    System.currentTimeMillis());
            return "liked::" + id;
        } else {
            // 点赞过
            boolean success = super.lambdaUpdate()
                    .setSql("liked= liked - 1").eq(Blog::getId, id).update();
            stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            return "unliked::" + id;
        }
    }

    @Override
    public Boolean isLiked(Long id) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Double liked = stringRedisTemplate.opsForZSet().score(RedisPrefixConstant.BLOG_LIKED_PREFIX + id, userId.toString());
        return liked != null;
    }

    @Override
    public Set<String> likedAll(Long id) {
        return stringRedisTemplate.opsForZSet().range(RedisPrefixConstant.BLOG_LIKED_PREFIX+ id, 0, -1);
    }

    @Override
    public ScrollResult followScroll(Long offset,Long current) {
        if (current == null) {
            current = System.currentTimeMillis();
        }
        Long userId = SecurityContextParam.getCurrentUserId();
        Set<ZSetOperations.TypedTuple<String>> result = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(
                RedisPrefixConstant.BLOG_FOLLOW_PREFIX + userId, 0, current, offset, 10);
        if (CollectionUtil.isEmpty(result)) {
            return null;
        }
        List<Long> ids = new ArrayList<>();
        long minTime = 0;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : result) {
            ids.add(Long.parseLong(typedTuple.getValue()));
            Long time = typedTuple.getScore().longValue();
            if (minTime == time) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }
        List<Blog> blogList = this.listByIds(ids);
        ScrollResult next = new ScrollResult();
        next.setList(blogList);
        next.setMinTime(minTime);
        next.setOffset(Long.valueOf(os));
        return next;
    }

   
}
