package service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.result.PageResult;
import model.dto.FestivalDTO;
import model.dto.FestivalPageDTO;
import model.entity.Festival;
import model.vo.FestivalDetailVO;
import model.vo.FestivalVO;

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
