package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.common.constant.ErrorConstant;
import com.branch.common.constant.RedisPrefixConstant;
import com.branch.domain.dto.ArticleDTO;
import com.branch.domain.vo.ArticleVO;
import com.branch.domain.wrapper.LogicData;
import com.branch.mapper.ArticleMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import com.branch.domain.entity.Article;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.branch.service.ArticleService;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 分类服务实现类 - 实现分类相关业务逻辑
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private Cache<String, Object> articleCache;

    // 时间统一使用s结算
    private static final long FLASH_CACHE_TTL = 30L;
    private static final long NEED_FLASH_CACHE_TTL = FLASH_CACHE_TTL / 10;
    private static final long REDIS_EXIST_TTL = 86400L;
    private static final Random RANDOM = new Random();

    @Override
    public ArticleDTO create(ArticleDTO articleDTO) {
        Article article = BeanUtil.toBean(articleDTO, Article.class);
        this.save(article);
        ArticleDTO newArticle = BeanUtil.toBean(articleDTO, ArticleDTO.class);
        return newArticle;
    }

    @Override
    public ArticleVO readCache(Long id) {
        ArticleVO articleVO = logicalCache(id);
        return articleVO;

    }

    private ArticleVO logicalCache(Long id) {
        String key = RedisPrefixConstant.ARTICLE_PREFIX + id;
        //1直接从缓存中获取数据
        String value;

        ArticleVO localVo= (ArticleVO) articleCache.getIfPresent(key);
        if (localVo != null) {
            return localVo;
        }
        //2缓存不存在
        LogicData logicData = new LogicData();
        try {
            value = stringRedisTemplate.opsForValue().get(RedisPrefixConstant.ARTICLE_PREFIX + id);
            if (StrUtil.isBlank(value)) {
                Article article = this.getCache(id);
                ArticleVO articleVO = article == null ? null : BeanUtil.toBean(article, ArticleVO.class);
                articleCache.put(key, articleVO);
                return articleVO;
            }
            logicData = JSONUtil.toBean(value, LogicData.class);
        } catch (Exception e) {
            log.info("Redis 宕机:{}", e.getMessage());
            // Redis 不可用：降级直查数据库
            Article article = this.getMysql(id);
            ArticleVO articleVO = article == null ? null : BeanUtil.toBean(article, ArticleVO.class);
            articleCache.put(key, articleVO);
            return articleVO;
        }
        //3 缓存存在
        if(logicData.getData() == null){
            return null;
        }
//        if(logicData.getData() != null){}
        ArticleVO old = null;
        old = BeanUtil.toBean(logicData.getData(), ArticleVO.class);
        if (logicData.getExpireTime().isAfter(LocalDateTime.now())) {
            long remainSec = Duration.between(LocalDateTime.now(), logicData.getExpireTime()).getSeconds();
            if (remainSec < NEED_FLASH_CACHE_TTL) {
                logicData.setExpireTime(LocalDateTime.now().plusSeconds(FLASH_CACHE_TTL));
                stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(logicData),
                        REDIS_EXIST_TTL, TimeUnit.SECONDS);
            }
            log.info("缓存没有过期------------");
            ArticleVO vo = BeanUtil.toBean(logicData.getData(), ArticleVO.class);
            articleCache.put(key, vo);
            return vo;
        }
        //逻辑过期：重建缓存
        log.info("festival缓存过期-------------");
        this.getRedis(id);
        articleCache.put(key, old);
        return old;
    }
    private Article getCache(Long id) {
        String lockKey = "article:lock:" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("Redis 宕机:{}", e.getMessage());
            return getMysql(id);
        }
        return null;
    }
    private Article getMysql(Long id) {
        Article article = this.getById(id);
        if (article == null) {}
        return null;
    }
    private void getRedis(Long id) {

    }
    @Override
    public void updateCache(ArticleDTO articleDTO) {
        if (articleDTO.getId() == null) {
            throw new IllegalArgumentException(ErrorConstant.OPERATION_ERROR);
        }
        Article article = BeanUtil.toBean(articleDTO, Article.class);
        this.updateById(article);
        articleCache.invalidate(RedisPrefixConstant.ARTICLE_PREFIX + article.getId());
        stringRedisTemplate.delete(RedisPrefixConstant.ARTICLE_PREFIX+article.getId());
    }

    @Override
    public void deleteCache(List<Long> ids) {
        for (Long id : ids) {
            articleCache.invalidate(RedisPrefixConstant.ARTICLE_PREFIX + id);
            stringRedisTemplate.delete(RedisPrefixConstant.ARTICLE_PREFIX+id);
        }
    }
}