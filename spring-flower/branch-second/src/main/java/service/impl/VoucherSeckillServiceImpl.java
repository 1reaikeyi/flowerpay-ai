package service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import framework.redis.RedisID;
import framework.security.SecurityContextParam;

import mapper.VoucherSeckillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import service.VoucherOrderService;
import service.VoucherSeckillService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 秒杀优惠券服务实现类 - 实现秒杀优惠券相关业务逻辑
 */
@Service
@Slf4j
public class VoucherSeckillServiceImpl extends ServiceImpl<VoucherSeckillMapper, VoucherSecond> implements VoucherSeckillService {

    @Autowired
    @Lazy
    private VoucherOrderService voucherOrderService;
    @Autowired
    private RedisID redisID;

    @Override
    public VoucherSecond voucherSeckillValid(Long id) {
        VoucherSecond voucherSecond = super.getById(id);
        if (voucherSecond == null) {
            throw new RuntimeException("不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucherSecond.getBeginTime()) || now.isAfter(voucherSecond.getEndTime())) {
            throw new RuntimeException("不在规定时间段");
        }
        if (voucherSecond.getStock() <= 0) {
            throw new RuntimeException("结束了");
        }
        return voucherSecond;
    }

    @Override
    public void pay(VoucherOrder voucherOrder) {
        // 校验秒杀活动是否有效
        VoucherSecond voucherSecond = this.voucherSeckillValid(voucherOrder.getVoucherId());
        if (voucherSecond == null) {
            throw new RuntimeException("秒杀活动不存在或已结束");
        }
        // 获取当前用户ID
        Long userId = SecurityContextParam.getCurrentUserId();
        // 设置订单基础信息
        voucherOrder.setId(redisID.createId("orderId"));
        voucherOrder.setUserId(userId);
        voucherOrder.setStatus(1L);
        voucherOrderService.secondKill(voucherOrder);
    }
}