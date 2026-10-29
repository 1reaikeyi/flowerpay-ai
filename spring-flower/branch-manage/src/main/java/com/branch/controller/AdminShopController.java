package com.branch.controller;

import com.branch.domain.vo.ShopStatusVO;
import com.branch.common.ShopConstant;
import framework.aop.oparation.OperationSuccessEnum;
import com.branch.result.Result;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import framework.aop.OperationLogging;

@RestController
@RequestMapping("/admin/shop")
public class AdminShopController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PostMapping("{status}")
    @Validated
    public Result updateStatus(@PathVariable
                                   @Min(value = 0) @Max(value = 1) Long status) {
        stringRedisTemplate.opsForValue().set(ShopConstant.SHOP_STATUS, status == 1 ? "营业中" : "已打烊");
        ShopStatusVO shopStatusVO = new ShopStatusVO(status == 1 ? "营业中" : "已打烊");
        return Result.success(shopStatusVO);
    }
    
    @PreAuthorize("permitAll")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping
    public Result read() {
        String shop = stringRedisTemplate.opsForValue().get(ShopConstant.SHOP_STATUS);
        ShopStatusVO shopStatusVO = new ShopStatusVO(shop);
        return Result.success(shopStatusVO);
    }
}
