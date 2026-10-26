package controller;

import common.result.Result;
import domain.dto.VoucherDTO;
import domain.dto.VoucherOrderDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.VoucherOrderService;
import service.VoucherService;

/**
 * 使用 redisLock，异步处理
 */
@RestController
@RequestMapping("/voucher")
@Slf4j
public class VoucherController {
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private VoucherOrderService voucherOrderService;

    @PostMapping("/create")
    public Result createVoucher(@RequestBody VoucherDTO voucherDTO) {
//        voucherService.createVoucher(voucherDTO);
        return Result.success("createVoucher");
    }

    @PostMapping("/pay")
    public Result redisLock(@RequestBody VoucherOrderDTO voucherOrder) {
//        return voucherOrderService.redisLockPay(voucherOrder);
        return Result.success("pay");
    }
}
