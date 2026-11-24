package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.dto.ArticleDTO;
import com.branch.model.dto.ArticlePageDTO;
import com.branch.model.entity.Article;
import com.branch.model.vo.ArticleVO;
import com.branch.result.PagePart;

import java.util.List;


/**
 * 分类服务接口 - 定义分类相关业务操作
 */
public interface ArticleService extends IService<Article> {

    ArticleDTO create(ArticleDTO articleDTO);
    PagePart<ArticleVO> readPage(ArticlePageDTO articlePageDTO);
    ArticleVO readCache(Long id);
    void updateCache(ArticleDTO article);
    void deleteCache(List<Long> id);
}