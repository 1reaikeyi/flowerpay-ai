package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.common.constant.RedisPrefixConstant;
import com.branch.domain.dto.BlogSelfDTO;
import com.branch.domain.entity.BlogFollower;
import com.branch.domain.entity.BlogSelf;
import com.branch.mapper.BlogFollowMapper;
import com.branch.service.BlogSelfService;
import framework.security.SecurityContextParam;
;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.branch.service.BlogFollowerService;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlogFollowerServiceImpl extends ServiceImpl<BlogFollowMapper, BlogFollower> implements BlogFollowerService {



    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BlogSelfService blogSelfService;

    @Override
    public String follow(Long followUserId, Boolean ifFollow) {
        Long userId = SecurityContextParam.getCurrentUserId();
        String key = RedisPrefixConstant.BLOG_WATCH_PREFIX + userId;
        if (ifFollow) {
            BlogFollower userFollow = BlogFollower.builder()
                    .userId(userId)
                    .followUserId(followUserId)
                    .build();
            stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            this.save(userFollow);
        }
        if (!ifFollow) {
            super.remove(new LambdaQueryWrapper<BlogFollower>()
                    .eq(BlogFollower::getFollowUserId, userId)
                    .eq(BlogFollower::getFollowUserId, followUserId));
            stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
        }
        return followUserId + "::" + (BooleanUtil.isTrue(ifFollow) ? "关注" : "取关");
    }

    @Override
    public String followStatus(Long followUserId) {
        Long userId = SecurityContextParam.getCurrentUserId();
        BlogFollower userFollow = super.getOne(new LambdaQueryWrapper<BlogFollower>()
                .eq(BlogFollower::getUserId, userId)
                .eq(BlogFollower::getFollowUserId, followUserId));
        return userFollow != null ? "关注" : "取关";
    }

    @Override
    public List<String> commonFollow(Long followId) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Set<String> commonSet = stringRedisTemplate.opsForSet().intersect(RedisPrefixConstant.BLOG_WATCH_PREFIX + userId,
                RedisPrefixConstant.BLOG_WATCH_PREFIX + userId + userId);
        if (CollectionUtil.isEmpty(commonSet)) {
            return null;
        }
        List<Long> ids = commonSet.stream().map(s -> Long.parseLong(s)).toList();
        List<BlogSelf> blogSelfList = blogSelfService.listByIds(ids);
        return blogSelfList.stream()
                .map(b -> b.getUserId().toString())
                .collect(Collectors.toList());
    }
}
