package com.branch.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.ArticleDetailDTO;
import com.branch.domain.dto.ArticleDetailPageDTO;
import com.branch.domain.entity.ArticleDetail;
import com.branch.domain.vo.ArticleDetailVO;
import com.branch.domain.vo.ArticleVO;
import com.branch.result.PageResult;

import java.util.List;

/**
 * 文章服务接口 - 定义文章相关业务操作
 */
public interface ArticleDetailService extends IService<ArticleDetail> {

    ArticleDetailDTO create(ArticleDetailDTO articleDetailDTO);
    ArticleDetailVO readCache(Long id);
    void updateCache(ArticleDetailDTO articleDetail);
    void deleteCache(List<Long> ids);

    PageResult<ArticleVO> queryPublishedPage(ArticleDetailPageDTO articleDetailPageDTO);
}