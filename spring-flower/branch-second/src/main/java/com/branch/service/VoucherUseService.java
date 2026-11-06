package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.entity.VoucherUse;


/**
 * 优惠券订单服务接口 - 定义优惠券订单相关业务操作
 */
public interface VoucherUseService extends IService<VoucherUse> {


    void pay1(Long voucherId);

    void pay2(Long voucherId);
}