package generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// 日志配置
@Slf4j
//主程序入口
@SpringBootApplication
@ComponentScan(basePackages = {"common","generator"})
public class FlowerGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowerGeneratorApplication.class, args);
        log.info("--匹配3成功");
    }
}
