package com.branch.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.ArticleDTO;
import com.branch.domain.dto.ArticleDetailDTO;
import com.branch.domain.dto.ArticleDetailPageDTO;
import com.branch.domain.entity.ArticleDetail;

/**
 * 文章服务接口 - 定义文章相关业务操作
 */
public interface ArticleDetailService extends IService<ArticleDetail> {
    ArticleDTO create(ArticleDetailDTO articleDetailDTO);
    ArticleDetail readCache(Long id);
    Boolean updateCache(ArticleDetailDTO articleDetail);
    Boolean deleteCache(Long id);

    void queryPublishedPage(ArticleDetailPageDTO articleDetailPageDTO);
}