package com.branch.controller.user;

import com.branch.domain.dto.FestivalPageDTO;
import com.branch.domain.vo.FestivalDetailVO;
import com.branch.domain.vo.FestivalVO;
import com.branch.service.FestivalService;
import framework.aop.oparation.OperationEnum;
import common.result.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/user/festival")
public class FestivalController {

    @Autowired
    private FestivalService festivalService;

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
//        return Result.success(festivalService.getById(id));
        FestivalVO festivalVO = festivalService.readCache(id);
        return Result.success(festivalVO);
    }

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/all")
    public Result readPage(FestivalPageDTO festivalPageDTO) {
        return Result.success(festivalService.readPage(festivalPageDTO));
    }

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/of/festivalDetail")
    public Result readFestivalDetail(@RequestParam Long id) {
        List<FestivalDetailVO> festivalDetailVOList = festivalService.readFestivalDetail(id);
        return Result.success(festivalDetailVOList);
    }
    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/of/flower")
    public Result readFlower(@RequestParam Long id) {
        List<FestivalDetailVO> festivalDetailVOList = festivalService.readOfFlower(id);
        return Result.success(festivalDetailVOList);
    }
}

