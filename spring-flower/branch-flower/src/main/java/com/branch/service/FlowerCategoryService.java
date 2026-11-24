package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.PagePart;
import com.branch.model.dto.FlowerCategoryPageDTO;
import com.branch.model.dto.FlowerCategoryDTO;
import com.branch.model.entity.FlowerCategory;
import com.branch.model.vo.FestivalVO;
import com.branch.model.vo.FlowerCategoryVO;
import com.branch.model.vo.FlowerVO;

import java.util.List;

/**
 * 花店分类 Service（对应 flower_category 表）
 */

public interface FlowerCategoryService extends IService<FlowerCategory> {

    FlowerCategoryDTO create(FlowerCategoryDTO flowerCategoryDTO);

    List<FlowerCategoryVO> readByType(Long type);

    PagePart<FlowerCategoryVO> readPage(FlowerCategoryPageDTO flowerCategoryPageDTO);

    void updateByObject(FlowerCategoryDTO categoryDTO);

    void deleteById(List<Long> ids);

    List<FlowerVO> readFlower(Long categoryId);

    List<FestivalVO> readFestival(Long categoryId);
}
