package framework.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


import java.lang.reflect.Method;

@Slf4j
@Aspect // 标记为AOP切面类
@Component
public class LoggingAspect {

    @Around("@annotation(com.framework.aop.Logging)")
    public Object interceptServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取注解信息和目标方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 目标方法
        Method targetMethod = signature.getMethod();
        // 获取自定义注解
        Logging annotation = targetMethod.getAnnotation(Logging.class);
        // 注解的描述属性
        String methodDesc = annotation.desc();
        // 目标类名（比如com.rent.service.RentService）
        String className = joinPoint.getTarget().getClass().getName();
        // 目标方法名（比如queryRentInfo）
        String methodName = targetMethod.getName();

        Object result = null;
        try {
            // 3. 执行目标方法（核心业务逻辑）
            result = joinPoint.proceed();
            log.info("=>class执行类: {}, 执行方法: {}, 方法备注: {}", className, methodName, methodDesc);
            log.info(" Rerurn: {}", result);
        } catch (Exception e) {
            // 5. 方法执行异常：打印异常信息
            log.info("=>class执行类: {}, 执行方法: {}, 方法备注: {}", className, methodName, methodDesc);
            log.error("存在异常信息:{}", e.getMessage());
            throw e;
        }
        return result;
    }


}
