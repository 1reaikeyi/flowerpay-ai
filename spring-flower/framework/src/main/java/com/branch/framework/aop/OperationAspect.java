package com.branch.framework.aop;


import com.branch.domain.entity.OperateLog;
import com.branch.framework.bo.SecurityContextParam;
import com.branch.service.OperateLogService;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
@Slf4j
@Aspect // 标记为AOP切面类
@Component
public class OperationAspect {

    @Autowired
    private OperateLogService operateLogService;

    // 操作日志切面：拦截标注了 @OperationLogging 注解的方法，自动记录操作日志
    @Around("@annotation(com.branch.framework.aop.OperationLogging)")
    public Object interceptOperationLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取目标方法上的 @OperationLogging 注解，取出操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = signature.getMethod();
        OperationLogging annotation = targetMethod.getAnnotation(OperationLogging.class);

        // 目标类名
        String className = joinPoint.getTarget().getClass().getName();
        // 目标方法名（比如queryRentInfo）
        String methodName = targetMethod.getName();

        String operation = annotation.operation().name(); // 操作类型（CREATE/GET/UPDATE/DELETE）


        String methodArgs = Arrays.toString(joinPoint.getArgs());

        Object result = null;
        long startTime = System.currentTimeMillis();
        Long userId = SecurityContextParam.getCurrentUserId();
        try {
            // 2. 执行目标业务方法，成功后记录操作日志（效果同 Operation.ok）
            result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
//            OperateLog operateLog = OperateLog.builder()
//                    .status(1)
//                    .title(null).requestMethod(null).method(methodName).businessType(0)
//                    .operIp("localhost").operatorType(0).operLocation(null)
//                    .operName(userId.toString()).operParam(methodArgs).operUrl(null).operTime(LocalDateTime.now())
//                    .costTime(costTime)
//                    .jsonResult(result.toString())
//                    .build();
//            operateLogService.save(operateLog);
        } catch (Exception e) {
            // 3. 方法执行异常：记录错误操作日志（效果同 Operation.error）
            long costTime = System.currentTimeMillis() - startTime;
//            OperateLog operateLog = OperateLog.builder()
//                    .status(0)
//                    .title(null).requestMethod(null).method(methodName).businessType(0)
//                    .operIp("localhost").operatorType(0).operLocation(null)
//                    .operName(userId.toString()).operParam(methodArgs).operUrl(null).operTime(LocalDateTime.now())
//                    .costTime(costTime)
//                    .jsonResult(result.toString())
//                    .errorMsg(e.getMessage())
//                    .build();
//            operateLogService.save(operateLog);
            throw e; // 异常继续向上抛，保证全局异常处理器能处理
        }

        return result;
    }
}
