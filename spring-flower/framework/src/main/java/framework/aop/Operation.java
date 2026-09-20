package framework.aop;

import cn.hutool.core.util.StrUtil;
import framework.oparation.OperationType;
import framework.security.SecurityContextParam;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
@Slf4j
@Aspect // 标记为AOP切面类
@Component
public class Operation {
    // 操作日志切面：拦截标注了 @OperationLogging 注解的方法，自动记录操作日志
    @Around("@annotation(framework.aop.OperationLogging)")
    public Object interceptOperationLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取目标方法上的 @OperationLogging 注解，取出操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = signature.getMethod();
        OperationLogging annotation = targetMethod.getAnnotation(OperationLogging.class);
        String operation = annotation.operation().name(); // 操作类型（CREATE/GET/UPDATE/DELETE）
        Object result = null;
        String methodArgs = Arrays.toString(joinPoint.getArgs());
        if (StrUtil.isBlank(operation)) {
            methodArgs = "没有param,boby";
        }
        long startTime = System.currentTimeMillis();
        try {
            // 2. 执行目标业务方法，成功后记录操作日志（效果同 OperationType.ok）
            result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            OperationType.ok(operation, methodArgs, costTime);
        } catch (Exception e) {
            // 3. 方法执行异常：记录错误操作日志（效果同 OperationType.error）
            long costTime = System.currentTimeMillis() - startTime;
            OperationType.error(operation, methodArgs, costTime);
            throw e; // 异常继续向上抛，保证全局异常处理器能处理
        }
        return result;
    }
}
