package com.branch.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.dto.TimeDTO;
import com.branch.domain.entity.Sign;
import com.branch.mapper.SignMapper;
import com.branch.service.SignService;


import com.branch.common.constant.RedisPrefixConstant;
import framework.security.SecurityContextParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign> implements SignService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String sign() {
        LocalDateTime now = LocalDateTime.now();
        Long userId = SecurityContextParam.getCurrentUserId();
        String key = RedisPrefixConstant.SIGN_DAY + userId + ":" + now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Long day = Long.valueOf(now.getDayOfMonth() - 1);
        Boolean result = stringRedisTemplate.opsForValue().setBit(key, day, true);
        return result ? "已签到" : "签到成功";
    }

    @Override
    public String backSign(TimeDTO time) {
        LocalDate localDate = LocalDate.parse(time.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Long userId = SecurityContextParam.getCurrentUserId();
        String key = RedisPrefixConstant.SIGN_DAY + userId + ":" + localDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Long value = Long.valueOf(localDate.getDayOfMonth() - 1);
        Boolean result = stringRedisTemplate.opsForValue().setBit(key, value, true);
        return result ? "已补签" : "补签成功";
    }

    @Override
    public Object countOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        Long userId = SecurityContextParam.getCurrentUserId();
        String key = RedisPrefixConstant.SIGN_DAY + userId + ":" + now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        // bitfield key get u8 0
        List<Long> result = stringRedisTemplate.opsForValue().bitField(key, BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(now.getDayOfMonth()))
                .valueAt(0));
        if (CollectionUtil.isEmpty(result)) {
            return 0;
        }
        Long num10 = result.get(0);
        long signedDays = 0;
        for (int i = 0; i < now.getDayOfMonth(); i++) {
            if ((num10 & 1) == 1) {
                signedDays++;
            }
            num10 = num10 >>> 1;
        }
        long unSignedDays = now.getDayOfMonth() - signedDays;
        return "签到::" + signedDays + ",缺勤::" + unSignedDays;
    }

    @Override
    public Object countOfTime(TimeDTO time) {

        return null;
    }

}
