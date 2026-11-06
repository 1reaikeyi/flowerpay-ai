package com.branch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.common.constant.RedisPrefixConstant;
import com.branch.domain.dto.VoucherDTO;
import com.branch.domain.entity.Voucher;

import com.branch.domain.entity.VoucherSecond;
import lombok.extern.slf4j.Slf4j;
import com.branch.mapper.VoucherMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.branch.service.VoucherSecondService;
import com.branch.service.VoucherService;

import java.util.List;

/**
 * 优惠券服务实现类 - 实现优惠券相关业务逻辑
 */
@Service
@Slf4j
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    @Autowired
    private VoucherSecondService voucherSecondService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public VoucherDTO createVoucher(VoucherDTO voucherDTO) {
        Voucher voucher = BeanUtil.toBean(voucherDTO, Voucher.class);
        this.save(voucher);

        List<VoucherSecond> voucherSecondList = voucherDTO.getVoucherSecondList();
        for (VoucherSecond seckill : voucherSecondList) {
            stringRedisTemplate.opsForValue()
                    .set(RedisPrefixConstant.VOUCHER_STOCK_PREFIX + seckill.getVoucherId(), seckill.getStock().toString());
        }
        voucherSecondService.saveBatch(voucherSecondList);
        return voucherDTO;
    }
}
