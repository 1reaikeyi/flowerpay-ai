package framework.config;

import framework.interceptor.SensitiveWordInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private SensitiveWordInterceptor sensitiveWordInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册敏感词拦截器，拦截AI相关接口
        registry.addInterceptor(sensitiveWordInterceptor).addPathPatterns("/ai");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
       registry.addResourceHandler("/image/**")
                .addResourceLocations("file:ku/image/");
    }
}
