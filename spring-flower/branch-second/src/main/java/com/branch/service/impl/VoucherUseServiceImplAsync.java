package com.branch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.constant.RedisPrefixConstant;
import com.branch.domain.bo.RedisIdBO;
import com.branch.domain.entity.VoucherUse;
import com.branch.mapper.VoucherUseMapper;
import com.branch.service.VoucherUseService;
import com.branch.framework.bo.SecurityContextParam;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class VoucherUseServiceImplAsync extends ServiceImpl<VoucherUseMapper, VoucherUse> implements VoucherUseService {

    @Resource(name = "voucherOrderExecutor")
    private ExecutorService voucherOrderExecutor;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisIdBO redisIdBO;

    private static final DefaultRedisScript<Long> ASYNC_REDIS_SCRIPT = new DefaultRedisScript<>();
    static {
        ASYNC_REDIS_SCRIPT.setLocation(new ClassPathResource("second-async.lua"));
        ASYNC_REDIS_SCRIPT.setResultType(Long.class);
    }
    private static final String VOUCHER_STREAM = "stream.order";
    private static final String VOUCHER_STREAM_GROUP = "group";
    private static final String VOUCHER_STREAM_CONSUMER = "order-consumer";

    @PostConstruct
    public void init() {
        try {
            stringRedisTemplate.execute((RedisCallback<String>) connection ->
                    connection.streamCommands().xGroupCreate(
                            VOUCHER_STREAM.getBytes(StandardCharsets.UTF_8),
                            VOUCHER_STREAM_GROUP,
                            ReadOffset.from("0"),
                            true));
            log.info("Redis Stream消费组 {} 创建成功", VOUCHER_STREAM_GROUP);
        } catch (Exception e) {
            log.info("已存在 {}", VOUCHER_STREAM_GROUP);
        }
        voucherOrderExecutor.submit(new Handler());
    }
    @Override
    public void pay1(Long voucherId) {
        pay2(voucherId);
    }

    @Override
    public void pay2(Long voucherId) {
        Long userId = SecurityContextParam.getCurrentUserId();
        Long orderId = redisIdBO.getId("异步");
        // 执行 lua
        Long result = stringRedisTemplate.execute(
                ASYNC_REDIS_SCRIPT,
                List.of(RedisPrefixConstant.VOUCHER_STOCK_PREFIX + voucherId,
                        RedisPrefixConstant.VOUCHER_ORDER_PREFIX + voucherId,
                        VOUCHER_STREAM),
                voucherId.toString(), userId.toString(), orderId.toString());
        if (result != 0) {
            throw new RuntimeException(result == 1 ? "库存不够" : "重复下单");
        }
        log.info("订单等待异步orderId={}, voucherId={}, userId={}", orderId, voucherId, userId);
    }

    private class Handler implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 1.阻塞读取 stream 中的新订单（无消息时最多等待 2 秒）
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from(VOUCHER_STREAM_GROUP, VOUCHER_STREAM_CONSUMER),
                            StreamReadOptions.empty().noack().block(Duration.ofSeconds(2)),
                            StreamOffset.create(VOUCHER_STREAM, ReadOffset.lastConsumed()));

                    if (list == null || list.isEmpty()) {
                        continue;
                    }

                    for (MapRecord<String, Object, Object> record : list) {
                        Map<Object, Object> values = record.getValue();
                        VoucherUse voucherUse = VoucherUse.builder()
                                .id(Long.valueOf(values.get("orderId").toString()))
                                .voucherId(Long.valueOf(values.get("voucherId").toString()))
                                .userId(Long.valueOf(values.get("userId").toString()))
                                .useTime(LocalDateTime.now())
                                .build();
                        VoucherUseServiceImplAsync.this.save(voucherUse);

                        stringRedisTemplate.opsForStream()
                                .acknowledge(VOUCHER_STREAM, VOUCHER_STREAM_GROUP, record.getId());
                    }
                } catch (Exception e) {
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("订单处理器线程被中断，准备退出");
                        return;
                    }
                    log.error("处理订单异常", e);
                    handlePendingList();
                }
            }
        }

        private void handlePendingList() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 1.读取未确认的 pending 消息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from(VOUCHER_STREAM_GROUP, VOUCHER_STREAM_CONSUMER),
                            StreamReadOptions.empty().noack(),
                            StreamOffset.create(VOUCHER_STREAM, ReadOffset.from("0")));
                    // 2.没有 pending，结束重试
                    if (list == null || list.isEmpty()) {
                        break;
                    }

                    for (MapRecord<String, Object, Object> record : list) {
                        Map<Object, Object> values = record.getValue();
                        VoucherUse voucherUse = VoucherUse.builder()
                                .id(Long.valueOf(values.get("orderId").toString()))
                                .voucherId(Long.valueOf(values.get("voucherId").toString()))
                                .userId(Long.valueOf(values.get("userId").toString()))
                                .useTime(LocalDateTime.now())
                                .build();
                        VoucherUseServiceImplAsync.this.save(voucherUse);

                        stringRedisTemplate.opsForStream()
                                .acknowledge(VOUCHER_STREAM, VOUCHER_STREAM_GROUP, record.getId());
                    }
                } catch (Exception e) {
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("pendingList 处理器线程被中断，准备退出");
                        return;
                    }
                    log.error("处理 pendingList 异常", e);
                    sleepQuietly(20);
                }
            }
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
