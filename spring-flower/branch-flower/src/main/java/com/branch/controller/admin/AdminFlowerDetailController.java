package com.branch.controller.admin;

import com.branch.domain.dto.FlowerDetailDTO;
import com.branch.domain.vo.FlowerDetailVO;
import com.branch.service.FlowerDetailService;
import framework.aop.oparation.OperationSuccessEnum;
import result.Result;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/admin/flowerDetail")
@Slf4j
public class AdminFlowerDetailController {
    @Autowired
    private FlowerDetailService flowerDetailService;

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping
    public Result add(@RequestBody FlowerDetailDTO flowerDetailDTO) {
        FlowerDetailDTO dto = flowerDetailService.create(flowerDetailDTO);
        return Result.success(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
//        return Result.success(flowerService.getById(id));
        FlowerDetailVO flowerDetailVO = flowerDetailService.readCache(id);
        return Result.success(flowerDetailVO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody FlowerDetailDTO flowerDetailDTO) {
        flowerDetailService.updateCache(flowerDetailDTO);
        return Result.success(flowerDetailDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) {
        flowerDetailService.deleteCache(ids);
        return Result.success(ids);
    }

}
