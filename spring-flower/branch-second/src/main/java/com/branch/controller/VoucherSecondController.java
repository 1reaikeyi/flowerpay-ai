package com.branch.controller;


import com.branch.domain.dto.VoucherUseDTO;
import com.branch.result.Result;
import com.branch.service.VoucherSecondService;
import com.branch.service.VoucherUseService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/voucher/second")
public class VoucherSecondController {
    @Resource
    private VoucherSecondService voucherSecondService;

    private VoucherUseService voucherUseService;
    @PostMapping("/pay1")
    public Result pay1(@RequestBody VoucherUseDTO voucherUseDTO) {
        voucherSecondService.pay1(voucherUseDTO);
        return Result.success("paySuccess");
    }
    @PostMapping("/pay2")
    public Result pay2(@RequestBody VoucherUseDTO voucherUseDTO) {
        voucherSecondService.pay2(voucherUseDTO);
        return Result.success("paySuccess");
    }
}
