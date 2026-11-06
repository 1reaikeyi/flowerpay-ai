package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.VoucherUseDTO;
import com.branch.domain.entity.VoucherSecond;


/**
 * 秒杀优惠券服务接口 - 定义秒杀优惠券相关业务操作
 */
public interface VoucherSecondService extends IService<VoucherSecond> {

    VoucherSecond voucherValid(Long id);

    void pay1(VoucherUseDTO voucherOrder);
    void pay2(VoucherUseDTO voucherOrder);
}