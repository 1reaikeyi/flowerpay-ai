package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.result.PageResult;
import com.branch.domain.dto.FestivalDTO;
import com.branch.domain.dto.FestivalPageDTO;
import com.branch.domain.entity.Festival;
import com.branch.domain.vo.FestivalDetailVO;
import com.branch.domain.vo.FestivalVO;

import java.util.List;

/**
 * 芊店的festival Service（对应 festival 表）
 */

public interface FestivalService extends IService<Festival> {

    FestivalDTO create(FestivalDTO festivalDTO);
    FestivalVO readCache(Long id);
    void updateCache(FestivalDTO festivalDTO);
    void deleteCache(List<Long> ids);
    PageResult<FestivalVO> readPage(FestivalPageDTO festivalPageDTO);
    List<FestivalDetailVO> readFestivalDetail(Long id);
    List<FestivalDetailVO> readOfFlower(Long id);
    List<FestivalDetailVO> readOfObject(String object);
    List<FestivalDetailVO> readOfOption(String option);
    List<FestivalDetailVO> readOfNumber(int number);
}
