package com.branch.controller.user;

import com.branch.domain.vo.FlowerDetailVO;
import com.branch.service.FlowerDetailService;
import framework.aop.oparation.OperationEnum;
import common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import framework.aop.OperationLogging;

@RestController
@RequestMapping("/user/flowerDetail")
public class FlowerDetailController {

    @Autowired
    private FlowerDetailService flowerDetailService;


    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
        FlowerDetailVO flowerDetailVO = flowerDetailService.readCache(id);
        return Result.success(flowerDetailVO);
    }
}
