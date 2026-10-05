package com.branch.controller.user;

import com.branch.domain.dto.FlowerCategoryPageDTO;
import com.branch.domain.vo.FestivalVO;
import com.branch.domain.vo.FlowerCategoryVO;
import com.branch.domain.vo.FlowerVO;
import com.branch.service.FlowerCategoryService;
import framework.aop.oparation.OperationEnum;
import common.result.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/user/category")
public class FlowerCategoryController {

    @Autowired
    private FlowerCategoryService flowerCategoryService;

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readByType(@RequestParam("type") Long type) {
        List<FlowerCategoryVO> flowerCategoryVOList = flowerCategoryService.readByType(type);
        return Result.success(flowerCategoryVOList);
    }

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/all")
    public Result readPage( @Validated FlowerCategoryPageDTO flowerCategoryPageDTO) {
        return Result.success(flowerCategoryService.readPage(flowerCategoryPageDTO));
    }


    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/of/flower")
    public Result readFlower(@RequestParam("id") Long categoryId) {
        List<FlowerVO> flowerVOList = flowerCategoryService.readFlower(categoryId);
        return Result.success(flowerVOList);
    }


    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_EMP') or hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("of/festival")
    public Result getFestival(@RequestParam("id") Long categoryId) {
        List<FestivalVO> festivalList = flowerCategoryService.readFestival(categoryId);
        return Result.success(festivalList);
    }
}
