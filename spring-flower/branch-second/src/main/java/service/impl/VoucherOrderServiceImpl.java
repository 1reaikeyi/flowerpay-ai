package service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import common.result.Result;
import framework.redis.RedisID;

import mapper.VoucherOrderMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.VoucherOrderService;
import service.VoucherSeckillService;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 优惠券订单服务实现类 - 实现优惠券订单相关业务逻辑
 */
@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements VoucherOrderService {

    @Autowired
    private VoucherSeckillService voucherSeckillService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisID redisID;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // ==================== 内存队列异步下单方案（对应 /voucher/pay）====================
    // Lua 脚本：校验库存和重复下单
    private static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>();

    static {
        REDIS_SCRIPT.setLocation(new ClassPathResource("redis-seckill.lua"));
        REDIS_SCRIPT.setResultType(Long.class);
    }

    // 异步订单处理队列 - 存储完整的 VoucherOrder 对象
    private final BlockingQueue<VoucherOrder> orderQueue = new ArrayBlockingQueue<>(128 * 1024);

    // 秒杀订单处理线程池 - 单线程确保顺序处理
    private static final ExecutorService SECKILL_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "voucher-order-handler");
        t.setDaemon(true);
        return t;
    });

    // ==================== Stream 消费组异步下单方案（对应 /voucherOrder/pay）====================
    private static final String VOUCHERORDER_STREAM = "stream.order";
    private static final String VOUCHERORDER_STREAM_GROUP = "group";

    // Stream 消费处理线程池
    private static final ExecutorService STREAM_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "seckill-order-handler");
        t.setDaemon(true);
        return t;
    });

    /**
     * 初始化方法 - 启动两套异步订单处理线程
     */
    @PostConstruct
    public void init() {
        // 内存队列消费线程
        SECKILL_EXECUTOR.submit(new HandleOrderTaskByList());
        // Stream 消费组创建 + 消费线程
        try {
            stringRedisTemplate.opsForStream().createGroup(VOUCHERORDER_STREAM, VOUCHERORDER_STREAM_GROUP);
            log.info("Redis Stream消费组" + VOUCHERORDER_STREAM_GROUP + " 创建成功");
        } catch (Exception e) {
            log.info("二次确认:Redis Stream消费组" + VOUCHERORDER_STREAM_GROUP + "创建成功");
        }
        STREAM_EXECUTOR.submit(new HandleOrderTask());
    }

    @PreDestroy
    public void destroy() {
        SECKILL_EXECUTOR.shutdown();
        STREAM_EXECUTOR.shutdown();
        try {
            // 等待10秒让未处理的订单完成
            if (!SECKILL_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                SECKILL_EXECUTOR.shutdownNow();
            }
            if (!STREAM_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                STREAM_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            SECKILL_EXECUTOR.shutdownNow();
            STREAM_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 支付成功处理逻辑
     * 1. 校验一人一单（放在前面，避免重复下单扣减库存）
     * 2. 扣减库存
     * 3. 保存订单
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void paySuccess(VoucherOrder voucherOrder) {
        // 扣库存
        boolean success = voucherSeckillService.lambdaUpdate()
                .eq(VoucherSecond::getVoucherId, voucherOrder.getVoucherId())
                .gt(VoucherSecond::getStock, 0)
                .setSql("stock = stock - 1")
                .update();
        if (!success) {
            throw new RuntimeException("库存不够");
        }
        // 一人一单检查（优先检查，避免重复下单扣减库存）
        Long count = this.count(new LambdaQueryWrapper<VoucherOrder>()
                .eq(VoucherOrder::getUserId, voucherOrder.getUserId())
                .eq(VoucherOrder::getVoucherId, voucherOrder.getVoucherId()));
        if (count > 0) {
            throw new RuntimeException("一人一单");
        }
        // 保存订单
        this.save(voucherOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void secondKill(VoucherOrder voucherOrder) {
        // 使用 Redisson 分布式锁防止重复下单
        RLock redisLock = redissonClient.getLock("redisson:voucherSeckill:" + voucherOrder.getUserId() + ":" + voucherOrder.getVoucherId());
        boolean locked = false;
        try {
            // 尝试获取锁，等待5秒
            locked = redisLock.tryLock(5, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁失败");
        }

        if (!locked) {
            throw new RuntimeException("请勿重复下单");
        }

        try {
            paySuccess(voucherOrder);
        } finally {
            // 确保锁被释放
            if (locked && redisLock.isHeldByCurrentThread()) {
                redisLock.unlock();
            }
        }
    }

    @Override
    public Result redisLockPay(VoucherOrder voucherOrder) {
        VoucherSecond voucherSecond = voucherSeckillService.voucherSeckillValid(voucherOrder.getVoucherId());
        if (voucherSecond == null) {
            return Result.error("秒杀活动不存在或已结束");
        }
        Long userId = SecurityContextParam.getCurrentUserId();
        Long orderId = redisID.createId("pay");
        // 执行 lua
        Long result = stringRedisTemplate.execute(REDIS_SCRIPT,
                List.of(),
                voucherOrder.getVoucherId().toString(),
                userId.toString(),
                orderId.toString());
        if (result != 0) {
            return Result.error(result == 1 ? "库存不够" : "重复下单");
        }
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setStatus(1L);
        boolean offer = orderQueue.offer(voucherOrder);
        if (!offer) {
            return Result.error("系统繁忙，请稍后重试");
        }
        return Result.success(orderId);
    }

    @Override
    public Result streamPay(VoucherOrder voucherOrder) {
        VoucherSecond voucherSecond = voucherSeckillService.voucherSeckillValid(voucherOrder.getVoucherId());
        if (voucherSecond == null) {
            return Result.error("秒杀活动不存在或已结束");
        }
        // 获取当前登录用户ID
        Long userId = SecurityContextParam.getCurrentUserId();
        // 生成订单ID
        Long orderId = redisID.createId("order");
        // 执行 Lua 脚本：校验库存和重复下单
        Long result = stringRedisTemplate.execute(REDIS_SCRIPT,
                List.of(),
                voucherOrder.getVoucherId().toString(),
                userId.toString(),
                orderId.toString());
        // 脚本返回非0表示失败
        if (result != 0) {
            return Result.error(result == 1 ? "库存不够" : "重复下单");
        }
        return Result.success(orderId);
    }

    /**
     * 内存队列异步处理任务
     */
    private class HandleOrderTaskByList implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // 从队列中取出订单（阻塞等待）
                    VoucherOrder voucherOrder = orderQueue.take();
                    log.info("检验值voucherOrder:{}", voucherOrder);
                    ILock redisLock = new RedisLock(stringRedisTemplate,
                            "redisson:voucherSeckill:" + voucherOrder.getUserId() + ":" + voucherOrder.getVoucherId());
                    boolean locked = false;
                    locked = redisLock.getLocked(10);
                    if (!locked) {
                        continue;
                    }
                    try {
                        // 执行实际的下单逻辑
                        paySuccess(voucherOrder);
                    } finally {
                        redisLock.unlock();
                    }
                } catch (Exception e) {
                    // 线程被中断，退出循环
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Stream 消费异步处理任务
     * 从队列中取出订单，完成扣减库存和保存订单操作
     */
    private class HandleOrderTask implements Runnable {
        @Override
        public void run() {
            String streamKey = "stream.order";
            while (true) {
                List<MapRecord<String, Object, Object>> messageList = stringRedisTemplate.opsForStream().read(
                        Consumer.from(VOUCHERORDER_STREAM_GROUP, UUID.randomUUID().toString()),
                        StreamReadOptions.empty().count(1).block(Duration.ofSeconds(10)),
                        StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
                if (messageList == null || messageList.isEmpty()) {
                    continue;
                }
                MapRecord<String, Object, Object> record = messageList.get(0);
                Map<Object, Object> map = record.getValue();
                VoucherOrder voucherOrder = VoucherOrder.builder()
                        .voucherId(Long.parseLong(map.get("voucherId").toString()))
                        .userId(Long.parseLong(map.get("userId").toString()))
                        .id(Long.parseLong(map.get("orderId").toString()))
                        .build();
                log.info(voucherOrder.toString());
                secondKill(voucherOrder);
                stringRedisTemplate.opsForStream().acknowledge(streamKey, VOUCHERORDER_STREAM_GROUP, record.getId());
                log.info("确认acknowledge");
            }
        }
    }
}
