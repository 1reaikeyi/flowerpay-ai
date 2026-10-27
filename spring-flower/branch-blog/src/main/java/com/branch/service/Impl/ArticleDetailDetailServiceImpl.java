package com.branch.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.branch.domain.dto.ArticleDetailDTO;
import com.branch.domain.dto.ArticleDetailPageDTO;
import com.branch.domain.vo.ArticleDetailVO;
import com.branch.domain.vo.ArticleVO;
import com.branch.mapper.ArticleDetailMapper;
import com.branch.result.PageResult;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import com.branch.domain.entity.ArticleDetail;
import com.branch.service.ArticleDetailService;

import java.util.List;

/**
 * 文章服务实现类 - 实现文章相关业务逻辑
 */
@Service
@Slf4j
public class ArticleDetailDetailServiceImpl extends ServiceImpl<ArticleDetailMapper, ArticleDetail> implements ArticleDetailService {

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
    public PageResult<ArticleVO> queryPublishedPage(ArticleDetailPageDTO articleDetailPageDTO) {
        return null;
    }

}