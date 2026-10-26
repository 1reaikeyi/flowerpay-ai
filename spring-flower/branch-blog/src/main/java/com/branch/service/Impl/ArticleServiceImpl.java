package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.dto.ArticleDTO;
import com.branch.domain.wrapper.LogicData;
import com.branch.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import com.branch.domain.entity.Article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.branch.service.ArticleService;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 分类服务实现类 - 实现分类相关业务逻辑
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    private final static String KEYS = "category:";
    private final static String LOCK_KEY = "category:lock";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ArticleDTO create(ArticleDTO articleDTO) {
        return null;
    }

    @Override
    public Article readCache(Long id) {
//        Article article = logicalCache(id);
//        return article;
        return null;
    }

//    private Article logicalCache(Long id) {
//        String key = KEYS + id;
//        //1 直接从缓存中获取数据
//        String categoryJson = stringRedisTemplate.opsForValue().get(key);
//        //2缓存不存在
//        if (StrUtil.isBlank(categoryJson)) {
//            Article article = super.getById(id);
//            LogicData logicData = new LogicData();
//            //没有id
//            if (article == null) {
//                logicData.setExpireTime(LocalDateTime.now().plusSeconds(30));
//                logicData.setData(null);
//                stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(logicData));
//                throw  new RuntimeException("id不存在");
//            }
//            //有id
//            logicData.setData(article);
//            logicData.setExpireTime(LocalDateTime.now().plusSeconds(5));
//            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(logicData));
//            return article;
//        }
//        //3 缓存存在
//        LogicData logicData = JSONUtil.toBean(categoryJson, LogicData.class);
//        LocalDateTime expireTime = logicData.getExpireTime();
//        //过期
//        if (expireTime.isBefore(LocalDateTime.now())) {
//            log.info("Category缓存过期");
//            Boolean success = cacheLocked();
//            if (success) {
//                try {
//                    Article article = super.getById(id);
//                    logicData.setExpireTime(LocalDateTime.now().plusSeconds(5));
//                    logicData.setData(article);
//                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(logicData));
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    if (success) {
//                        cacheUnlock();
//                    }
//                }
//            }else {
//                throw new RuntimeException("服务器繁忙，请稍后重试");
//            }
//        }
//        //正常
//        log.info("Category缓存正常");
//        Article article = BeanUtil.toBean(logicData.getData(), Article.class);
//        return article;
//    }

    @Override
    public Boolean updateCache(ArticleDTO article) {
//        boolean result = super.updateById(article);
//        stringRedisTemplate.delete(KEYS+ article.getId());
//        return result;
        return null;
    }

    @Override
    public Boolean deleteCache(List<Long> id) {
//        boolean result = super.removeById(id);
//        stringRedisTemplate.delete(KEYS+id);
//        return result;
        return null;
    }
}