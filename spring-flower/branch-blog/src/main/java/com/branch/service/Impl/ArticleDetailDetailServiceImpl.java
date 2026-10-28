package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.branch.domain.dto.ArticleDetailDTO;
import com.branch.domain.dto.ArticleDetailPageDTO;
import com.branch.domain.entity.Article;
import com.branch.domain.vo.ArticleDetailVO;
import com.branch.mapper.ArticleMapper;
import com.branch.mapper.ArticleDetailMapper;
import com.branch.result.PageResult;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.branch.domain.entity.ArticleDetail;
import com.branch.service.ArticleDetailService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章服务实现类 - 实现文章相关业务逻辑
 */
@Service
@Slf4j
public class ArticleDetailDetailServiceImpl extends ServiceImpl<ArticleDetailMapper, ArticleDetail> implements ArticleDetailService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public ArticleDetailDTO create(ArticleDetailDTO articleDetailDTO) {
        ArticleDetail articleDetail = BeanUtil.toBean(articleDetailDTO, ArticleDetail.class);
        this.save(articleDetail);
        ArticleDetailDTO dto = BeanUtil.toBean(articleDetailDTO, ArticleDetailDTO.class);
        return dto;
    }

    @Override
    public ArticleDetailVO readCache(Long id) {
        return BeanUtil.toBean(this.getById(id), ArticleDetailVO.class);
    }


    @Override
    public void updateCache(ArticleDetailDTO articleDetailDTO) {
        if(articleDetailDTO.getId() == null){
            throw new IllegalArgumentException("文章ID不能为空");
        }
        ArticleDetail articleDetail = BeanUtil.toBean(articleDetailDTO, ArticleDetail.class);
        this.updateById(articleDetail);
    }

    @Override
    public void deleteCache(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public PageResult<ArticleDetailVO> queryPublishedPage(ArticleDetailPageDTO articleDetailPageDTO) {
        IPage<ArticleDetail> page = new Page<>(articleDetailPageDTO.getPage(), articleDetailPageDTO.getPageSize());
        IPage<ArticleDetail> detailPage = this.page(page);
        List<ArticleDetailVO> voList = detailPage.getRecords().stream()
                .map(d -> BeanUtil.toBean(d, ArticleDetailVO.class))
                .collect(Collectors.toList());
        // 回填分类名称：按 categoryId 批量查 article 表，避免 N+1
        List<Long> categoryIds = voList.stream()
                .map(ArticleDetailVO::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (!categoryIds.isEmpty()) {
            Map<Long, String> nameMap = articleMapper.selectBatchIds(categoryIds).stream()
                    .collect(Collectors.toMap(Article::getId, Article::getCategoryName, (a, b) -> a));
            voList.forEach(vo -> vo.setCategoryName(nameMap.get(vo.getCategoryId())));
        }
        PageResult<ArticleDetailVO> pageResult = new PageResult<>();
        pageResult.setList(voList);
        pageResult.setTotal(detailPage.getTotal());
        pageResult.setPageSize(detailPage.getSize());
        pageResult.setPageNum(detailPage.getCurrent());
        return pageResult;
    }

}
