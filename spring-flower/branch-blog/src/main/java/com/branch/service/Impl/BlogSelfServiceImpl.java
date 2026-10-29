package com.branch.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.dto.BlogSelfDTO;
import com.branch.domain.entity.BlogSelf;
import com.branch.mapper.BlogSelfMapper;
import com.branch.service.BlogSelfService;

import org.springframework.stereotype.Service;

/**
 * 博主信息服务实现类 - 实现博主信息相关业务逻辑
 */
@Service
public class BlogSelfServiceImpl extends ServiceImpl<BlogSelfMapper, BlogSelf> implements BlogSelfService {
    @Override
    public void updateSelf(BlogSelfDTO blogSelfDTO) {

    }
}
