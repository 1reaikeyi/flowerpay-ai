package start;

import comom.properties.SessionProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// spring-security 随 service/framework 依赖引入且不可排除：被扫描的 RoleAuthenticationProvider、
// SecurityConfig 及大量 @PreAuthorize 均强依赖它。framework 的 SecurityConfig 已配置
// anyRequest().permitAll()，ai 模块的 /chat、/session、/see 接口不在鉴权规则内，不会被拦截
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan(basePackages = {"mapper"})
@ComponentScan(basePackages = {"common","framework","service","start"})
@Slf4j
@EnableConfigurationProperties({SessionProperties.class})
public class AIApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIApplication.class, args);
        log.info("---匹配成功2");
    }

}
