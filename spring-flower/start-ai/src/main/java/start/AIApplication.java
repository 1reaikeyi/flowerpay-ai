package start;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// 日志配置
@Slf4j
//主程序入口
@SpringBootApplication
// 扫描bean组件
@ComponentScan(basePackages = {"framework","com.branch","start"})
// 扫描mapper接口
@MapperScan("com.branch.mapper")
public class AIApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIApplication.class, args);
        log.info("--匹配成功2");
    }
}