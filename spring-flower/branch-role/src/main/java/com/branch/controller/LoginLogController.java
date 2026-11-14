package com.branch.controller;

import com.branch.domain.entity.LoginLog;
import com.branch.service.LoginLogService;
import com.branch.framework.aop.OperationLogging;
import com.branch.domain.OperationSuccessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('admin:get')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/all")
    public Result getList() {
        PageResult<LoginLog> result = loginLogService.read();
        return Result.success();
    }

    @PreAuthorize("hasAuthority('admin:delete')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping("/delete")
    public Result delete(List<Long> ids) {
        loginLogService.deleteRole(ids);
        return Result.success(ids);
    }

}
