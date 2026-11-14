package com.branch.framework.exceptionhandle;

import com.branch.constant.ErrorConstant;
import com.branch.exception.*;


import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.branch.result.Result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Order(1)
@Slf4j
public class PartExceptionHandler {
    
    /**
     * 处理数据库唯一约束冲突（如重复用户名）
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handleSQLIntegrityConstraintViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            String[] split = message.split("'");
            String username = split[1];
            String Message = username + ErrorConstant.USERNAME_EXIST;
            return Result.error(Message);
        } else {
            return Result.error(ErrorConstant.ERROR + e.getMessage());
        }
    }
     /**
     * 处理权限不足异常（含 URL 级和 @PreAuthorize 方法级权限校验失败）
     * 返回 403 状态码，与 SecurityConfig.accessDeniedHandler 保持一致
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        Result<Object> result = Result.error("无权限访问");
        result.setCode(403);
        return result;
    }
    @ExceptionHandler(LoginFailedException.class)
    public Result loginFailedExceptionHandler(com.branch.exception.LoginFailedException e) {
        return Result.error(ErrorConstant.LOGIN_FAILED+ ", " + e.getMessage() + ", 请检查账号密码");
    }
    @ExceptionHandler(ModelFailedException.class)
    public Result modelFailedExceptionHandler(ModelFailedException e) {
        return Result.error(ErrorConstant.USER_FAILED+ ", " + e.getMessage() + ", 请检查用户信息");
    }
    @ExceptionHandler(FileException.class)
    public Result fileFailedExceptionHandler(ModelFailedException e) {
        return Result.error(ErrorConstant.USER_FAILED+ ", " + e.getMessage() + ", 请检查用户信息");
    }


}
