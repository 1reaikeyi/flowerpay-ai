package com.branch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.branch.constant.RedisPrefixConstant;
import com.branch.domain.bo.RedisIdBO;
import com.branch.domain.entity.VoucherUse;
import com.branch.mapper.VoucherUseMapper;
import com.branch.service.VoucherUseService;
import com.branch.framework.bo.SecurityContextParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券订单服务实现类 - 实现优惠券订单相关业务逻辑
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class VoucherUseServiceImpl extends ServiceImpl<VoucherUseMapper, VoucherUse> implements VoucherUseService {

    @Autowired
    private RedisIdBO redisID;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>();
    private static final DefaultRedisScript<Long> ROLLBACK_SCRIPT = new DefaultRedisScript<>();
    static {
        REDIS_SCRIPT.setLocation(new ClassPathResource("second.lua"));
        REDIS_SCRIPT.setResultType(Long.class);
        ROLLBACK_SCRIPT.setLocation(new ClassPathResource("second-rollback.lua"));
        ROLLBACK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void pay1(Long voucherId) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Long orderId = redisID.getId("同步");
        // 执行 lua
        Long result = stringRedisTemplate.execute(
                REDIS_SCRIPT,
                List.of(RedisPrefixConstant.VOUCHER_STOCK_PREFIX + voucherId,
                        RedisPrefixConstant.VOUCHER_ORDER_PREFIX + voucherId),
                voucherId.toString(), userId.toString(), orderId.toString());
        if (result != 0) {
            throw new RuntimeException(result == 1 ? "库存不够" : "重复下单");
        }
        VoucherUse voucherUse = VoucherUse.builder()
                .voucherId(voucherId)
                .id(orderId)
                .useTime(LocalDateTime.now())
                .userId(userId)
                .build();
        try {
            this.save(voucherUse);
        } catch (Exception e) {
            // 订单落库失败，回滚 lua 已扣减的库存和已下单标记，避免锁单无单
            rollbackRedis(voucherId, userId);
            throw e;
        }
    }

    /**
     * 回滚秒杀 lua 的扣减结果，仅在 lua 已成功扣减后调用
     */
    private void rollbackRedis(Long voucherId, Long userId) {
        try {
            stringRedisTemplate.execute(
                    ROLLBACK_SCRIPT,
                    List.of(RedisPrefixConstant.VOUCHER_STOCK_PREFIX + voucherId,
                            RedisPrefixConstant.VOUCHER_ORDER_PREFIX + voucherId),
                    userId.toString());
        } catch (Exception e) {
            log.error("Redis 库存回滚失败, voucherId={}, userId={}", voucherId, userId, e);
        }
    }

    @Override
    public void pay2(Long voucherId) {
        pay1(voucherId);
    }


}
