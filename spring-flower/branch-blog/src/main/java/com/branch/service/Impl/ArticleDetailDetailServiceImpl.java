package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.branch.model.dto.ArticleDetailDTO;
import com.branch.model.dto.ArticleDetailPageDTO;
import com.branch.model.entity.Article;
import com.branch.model.vo.ArticleDetailVO;
import com.branch.service.mapper.ArticleMapper;
import com.branch.service.mapper.ArticleDetailMapper;
import com.branch.result.PagePart;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.branch.model.entity.ArticleDetail;
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
    public PagePart<ArticleDetailVO> queryPublishedPage(ArticleDetailPageDTO articleDetailPageDTO) {
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
        PagePart<ArticleDetailVO> pagePart = new PagePart<>();
        pagePart.setList(voList);
        pagePart.setTotal(detailPage.getTotal());
        pagePart.setPageSize(detailPage.getSize());
        pagePart.setPageNum(detailPage.getCurrent());
        return pagePart;
    }

}
