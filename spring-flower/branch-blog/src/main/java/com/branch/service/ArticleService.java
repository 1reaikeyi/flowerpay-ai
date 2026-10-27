package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.ArticleDTO;
import com.branch.domain.entity.Article;
import com.branch.domain.vo.ArticleVO;

import java.util.List;


/**
 * 分类服务接口 - 定义分类相关业务操作
 */
public interface ArticleService extends IService<Article> {
    
    ArticleDTO create(ArticleDTO articleDTO);
    ArticleVO readCache(Long id);
    void updateCache(ArticleDTO article);
    void deleteCache(List<Long> id);
}