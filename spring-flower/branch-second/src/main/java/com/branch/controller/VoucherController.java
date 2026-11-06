package com.branch.controller;


import com.branch.domain.dto.VoucherDTO;
import com.branch.domain.dto.VoucherUseDTO;
import com.branch.result.Result;
import com.branch.service.VoucherUseService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.branch.service.VoucherService;


@RestController
@RequestMapping("/voucher")
@Slf4j
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @PostMapping("/create")
    public Result createVoucher(@RequestBody VoucherDTO voucherDTO) {
        VoucherDTO dto = voucherService.createVoucher(voucherDTO);
        return Result.success(dto);
    }

}
