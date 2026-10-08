package framework.exceptionhandle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class ExternalExceptionHandle {
    /**
     * 捕获静态资源缺失异常（如 favicon.ico），仅记录 warn 日志
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFound(NoResourceFoundException e) {
        log.warn("静态资源未找到: {}", e.getResourcePath());
    }

}
