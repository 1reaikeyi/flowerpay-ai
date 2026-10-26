package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.entity.BlogComments;


public interface BlogCommentsService extends IService<BlogComments> {

    /**
     * 记录一次博客浏览（HyperLogLog 去重计数）
     */
    void view(Long id);

    /**
     * 查询博客浏览量
     */
    Long viewCount(Long id);
}
