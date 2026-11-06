package com.branch.domain.bo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class RedisIdBO {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 基准时间：2020-01-01 00:00:00 UTC
     private final static long BEGIN_TIME = 1577836800L;
    // 32位序号最大值
     private static final long MAX_SEQ = 0xFFFFFFFFL;
     public long getId(String prefix){
         ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
         long time = now.toEpochSecond();
         long seq = time - BEGIN_TIME;
         String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
         String key = "icr:" + prefix + ":" + date;
         long count = stringRedisTemplate.opsForValue().increment(key);
         return seq << 32 | count;
     }
}
