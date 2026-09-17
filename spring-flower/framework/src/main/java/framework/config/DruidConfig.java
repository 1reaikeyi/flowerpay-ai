package framework.config;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidConfig {
    @Bean
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }
    // 声明为 static：BeanPostProcessor 类型的 Bean 必须用静态工厂方法，
    // 否则会导致 @Configuration 实例被提前创建，触发
    // "Bean 'druidConfig' is not eligible for getting processed by all BeanPostProcessors" 警告
    @Bean
    public static BeanNameAutoProxyCreator autoProxyCreator() {
        BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
        creator.setProxyTargetClass(true);
        creator.setBeanNames("*Service"); //拦截所有service的bean
        creator.setInterceptorNames("druidStatInterceptor");
        return creator;
    }
}
