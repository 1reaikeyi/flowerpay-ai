package controller;

import common.result.Result;
import common.annotion.Logging;
import domain.dto.VoucherOrderDTO;
import lombok.extern.slf4j.Slf4j;
import VoucherOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.VoucherOrderService;

/**
 * 优惠券订单控制器 - 异步处理秒杀订单（基于 Redis Stream 消费组）
 */
@RestController
@RequestMapping("/voucherOrder")
@Slf4j
public class VoucherOrderController {
    @Autowired
    private VoucherOrderService voucherOrderService;

    @Logging(desc = "异步下单")
    @PostMapping("/pay")
    public Result redisproLock(@RequestBody VoucherOrderDTO voucherOrderDTO) {
//        return voucherOrderService.streamPay(voucherOrder);
        return Result.success();
    }
}
