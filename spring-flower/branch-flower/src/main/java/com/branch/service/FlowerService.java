package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.PagePart;
import com.branch.model.dto.FlowerDTO;
import com.branch.model.dto.FlowerPageDTO;
import com.branch.model.entity.Flower;
import com.branch.model.vo.FlowerDetailVO;
import com.branch.model.vo.FlowerVO;

import java.util.List;

/**
 * 花店 Service（对应 flower 表）
 */

public interface FlowerService extends IService<Flower> {

    FlowerVO readCache(Long id);

    void updateCache(FlowerDTO flowerDTO);

    void deleteCache(List<Long> ids);

    PagePart<FlowerVO> readPage(FlowerPageDTO flowerPageDTO);

    FlowerDTO create(FlowerDTO flowerDTO);

    List<FlowerDetailVO> readFlowerDetail(Long id);
    List<FlowerDetailVO> readOfObject(String object);
    List<FlowerDetailVO> readOfOption(String option);

}
