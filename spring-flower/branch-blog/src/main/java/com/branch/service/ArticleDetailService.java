package com.branch.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.dto.ArticleDetailDTO;
import com.branch.model.dto.ArticleDetailPageDTO;
import com.branch.model.entity.ArticleDetail;
import com.branch.model.vo.ArticleDetailVO;
import com.branch.result.PagePart;

import java.util.List;

/**
 * 文章服务接口 - 定义文章相关业务操作
 */
public interface ArticleDetailService extends IService<ArticleDetail> {

    ArticleDetailDTO create(ArticleDetailDTO articleDetailDTO);
    ArticleDetailVO readCache(Long id);
    void updateCache(ArticleDetailDTO articleDetail);
    void deleteCache(List<Long> ids);

    PagePart<ArticleDetailVO> queryPublishedPage(ArticleDetailPageDTO articleDetailPageDTO);
}