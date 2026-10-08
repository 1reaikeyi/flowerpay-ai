package framework.exceptionhandle;

import common.constant.ErrorConstant;
import common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class InternalExceptionHandle {


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
}
