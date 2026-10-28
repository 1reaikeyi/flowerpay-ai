package com.branch.controller;

import com.branch.domain.dto.ArticleDTO;
import com.branch.domain.dto.ArticleDetailDTO;

import com.branch.domain.dto.ArticleDetailPageDTO;
import com.branch.domain.vo.ArticleDetailVO;
import com.branch.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.branch.service.ArticleDetailService;
import com.branch.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.branch.result.Result;

import java.util.List;

/**
 * 文章管理控制器
 * <p>
 * 负责文章的增删改查、状态管理、分类关联等核心功能，
 * 提供完整的文章管理API接口，支持JWT认证和权限控制。
 * </p>
 * @author Smart-doc
 * @since 1.0.0
 * @version 1.0.0
 */
@RestController
@RequestMapping("/article/detail")
@Slf4j
public class ArticleDetailController {

    @Autowired
    private ArticleDetailService articleDetailService;

    @PostMapping
    public Result createArticle(@RequestBody @Validated ArticleDetailDTO articleDetailDTO) {
        ArticleDetailDTO dto = articleDetailService.create(articleDetailDTO);
        return Result.success(dto);
    }

    @GetMapping("/all")
    public Result readArticlePage(@Validated ArticleDetailPageDTO articleDetailPageDTO){
        PageResult<ArticleDetailVO> result = articleDetailService.queryPublishedPage(articleDetailPageDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result readById(@PathVariable Long id) {
        ArticleDetailVO vo = articleDetailService.readCache(id);
        return Result.success(vo);
    }

    @PutMapping
    public Result updateArticle(@RequestBody @Validated ArticleDetailDTO articleDetailDTO) {
        articleDetailService.updateCache(articleDetailDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(List<Long> ids) {
        articleDetailService.deleteCache(ids);
        return Result.success();
    }
}