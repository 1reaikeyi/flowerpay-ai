package service;

import com.baomidou.mybatisplus.extension.service.IService;
import domain.entity.VoucherOrder;
import domain.entity.VoucherSecond;


/**
 * 秒杀优惠券服务接口 - 定义秒杀优惠券相关业务操作
 */
public interface VoucherSeckillService extends IService<VoucherSecond> {
    VoucherSecond voucherSeckillValid(Long id);

    /**
     * 同步秒杀下单（Redisson 分布式锁）：
     * 校验活动有效性 -> 生成订单 -> 调用 VoucherOrderService.secondKill 完成下单
     */
    void pay(VoucherOrder voucherOrder);
}