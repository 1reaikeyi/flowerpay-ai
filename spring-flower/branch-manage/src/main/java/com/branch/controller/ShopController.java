package com.branch.controller;

import com.branch.domain.vo.ShopStatusVO;
import com.branch.common.ShopConstant;
import framework.aop.oparation.OperationSuccessEnum;
import com.branch.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import framework.aop.OperationLogging;

@RestController
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PreAuthorize("permitAll")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/status")
    public Result read() {
        String shop = stringRedisTemplate.opsForValue().get(ShopConstant.SHOP_STATUS);
        ShopStatusVO shopStatusVO = new ShopStatusVO(shop);
        return Result.success(shopStatusVO);
    }
}
