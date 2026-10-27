package com.branch.controller;

import com.branch.domain.dto.ArticleDTO;

import com.branch.domain.dto.ArticlePageDTO;
import com.branch.domain.vo.ArticleVO;
import com.branch.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.branch.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理控制器
 *
 * @author Smart-doc
 * @since 1.0.0
 */
@RestController
@RequestMapping("/article")

@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 添加分类
     *
     * @param articleDTO 分类信息
     * @return 结果
     */
    @PostMapping
    public Result create(@RequestBody @Validated ArticleDTO articleDTO) {
        ArticleDTO dto = articleService.create(articleDTO);
        return Result.success(dto);
    }

    /**
     * 获取所有分类
     *
     * @return 结果
     */
    @GetMapping("/all")
    public Result read(@Validated ArticlePageDTO articlePageDTO) {
        articleService.list();
        return Result.success();
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情信息
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") Long id) {
        ArticleVO articleVO = articleService.readCache(id);
        return Result.success(articleVO);
    }

    /**
     * 更新分类
     *
     * @return 结果
     */
    @PutMapping
    public Result update(@RequestBody @Validated ArticleDTO articleDTO) {
        articleService.updateCache(articleDTO);
        return Result.success(articleDTO.getId());
    }

    /**
     * 删除分类
     *
     * @param ids 分类ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") List<Long> ids) {
        articleService.deleteCache(ids);
        return Result.success(ids);
    }
}