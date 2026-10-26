package controller;

import common.result.Result;
import VoucherOrder;
import domain.dto.VoucherOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.VoucherSeckillService;

/**
 * 使用 redisson 分布式锁实现秒杀 - 同步版本
 * 支付成功逻辑已迁移至 VoucherOrderService.paySuccess()
 */
@RestController
@RequestMapping("/voucherSeckill")
public class VoucherSeckillController {
    @Autowired
    private VoucherSeckillService voucherSeckillService;

    @PostMapping("/pay")
    public Result redisLock(@RequestBody VoucherOrderDTO voucherOrderDTO) {
//        voucherSeckillService.pay(voucherOrder);
        return Result.success("paySuccess");
    }
}
