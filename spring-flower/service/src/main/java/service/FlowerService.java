package service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.result.PageResult;
import model.dto.FlowerDTO;
import model.dto.FlowerPageDTO;
import model.entity.Flower;
import model.vo.FlowerDetailVO;
import model.vo.FlowerVO;

import java.util.List;

/**
 * 花店 Service（对应 flower 表）
 */

public interface FlowerService extends IService<Flower> {

    FlowerVO readCache(Long id);

    void updateCache(FlowerDTO flowerDTO);

    void deleteCache(List<Long> ids);

    PageResult<FlowerVO> readPage(FlowerPageDTO flowerPageDTO);

    FlowerDTO create(FlowerDTO flowerDTO);

    List<FlowerDetailVO> readFlowerDetail(Long id);
    List<FlowerDetailVO> readOfObject(String object);
    List<FlowerDetailVO> readOfOption(String option);

}
