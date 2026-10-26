package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.ArticleDTO;
import com.branch.domain.entity.Article;

import java.util.List;


/**
 * 分类服务接口 - 定义分类相关业务操作
 */
public interface ArticleService extends IService<Article> {

    ArticleDTO create(ArticleDTO articleDTO);
    Article readCache(Long id);
    Boolean updateCache(ArticleDTO article);
    Boolean deleteCache(List<Long> id);


}