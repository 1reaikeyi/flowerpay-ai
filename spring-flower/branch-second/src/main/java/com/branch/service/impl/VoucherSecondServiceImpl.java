package com.branch.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.common.exception.VoucherException;
import com.branch.domain.bo.RedisIdBO;
import com.branch.domain.dto.VoucherUseDTO;
import com.branch.domain.entity.VoucherSecond;

import com.branch.mapper.VoucherSecondMapper;
import framework.security.SecurityContextParam;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.branch.service.VoucherUseService;
import com.branch.service.VoucherSecondService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 秒杀优惠券服务实现类 - 实现秒杀优惠券相关业务逻辑
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class VoucherSecondServiceImpl extends ServiceImpl<VoucherSecondMapper, VoucherSecond> implements VoucherSecondService {

    @Resource(name = "voucherUseServiceImpl")
    private VoucherUseService voucherUseService;
    @Resource(name = "voucherUseServiceImplAsync")
    private VoucherUseService voucherUseService_async;
    @Autowired
    private RedisIdBO redisID;

    @Override
    public VoucherSecond voucherValid(Long id) {
        VoucherSecond voucherSecond = this.getById(id);
        if (voucherSecond == null) {
            throw new VoucherException("不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucherSecond.getBeginTime()) || now.isAfter(voucherSecond.getEndTime())) {
            throw new VoucherException("不在规定时间段");
        }
        if (voucherSecond.getStock() <= 0) {
            throw new VoucherException("已经结束");
        }
        return voucherSecond;
    }

    @Override
    public void pay1(VoucherUseDTO voucherUseDTO) {
        // 校验秒杀活动是否有效
        VoucherSecond voucherSecond = this.voucherValid(voucherUseDTO.getVoucherId());
        if (voucherSecond == null) {
            throw new VoucherException("秒杀活动不存在或已结束");
        }
        LambdaUpdateWrapper<VoucherSecond> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("stock = stock - 1")
                .eq(VoucherSecond::getVoucherId, voucherUseDTO.getVoucherId())
                .gt(VoucherSecond::getStock, 0);
        if (!this.update(updateWrapper)) {
            throw new VoucherException("库存不足");
        }
        voucherUseService.pay1(voucherUseDTO.getVoucherId());
    }

    @Override
    public void pay2(VoucherUseDTO voucherUseDTO) {
        // 校验秒杀活动是否有效
        VoucherSecond voucherSecond = this.voucherValid(voucherUseDTO.getVoucherId());
        if (voucherSecond == null) {
            throw new RuntimeException("秒杀活动不存在或已结束");
        }
        LambdaUpdateWrapper<VoucherSecond> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("stock = stock - 1")
                .eq(VoucherSecond::getVoucherId, voucherUseDTO.getVoucherId())
                .gt(VoucherSecond::getStock, 0);
        if (!this.update(updateWrapper)) {
            throw new RuntimeException("库存不足");
        }
        voucherUseService_async.pay2(voucherUseDTO.getVoucherId());
    }
}