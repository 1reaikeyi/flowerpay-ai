package com.branch.controller;

import com.branch.domain.entity.LoginLog;
import com.branch.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.branch.result.PageResult;
import com.branch.result.Result;

import java.util.List;

/**
 * <p>
 * 系统访问记录 前端控制器
 * </p>
 *
 * @author flower
 * @since 2026-10-25
 */
@RestController
@RequestMapping("/login/info")
public class LoginLogController {
    @Autowired
    private LoginLogService loginLogService;

    @GetMapping("/all")
    public Result getList() {
        PageResult<LoginLog> result = loginLogService.read();
        return Result.success();
    }

    @DeleteMapping
    public Result delete(List<Long> ids) {
        loginLogService.deleteRole(ids);
        return Result.success(ids);
    }

}
