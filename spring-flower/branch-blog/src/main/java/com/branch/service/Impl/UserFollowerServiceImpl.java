package com.branch.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.entity.UserFollower;
import com.branch.mapper.UserFollowMapper;
import framework.security.SecurityContextParam;
;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.branch.service.UserFollowerService;


import java.util.List;
import java.util.Set;

@Service
public class UserFollowerServiceImpl extends ServiceImpl<UserFollowMapper, UserFollower> implements UserFollowerService {

    private static final String FOLLOW_PREFIX = "follow:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String follow(Long followUserId, Boolean ifFollow) {
        Long userId = SecurityContextParam.getCurrentUserId();
        if (ifFollow) {
            UserFollower userFollow = UserFollower.builder()
                    .userId(userId)
                    .followUserId(followUserId)
                    .build();
            stringRedisTemplate.opsForSet().add(FOLLOW_PREFIX + userId, followUserId.toString());
            super.save(userFollow);
        }
        if (!ifFollow) {
            super.remove(new LambdaQueryWrapper<UserFollower>()
                    .eq(UserFollower::getFollowUserId, userId)
                    .eq(UserFollower::getFollowUserId, followUserId));
            stringRedisTemplate.opsForSet().remove(FOLLOW_PREFIX + userId, followUserId.toString());
        }
        return followUserId + "::" + (BooleanUtil.isTrue(ifFollow) ? "关注" : "取关");
    }

    @Override
    public String followStatus(Long followUserId) {
        Long userId = SecurityContextParam.getCurrentUserId();
        UserFollower userFollow = super.getOne(new LambdaQueryWrapper<UserFollower>()
                .eq(UserFollower::getUserId, userId)
                .eq(UserFollower::getFollowUserId, followUserId));
        return userFollow != null ? "关注" : "取关";
    }

    @Override
    public List<?> commonFollow(Long followId) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Set<String> commonSet = stringRedisTemplate.opsForSet().intersect(FOLLOW_PREFIX + followId, FOLLOW_PREFIX + userId);
        if (CollectionUtil.isEmpty(commonSet)) {
            return null;
        }
        List<Long> ids = commonSet.stream().map(s -> Long.parseLong(s)).toList();
//        List<User> userList = userService.listByIds(ids);
//        return userList;
        return null;
    }
}
