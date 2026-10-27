package com.branch.service.Impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.mapper.BlogCommentsMapper;
import com.branch.service.BlogCommentsService;
import com.branch.domain.entity.BlogComments;
import framework.security.SecurityContextParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements BlogCommentsService {

    private static final String BLOG_VIEW_PREFIX = "blog:view:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void view(Long id) {
        Long userId = SecurityContextParam.getCurrentUserId();
        stringRedisTemplate.opsForHyperLogLog().add(BLOG_VIEW_PREFIX + id, userId.toString());
    }

    @Override
    public Long viewCount(Long id) {
        return stringRedisTemplate.opsForHyperLogLog().size(BLOG_VIEW_PREFIX + id);
    }


    @Override
    public boolean saveBatch(Collection<BlogComments> entityList) {
        return super.saveBatch(entityList);
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     */
    @Override
    public boolean saveOrUpdateBatch(Collection<BlogComments> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }
}
