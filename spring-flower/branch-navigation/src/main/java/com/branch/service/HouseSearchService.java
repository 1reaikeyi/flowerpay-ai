package com.branch.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.branch.domain.entity.District;
import com.branch.domain.entity.House;
import com.branch.domain.vo.HouseResultVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图找房搜索服务
 */
@Slf4j
@Service
public class HouseSearchService {

    /** 行政区 key 前缀，例如 tb_district:1016 */
    private static final String DISTRICT_KEY_PREFIX = "tb_district:";

    /** 房源 key 前缀，例如 tb_house:60dd663615fae76aa8ea4a1a */
    private static final String HOUSE_KEY_PREFIX = "tb_house:";

    /** 批量读取 JSON 的 Lua 脚本：逐个 key 调用 JSON.GET，不存在或出错时返回空串 */
    private static final String JSON_GET_SCRIPT =
            "local r = {} "
                    + "for i, k in ipairs(KEYS) do "
                    + "  local ok, v = pcall(redis.call, 'JSON.GET', k) "
                    + "  r[i] = (ok and v) and v or '' "
                    + "end "
                    + "return r";
    /** 每批从 Redis 读取的记录数 */
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 聚合结果缓存：房源数量大，避免每次拖动/缩放地图都全量扫描 Redis。
     * 数据更新后重启应用即可刷新。
     */
    private volatile List<HouseResultVo> districtCache;

    /**
     * 地图找房：按行政区聚合房源套数和均价
     */
    public List<HouseResultVo> searchDistrict() {
        if (this.districtCache == null) {
            synchronized (this) {
                if (this.districtCache == null) {
                    this.districtCache = this.doSearchDistrict();
                }
            }
        }
        return this.districtCache;
    }

    private List<HouseResultVo> doSearchDistrict() {
        // 1、读取所有行政区
        List<District> districts = this.readAll(DISTRICT_KEY_PREFIX, District.class);
        if (districts.isEmpty()) {
            log.warn("Redis 中没有找到行政区数据，key 前缀：{}", DISTRICT_KEY_PREFIX);
            return Collections.emptyList();
        }

        // 2、扫描所有房源，按行政区编号累计套数与总价
        Map<Integer, double[]> stats = new HashMap<>();
        this.scanHouses(stats);

        // 3、组装返回给前端的数据
        List<HouseResultVo> resultList = new ArrayList<>(districts.size());
        for (District district : districts) {
            double[] stat = stats.getOrDefault(district.getCode(), new double[]{0, 0});
            HouseResultVo vo = new HouseResultVo();
            vo.setCode(String.valueOf(district.getCode()));
            vo.setName(district.getName());
            vo.setTotal(String.valueOf((long) stat[0]));
            vo.setPrice(stat[0] == 0 ? "0.00" : NumberUtil.roundStr(stat[1] / stat[0], 2));
            if (district.getLocation() != null) {
                vo.setLongitude(district.getLocation().getLongitude());
                vo.setLatitude(district.getLocation().getLatitude());
            }
            resultList.add(vo);
        }
        resultList.sort(Comparator.comparing(HouseResultVo::getCode));
        return resultList;
    }

    /**
     * 扫描指定前缀的 key，批量读取 JSON 并转换成 Java 对象
     */
    private <T> List<T> readAll(String prefix, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        List<String> keys = new ArrayList<>(BATCH_SIZE);
        try (Cursor<String> cursor = stringRedisTemplate.scan(
                ScanOptions.scanOptions().match(prefix + "*").count(BATCH_SIZE).build())) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
                if (keys.size() >= BATCH_SIZE) {
                    list.addAll(this.readBatch(keys, clazz));
                    keys.clear();
                }
            }
        }
        if (!keys.isEmpty()) {
            list.addAll(this.readBatch(keys, clazz));
        }
        return list;
    }

    /**
     * 管道批量执行 JSON.GET，再用 JSONUtil 转成 Java 对象
     */
    private <T> List<T> readBatch(List<String> keys, Class<T> clazz) {
        List<Object> replies = this.jsonGet(keys);
        List<T> list = new ArrayList<>(replies.size());
        for (Object reply : replies) {
            String json = this.replyToString(reply);
            if (json == null || json.isEmpty()) {
                continue;
            }
            try {
                list.add(JSONUtil.toBean(json, clazz));
            } catch (Exception e) {
                log.warn("JSON 转 Java 失败：{}", json, e);
            }
        }
        return list;
    }

    /**
     * 扫描所有房源，按行政区累计 套数 和 总价
     */
    private void scanHouses(Map<Integer, double[]> stats) {
        List<String> keys = new ArrayList<>(BATCH_SIZE);
        try (Cursor<String> cursor = stringRedisTemplate.scan(
                ScanOptions.scanOptions().match(HOUSE_KEY_PREFIX + "*").count(BATCH_SIZE).build())) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
                if (keys.size() >= BATCH_SIZE) {
                    this.accumulate(keys, stats);
                    keys.clear();
                }
            }
        }
        if (!keys.isEmpty()) {
            this.accumulate(keys, stats);
        }
    }

    private void accumulate(List<String> keys, Map<Integer, double[]> stats) {
        List<Object> replies = this.jsonGet(keys);
        for (Object reply : replies) {
            String json = this.replyToString(reply);
            if (json == null || json.isEmpty()) {
                continue;
            }
            try {
                House house = JSONUtil.toBean(json, House.class);
                if (house.getDistrictCode() == null || house.getPrice() == null) {
                    continue;
                }
                double[] stat = stats.computeIfAbsent(house.getDistrictCode(), k -> new double[2]);
                stat[0] += 1;                 //套数
                stat[1] += house.getPrice();  //总价，用于最后算均价
            } catch (Exception e) {
                log.warn("房源 JSON 转 Java 失败：{}", json, e);
            }
        }
    }

    /**
     * 对一批 key 执行 Redis 的 JSON.GET 命令（一次 EVAL 批量执行，减少网络往返）
     * 项目里 RedisConnection 是 Redisson 实现，不支持 connection.execute 原生命令，
     * 所以改用 Lua 脚本转调 JSON.GET
     */
    private List<Object> jsonGet(List<String> keys) {
        List<Object> result = this.redissonClient.getScript(StringCodec.INSTANCE).eval(
                RScript.Mode.READ_ONLY,
                JSON_GET_SCRIPT,
                RScript.ReturnType.MULTI,
                new ArrayList<Object>(keys));
        return result == null ? Collections.emptyList() : result;
    }

    private String replyToString(Object reply) {
        if (reply == null) {
            return null;
        }
        if (reply instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return reply.toString();
    }
}
