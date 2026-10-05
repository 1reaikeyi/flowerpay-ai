package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.FestivalDetailDTO;
import com.branch.domain.entity.FestivalDetail;
import com.branch.domain.vo.FestivalDetailVO;

import java.util.List;

/**
 * 芊店的festival关系 Service（对应 festival_detail 表）
 */

public interface FestivalDetailService extends IService<FestivalDetail> {

    FestivalDetailDTO create(FestivalDetailDTO festivalDetailDTO);

    FestivalDetailVO readCache(Long id);

    void updateCache(FestivalDetailDTO festivalDetailDTO);

    void deleteCache(List<Long> ids);
}
