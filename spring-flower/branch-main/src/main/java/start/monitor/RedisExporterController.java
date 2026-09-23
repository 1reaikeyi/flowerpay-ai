package start.monitor;

import common.result.Result;
import framework.redis.RedisExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RedisExporterController {
    @Autowired
    private RedisExporter redisExporter;
    @Value("${flower.redis.host}")
    private String host;
    @GetMapping("/redis")
    public Result<Map<String,Object>> getRedisMetrics() {
        String url = "http://"+host+":9121/metrics";
        return Result.success(redisExporter.getRedisMetrics(url));
    }
}
