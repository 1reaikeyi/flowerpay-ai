package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.branch.domain.dto.ArticleDetailDTO;
import com.branch.domain.wrapper.LogicData;
import com.branch.mapper.ArticleDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Service;
import com.branch.domain.entity.ArticleDetail;
import com.branch.service.ArticleDetailService;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 文章服务实现类 - 实现文章相关业务逻辑
 */
@Service
@Slf4j
public class ArticleDetailDetailServiceImpl extends ServiceImpl<ArticleDetailMapper, ArticleDetail> implements ArticleDetailService {

    private final static String KEYS = "article:";
    private final static String LOCK_KEY = "article:lock";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public ArticleDetail readCache(Long id) {
        ArticleDetail articleDetailMysql = logicCache(id);
        return articleDetailMysql;
    }
    private Boolean cacheLock(){
        //对应 set key xxx nx, key为空写入xxx,否则返回false
        return stringRedisTemplate.opsForValue().setIfAbsent(LOCK_KEY, "locked", 5, TimeUnit.SECONDS);
    }
    private void cacheUnlock(){
        stringRedisTemplate.delete(LOCK_KEY);
    }

    private ArticleDetail logicCache(Long id){
        String key = KEYS + id;
        //1 直接从缓存中获取数据
        String value = stringRedisTemplate.opsForValue().get(key);
        //2.1 缓存中没有数据
        if(StrUtil.isBlank(value)){
            // 3查询数据库中的数据
            ArticleDetail articleDetail = super.getById(id);
            LogicData logicData = new LogicData();
            if (articleDetail == null) {
                logicData.setData(null);
                logicData.setExpireTime(LocalDateTime.now().plusSeconds(30));
                stringRedisTemplate.opsForValue().set(KEYS + id, JSONUtil.toJsonStr(logicData));
                throw new RuntimeException("id不存在");
            }
            // 正常数据，redis实际不设置过期
            logicData.setData(articleDetail);
            logicData.setExpireTime(LocalDateTime.now().plusSeconds(5));
            stringRedisTemplate.opsForValue().set(KEYS + id, JSONUtil.toJsonStr(logicData));
            return articleDetail;
        }
        //2.2 缓存中存在数据
        LogicData logicData = JSONUtil.toBean(value, LogicData.class);
        //3 检查缓存是否过期
        //过期，从数据库中查询数据
        if (logicData.getExpireTime().isBefore(LocalDateTime.now())) {
            log.info("Article缓存出现过期");
            Boolean success = cacheLock();
            if(success) {
                try {
                    ArticleDetail articleDetail = super.getById(id);
                    logicData.setExpireTime(LocalDateTime.now().plusSeconds(5));
                    logicData.setData(articleDetail);
                    stringRedisTemplate.opsForValue().set(KEYS + id, JSONUtil.toJsonStr(logicData));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (success) {
                        cacheUnlock();
                    }
                }
            }else {
                throw new RuntimeException("服务器繁忙，请稍后重试");
            }
        }
        //正常，返回缓存中的数据
        log.info("Article缓存正常");
        ArticleDetail articleDetail = BeanUtil.toBean(logicData.getData(), ArticleDetail.class);
        return articleDetail;
    }

    @Override
    public Boolean updateCache(ArticleDetailDTO articleDetail) {
        String key = KEYS + articleDetail.getId();
        //1 数据库中的数据更新后，删除缓存中的数据
        boolean result = super.updateById(articleDetail);
        //2 删除缓存中的数据
        stringRedisTemplate.delete(key);
        return result;
    }

    @Override
    public Boolean deleteCache(Long id) {
        String key = KEYS + id;
        //1 删除数据库中的数据
        boolean result = super.removeById(id);
        //2 删除缓存中的数据
        stringRedisTemplate.delete(key);
        return result;
    }

    @Override
    public IPage<ArticleDetail> queryPublishedPage(int pageNum, int pageSize) {
        LambdaQueryWrapper<ArticleDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleDetail::getState, "已发布");
        wrapper.orderByDesc(ArticleDetail::getCreateTime);
        IPage<ArticleDetail> page = new Page<>(pageNum, pageSize);
        return super.page(page, wrapper);
    }

}