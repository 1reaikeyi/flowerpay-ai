package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.PagePart;
import com.branch.model.dto.FlowerOrderPageDTO;
import com.branch.model.entity.FlowerOrder;
import com.branch.model.vo.FlowerOrderVO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 订单 Service（对应 flower_order 表）
 */

public interface FlowerOrderService extends IService<FlowerOrder> {

    FlowerOrderVO readById(Long id);

    PagePart<FlowerOrderVO> readPage(FlowerOrderPageDTO flowerOrderPageDTO);

    void update1(Long id);

    void update2(Long id, HttpServletResponse httpServletResponse);

    void update3(Long id);

    void update4(Long id);

    void update5(Long id);

    void update6(Long id);

    void update7(Long id);

    void update8(Long id);


}
