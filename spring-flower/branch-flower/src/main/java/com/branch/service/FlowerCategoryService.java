package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.result.PageResult;
import com.branch.domain.dto.FlowerCategoryPageDTO;
import com.branch.domain.dto.FlowerCategoryDTO;
import com.branch.domain.entity.FlowerCategory;
import com.branch.domain.vo.FestivalVO;
import com.branch.domain.vo.FlowerCategoryVO;
import com.branch.domain.vo.FlowerVO;

import java.util.List;

/**
 * 花店分类 Service（对应 flower_category 表）
 */

public interface FlowerCategoryService extends IService<FlowerCategory> {

    FlowerCategoryDTO create(FlowerCategoryDTO flowerCategoryDTO);

    List<FlowerCategoryVO> readByType(Long type);

    PageResult<FlowerCategoryVO> readPage(FlowerCategoryPageDTO flowerCategoryPageDTO);

    void updateByObject(FlowerCategoryDTO categoryDTO);

    void deleteById(List<Long> ids);

    List<FlowerVO> readFlower(Long categoryId);

    List<FestivalVO> readFestival(Long categoryId);
}
