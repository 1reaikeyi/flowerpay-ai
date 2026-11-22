package com.branch.controller;

import com.branch.domain.OperationSuccessEnum;
import com.branch.domain.dto.UserAddressDTO;
import com.branch.domain.vo.UserAddressVO;
import com.branch.service.UserAddressService;

import com.branch.result.ScrollPart;
import com.branch.result.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.branch.framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/user/address")
public class UserAddressController {
    @Autowired
    private UserAddressService userAddressService;


    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping
    public Result createAddress(@RequestBody UserAddressDTO userAddressDTO) {
        UserAddressDTO dto = userAddressService.create(userAddressDTO);
        return Result.success(dto);
    }

    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("default")
    public Result readDefaultAddress() {
        UserAddressVO defaultAddressVO = userAddressService.readDefaultAddress();
        return Result.success(defaultAddressVO);
    }
    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/all")
    public Result readAddress(Long offset, Long current) {
        ScrollPart<UserAddressVO> scrollPart = userAddressService.readPage(offset,current);
        return Result.success(scrollPart);
    }
    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("/default/{id}")
    public Result updateDefaultAddress(@PathVariable Long id) {
        userAddressService.updateDefaultAddress(id);
        return Result.success();
    }
    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping
    public Result updateAddress(@RequestBody UserAddressDTO userAddressDTO) {
        userAddressService.updateAddress(userAddressDTO);
        return Result.success();
    }
    @PreAuthorize("hasRole('USER')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping
    public Result deleteAddress(List<Long> ids) {
        userAddressService.deleteAddress(ids);
        return Result.success();
    }

}
