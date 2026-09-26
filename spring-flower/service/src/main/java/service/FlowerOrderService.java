package service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.result.PageResult;
import dto.FlowerOrderPageDTO;
import entity.FlowerOrder;
import vo.FlowerOrderVO;

/**
 * 订单 Service（对应 flower_order 表）
 */

public interface FlowerOrderService extends IService<FlowerOrder> {

    FlowerOrderVO readById(Long id);

    PageResult<FlowerOrderVO> readPage(FlowerOrderPageDTO flowerOrderPageDTO);

    void update3(Long id);

    void update4(Long id);

    void update5(Long id);

    void update6(Long id);

    void update7(Long id);

    void update8(Long id);

    void update1(Long id);

    void update2(Long id);
}
