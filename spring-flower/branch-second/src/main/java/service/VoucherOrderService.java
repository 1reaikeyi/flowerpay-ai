package service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.common.result.Result;
import domain.entity.VoucherOrder;


/**
 * 优惠券订单服务接口 - 定义优惠券订单相关业务操作
 */
public interface VoucherOrderService extends IService<VoucherOrder> {

    /**
     * 支付成功处理逻辑
     * 1. 校验一人一单
     * 2. 扣减库存
     * 3. 保存订单
     *
     * @param voucherOrder 订单对象
     */
    void paySuccess(VoucherOrder voucherOrder);

    /**
     * 使用 redis 锁
     * @param voucherOrder
     */
    void secondKill(VoucherOrder voucherOrder);

    /**
     * 基于 RedisLock + 内存队列 的异步秒杀下单：
     * 校验活动 -> 执行 Lua 校验库存与重复下单 -> 入队异步处理
     */
    Result redisLockPay(VoucherOrder voucherOrder);

    /**
     * 基于 Redis Stream 消费组 的异步秒杀下单：
     * 校验活动 -> 执行 Lua（含 xadd 到 stream）-> 由消费线程异步处理
     */
    Result streamPay(VoucherOrder voucherOrder);
}