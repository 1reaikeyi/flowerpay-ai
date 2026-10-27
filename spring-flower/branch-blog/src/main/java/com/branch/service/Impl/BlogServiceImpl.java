package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.entity.UserFollower;
import com.branch.result.PageResult;
import com.branch.service.UserFollowerService;

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

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    private static final String BLOG_LIKED_PREFIX = "blog:liked:";
    private static final String BLOG_FOLLOW_PREFIX = "blog:follow:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserFollowerService userFollowerService;


    public Long createBlog(BlogDTO blogDTO) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Blog blog = BeanUtil.toBean(blogDTO, Blog.class);
        blog.setUserId(userId);
        super.save(blog);
        // 发送给 follower 的收件箱
        List<UserFollower> userFollows = null;
//                = userFollowerService.list(new LambdaQueryWrapper<>()
//                .eq(UserFollower::getFollowUserId, userId));
        for (UserFollower userFollow : userFollows) {
            String key = BLOG_FOLLOW_PREFIX + userFollow.getUserId();
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        return blog.getId();
    }

    /**
     * 发布博客：保存博客并推送到所有粉丝的收件箱
     *
     * @param blogDTO
     * @return 博客ID
     */
    @Override
    public Long postBlog(BlogDTO blogDTO) {
        return null;
    }

    @Override
    public String toggleLike(Long id) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Double liked = stringRedisTemplate.opsForZSet().score(BLOG_LIKED_PREFIX + id, userId.toString());
        if (liked == null) {
            // 没点赞
            boolean success = super.lambdaUpdate()
                    .setSql("liked= liked + 1").eq(Blog::getId, id).update();
            if (success) {
                stringRedisTemplate.opsForZSet().add(BLOG_LIKED_PREFIX + id, userId.toString(), System.currentTimeMillis());
            }
            return "liked::" + id;
        } else {
            // 点赞过
            boolean success = super.lambdaUpdate()
                    .setSql("liked= liked - 1").eq(Blog::getId, id).update();
            if (success) {
                stringRedisTemplate.opsForZSet().remove(BLOG_LIKED_PREFIX + id, userId.toString());
            }
            return "unliked::" + id;
        }
    }

    @Override
    public Boolean isLiked(Long id) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Double liked = stringRedisTemplate.opsForZSet().score(BLOG_LIKED_PREFIX + id, userId.toString());
        return liked != null;
    }

    @Override
    public Set<String> likedAll(Long id) {
        return stringRedisTemplate.opsForZSet().range(BLOG_LIKED_PREFIX + id, 0, -1);
    }

    /**
     * 博客点赞的Top3用户
     *
     * @param id
     * @return 用户列表，无数据返回 null
     */
    @Override
    public Object likedTop(long id) {
        return null;
    }


    public Object likedHot(Long id) {
        Set<String> set = stringRedisTemplate.opsForZSet().range(BLOG_LIKED_PREFIX + id, 0, 2);
        if (CollectionUtil.isEmpty(set)) {
            return null;
        }
        List<Long> ids = set.stream().map(Long::parseLong).toList();
//        return userService.listByIds(ids);
        return null;
    }

    @Override
    public ScrollResult followScroll(Long max, Long offset) {
        if (max == null) {
            max = System.currentTimeMillis();
        }
        Long userId = SecurityContextParam.getCurrentUserId();
        Set<ZSetOperations.TypedTuple<String>> result = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(
                BLOG_FOLLOW_PREFIX + userId, 0, max, offset, 10);
        if (CollectionUtil.isEmpty(result)) {
            return null;
        }
        List<Long> ids = new ArrayList<>(result.size());
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
        List<Blog> blogList = super.listByIds(ids);
        ScrollResult next = new ScrollResult();
        next.setList(blogList);
        next.setMinTime(minTime);
        next.setOffset(Long.valueOf(os));
        return next;
    }

    @Override
    public PageResult<BlogDTO> readAll() {
        return null;
    }
}
