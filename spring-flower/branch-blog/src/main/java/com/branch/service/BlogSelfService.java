package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.BlogSelfDTO;
import com.branch.domain.entity.BlogSelf;


/**
 * 博主信息服务接口 - 定义博主信息相关业务操作
 */
public interface BlogSelfService extends IService<BlogSelf> {
    void updateSelf(BlogSelfDTO blogSelfDTO);
}
