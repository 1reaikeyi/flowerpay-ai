package com.branch.controller;

import com.branch.domain.dto.ArticleDTO;
import com.branch.domain.dto.ArticleDetailDTO;

import com.branch.domain.dto.ArticleDetailPageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.branch.service.ArticleDetailService;
import com.branch.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.branch.result.Result;

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
@RequestMapping("/article")
@Slf4j
public class ArticleDetailController {

    @Autowired
    private ArticleDetailService articleDetailService;

    /**
     * 添加文章
     *
     * @param articleDetailDTO 文章信息
     * @return 结果
     */
    @PostMapping
    public Result createArticle(@RequestBody @Validated ArticleDetailDTO articleDetailDTO) {
        ArticleDTO dto = articleDetailService.create(articleDetailDTO);
        return Result.success(dto);
    }


    /**
     * 获取分页文章列表
     *
     * @return 分页结果包含文章列表
     */
    @GetMapping("/all")
    public Result readArticlePage(@Validated ArticleDetailPageDTO articleDetailPageDTO){
        articleDetailService.queryPublishedPage(articleDetailPageDTO);
        return Result.success();
    }

    /**
     * 获取单个文章
     *
     * @param id 文章ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public Result readById(@PathVariable Long id) {
        articleDetailService.readCache(id);
        return Result.success();
    }

    /**
     * 更新文章
     *
     * @param articleDetailDTO 文章信息
     * @return 结果
     */
    @PutMapping
    public Result updateArticle(@RequestBody @Validated ArticleDetailDTO articleDetailDTO) {
        articleDetailService.updateCache(articleDetailDTO);
        return Result.success();
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 结果
     */
    @DeleteMapping
    public Result deleteById(@PathVariable Long id) {
        articleDetailService.deleteCache(id);
        return Result.success();
    }
}