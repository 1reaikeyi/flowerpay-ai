package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.FlowerDetailDTO;
import com.branch.domain.entity.FlowerDetail;
import com.branch.domain.vo.FlowerDetailVO;

import java.util.List;


/**
 * 花店关系 Service（对应 flower_detail 表）
 */

public interface FlowerDetailService extends IService<FlowerDetail> {

    FlowerDetailDTO create(FlowerDetailDTO flowerDetailDTO);

    FlowerDetailVO readCache(Long id);

    void updateCache(FlowerDetailDTO flowerDetailDTO);

    void deleteCache(List<Long> ids);

}
