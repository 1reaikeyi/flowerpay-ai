package com.branch.controller;

import com.branch.domain.OperationSuccessEnum;
import com.branch.domain.dto.UserShoppingDTO;
import com.branch.domain.vo.UserShoppingVO;
import com.branch.service.UserShoppingService;
import com.branch.result.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.branch.framework.aop.OperationLogging;


import java.util.List;

@RestController
@RequestMapping("/user/shopping")
public class UserShoppingController {
    @Autowired
    private UserShoppingService userShoppingService;

    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping
    public Result create(@Validated @RequestBody UserShoppingDTO userShoppingDTO){
        UserShoppingDTO dto = userShoppingService.create(userShoppingDTO);
        return Result.success(dto);
    }

    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping
    public Result readAll(){
        List<UserShoppingVO> userShoppingVOList = userShoppingService.readAll();
        return Result.success(userShoppingVOList);
    }

    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping
    public Result delete(@RequestParam Long id){
        userShoppingService.delete(id);
        return Result.success(id);
    }

    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping("/all")
    public Result deleteAll(){
        userShoppingService.deleteAll();
        return Result.success();
    }
    @PostMapping("/submit")
    public Result submit(){
        userShoppingService.submit();
        return Result.success();
    }
}
