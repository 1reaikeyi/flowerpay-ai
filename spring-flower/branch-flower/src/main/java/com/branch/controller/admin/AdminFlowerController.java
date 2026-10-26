package com.branch.controller.admin;

import com.branch.common.system.DictionaryVO;
import com.branch.common.system.NumberEnum;
import com.branch.common.system.ObjectEnum;
import com.branch.common.system.OptionEnum;
import com.branch.domain.dto.FlowerDTO;
import com.branch.domain.dto.FlowerPageDTO;
import com.branch.domain.vo.FlowerDetailVO;
import com.branch.domain.vo.FlowerVO;
import com.branch.service.FlowerDetailService;
import com.branch.service.FlowerService;
import framework.aop.oparation.OperationSuccessEnum;
import com.branch.result.PageResult;
import com.branch.result.Result;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import framework.aop.OperationLogging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/flower")
@Slf4j
public class AdminFlowerController {

    @Autowired
    private FlowerService flowerService;
    @Autowired
    private FlowerDetailService flowerDetailService;

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping
    public Result create(@RequestBody FlowerDTO flowerDTO) {
        FlowerDTO dto = flowerService.create(flowerDTO);
        return Result.success(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
//        return Result.success(flowerService.getById(id));
        FlowerVO flowerVO = flowerService.readCache(id);
        return Result.success(flowerVO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/all")
    public Result readPage(FlowerPageDTO flowerPageDTO) {
        PageResult<FlowerVO> flowerVOPageResult = flowerService.readPage(flowerPageDTO);
        return Result.success(flowerVOPageResult);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody FlowerDTO flowerDTO) {
        flowerService.updateCache(flowerDTO);
        return Result.success(flowerDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) {
        flowerService.deleteCache(ids);
        return Result.success(ids);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/of/flowerDetail")
    public Result readFlowerDetail(@RequestParam Long id) {
        List<FlowerDetailVO> flowerDetailVOList = flowerService.readFlowerDetail(id);
        return Result.success(flowerDetailVOList);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/of/object")
    public Result readOfObject(@RequestParam String object) {
        List<FlowerDetailVO> flowerDetailVOList = flowerService.readOfObject(object);
        return Result.success(flowerDetailVOList);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/of/option")
    public Result readOfOption(@RequestParam String option) {
        List<FlowerDetailVO> flowerDetailVOList = flowerService.readOfOption(option);
        return Result.success(flowerDetailVOList);
    }
    @GetMapping("/object")
    public Result<List<DictionaryVO>> dictionary() {
        List<DictionaryVO> list = Arrays.stream(ObjectEnum.values())
                .map(e -> new DictionaryVO(e.name(), e.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
    @GetMapping("/option")
    public Result<List<DictionaryVO>> option() {
        List<DictionaryVO> list = Arrays.stream(OptionEnum.values())
                .map(e -> new DictionaryVO(e.name(), e.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
    @GetMapping("/number")
    public Result<List<DictionaryVO>> number() {
        List<DictionaryVO> list = Arrays.stream(NumberEnum.values())
                .map(e -> new DictionaryVO(e.name(), e.getMeaning()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
}
