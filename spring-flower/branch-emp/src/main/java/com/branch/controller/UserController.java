package com.branch.controller;

import com.branch.domain.dto.LoginDTO;
import com.branch.domain.dto.UserDTO;
import com.branch.service.UserService;
import framework.aop.oparation.OperationEnum;
import common.result.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import framework.aop.OperationLogging;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        userService.register(userDTO);
        return Result.success("register");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(token);
    }

    @OperationLogging(operation = OperationEnum.CREATE)
    @PostMapping("/logout")
    public Result logout() {
        userService.logout();
        return Result.success("logout");
    }

    @PreAuthorize("hasAnyRole('USER')")
    @OperationLogging(operation = OperationEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody UserDTO userDTO) {
        userService.updateByObject(userDTO);
        return Result.success(userDTO.getId());
    }
}
