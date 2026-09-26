package framework.exceptionhandle;

import common.exception.BaseException;
import common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常 BaseException
     * 返回 200 状态码 + Result.error
     */
    @ExceptionHandler(BaseException.class)
    public Result exception(BaseException e) {
        return Result.error(e.getMessage() + ">>>>去联系管理员");
    }

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);  // 关键:打印堆栈,方便排查
        return Result.error("服务器开小差了,请稍后再试");
    }

    /**
     * 捕获静态资源缺失异常（如 favicon.ico），仅记录 warn 日志，避免污染错误日志
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFound(NoResourceFoundException e) {
        log.warn("静态资源未找到: {}", e.getResourcePath());
    }

}