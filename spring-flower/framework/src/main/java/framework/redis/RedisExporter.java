package framework.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class RedisExporter {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * key	含义	说明
     * usedMemoryBytes	Redis 已使用内存，单位字节	10485760 B = 10MB，Redis 实际占用内存
     * db0KeyCount	db0 数据库的 key 总数量	Redis 默认 db0，一共 142 个 key
     * keyspaceHits	键命中次数	查询 key 时，找到了的总次数
     * keyspaceMisses	键未命中次数	查询 key，不存在的总次数
     * hitRate	缓存命中率	公式：hits/(hits+misses)，95.97% 属于很不错的缓存命中率
     * slowLogCount	慢查询日志条数	Redis 慢查询队列里现存 5 条慢命令
     * status	接口状态	success = 成功拉取 redis_exporter 数据
     */
    // 正则
    private static final Pattern PATTERN_MEMORY = Pattern.compile("redis_memory_used_bytes\\s+(\\d+)");
    private static final Pattern PATTERN_DB0_KEYS = Pattern.compile("redis_db_keys\\{db=\"0\"\\}\\s+(\\d+)");
    private static final Pattern PATTERN_HITS = Pattern.compile("redis_keyspace_hits_total\\s+(\\d+)");
    private static final Pattern PATTERN_MISSES = Pattern.compile("redis_keyspace_misses_total\\s+(\\d+)");
    private static final Pattern PATTERN_SLOWLOG = Pattern.compile("redis_slowlog_length\\s+(\\d+)");

    /**
     * 从redis_exporter(9121)拉取并解析指标
     * @param exporterUrl redis exporter metrics地址
     * @return 指标map
     */
    public Map<String, Object> getRedisMetrics(String exporterUrl) {
        Map<String, Object> result = new HashMap<>();
        try {
            String rawText = restTemplate.getForObject(exporterUrl, String.class);
            if (rawText == null) {
                result.put("status", "fail");
                result.put("msg", "返回内容为空");
                return result;
            }

            long memory = 0, keys = 0, hits = 0, misses = 0, slow = 0;
            Matcher m;

            m = PATTERN_MEMORY.matcher(rawText);
            if (m.find()) memory = Long.parseLong(m.group(1));

            m = PATTERN_DB0_KEYS.matcher(rawText);
            if (m.find()) keys = Long.parseLong(m.group(1));

            m = PATTERN_HITS.matcher(rawText);
            if (m.find()) hits = Long.parseLong(m.group(1));

            m = PATTERN_MISSES.matcher(rawText);
            if (m.find()) misses = Long.parseLong(m.group(1));

            m = PATTERN_SLOWLOG.matcher(rawText);
            if (m.find()) slow = Long.parseLong(m.group(1));

            double hitRate = hits + misses == 0 ? 0 : (double) hits / (hits + misses);

            result.put("usedMemoryBytes", memory);
            result.put("db0KeyCount", keys);
            result.put("keyspaceHits", hits);
            result.put("keyspaceMisses", misses);
            result.put("hitRate", String.format("%.2f%%", hitRate * 100));
            result.put("slowLogCount", slow);
            result.put("status", "success");
        } catch (Exception e) {
            result.put("status", "fail");
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
