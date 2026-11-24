package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.PagePart;
import com.branch.model.dto.FestivalDTO;
import com.branch.model.dto.FestivalPageDTO;
import com.branch.model.entity.Festival;
import com.branch.model.vo.FestivalDetailVO;
import com.branch.model.vo.FestivalVO;

import java.util.List;

/**
 * 芊店的festival Service（对应 festival 表）
 */

public interface FestivalService extends IService<Festival> {

    FestivalDTO create(FestivalDTO festivalDTO);
    FestivalVO readCache(Long id);
    void updateCache(FestivalDTO festivalDTO);
    void deleteCache(List<Long> ids);
    PagePart<FestivalVO> readPage(FestivalPageDTO festivalPageDTO);
    List<FestivalDetailVO> readFestivalDetail(Long id);
    List<FestivalDetailVO> readOfFlower(Long id);
    List<FestivalDetailVO> readOfObject(String object);
    List<FestivalDetailVO> readOfOption(String option);
    List<FestivalDetailVO> readOfNumber(int number);
}
