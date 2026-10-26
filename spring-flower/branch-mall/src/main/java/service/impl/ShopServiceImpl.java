package service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import common.result.ScrollResult;
import mapper.ShopMapper;
import model.dto.ShopDTO;
import model.entity.Shop;
import model.entity.ShopType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;
import service.ShopService;
import service.ShopTypeService;

import java.util.List;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    private static final String SHOP_TYPE = "shopType:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ShopTypeService shopTypeService;

    @Override
    public ShopDTO createShop(ShopDTO shopDTO) {
        Shop shop = BeanUtil.toBean(shopDTO, Shop.class);
        ShopType shopType = BeanUtil.toBean(shopDTO, ShopType.class);
        super.save(shop);
        shopTypeService.save(shopType);
        Long typeId = shopType.getId();
        RedisGeoCommands.GeoLocation<String> location = new RedisGeoCommands.GeoLocation<>(
                shop.getId().toString(), new Point(shop.getX(), shop.getY()));
        stringRedisTemplate.opsForGeo().add(SHOP_TYPE + typeId, location);
        return shopDTO;
    }

    @Override
    public ScrollResult queryByType(Long typeId, Long lastId, Long offset, Double x, Double y) {
        ScrollResult scrollResult = new ScrollResult();
        Long limit = offset == null ? 5L : offset;
        // 如果没有传经纬度，使用基于 ID 的滚动分页查询
        if (x == null && y == null) {
            List<Shop> shops = super.list(
                    new LambdaQueryWrapper<Shop>()
                            .eq(Shop::getTypeId, typeId)
                            .gt(Shop::getId, lastId != null ? lastId : 0)
                            .orderByAsc(Shop::getId)
                            .last("LIMIT " + limit)
            );
            if (CollectionUtil.isEmpty(shops)) {
                scrollResult.setList(null);
                scrollResult.setMinTime(0L);
                scrollResult.setOffset(limit);
                return scrollResult;
            }
            scrollResult.setList(shops);
            scrollResult.setMinTime(shops.get(shops.size() - 1).getId());
            scrollResult.setOffset(limit);
            return scrollResult;
        }
        // 如果传了经纬度，使用 Redis GEO 按距离排序查询附近店铺
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo().search(
                SHOP_TYPE + typeId,
                GeoReference.fromCoordinate(x, y),
                new Distance(limit, Metrics.KILOMETERS),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(5).sortAscending()
        );
        if (CollectionUtil.isEmpty(results)) {
            scrollResult.setList(null);
            scrollResult.setMinTime(0L);
            scrollResult.setOffset(limit);
            return scrollResult;
        }
        List<Long> shopIds = results.getContent().stream()
                .map(item -> Long.valueOf(item.getContent().getName()))
                .toList();
        List<Shop> shopList = super.listByIds(shopIds);
        scrollResult.setList(shopList);
        scrollResult.setMinTime(shopIds.get(shopIds.size() - 1));
        scrollResult.setOffset(limit);
        return scrollResult;
    }
}
