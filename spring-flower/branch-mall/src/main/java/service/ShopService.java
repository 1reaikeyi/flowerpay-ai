package service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.result.ScrollResult;
import model.dto.ShopDTO;
import model.entity.Shop;

public interface ShopService extends IService<Shop> {

    /**
     * 创建店铺：保存店铺与店铺类型，并将地理位置写入 Redis GEO
     *
     * @return 原始入参 shopDTO
     */
    ShopDTO createShop(ShopDTO shopDTO);

    /**
     * 按类型查询店铺：
     * 未传经纬度时使用基于 ID 的滚动分页；
     * 传了经纬度时使用 Redis GEO 按距离排序查询附近店铺
     */
    ScrollResult queryByType(Long typeId, Long lastId, Long offset, Double x, Double y);
}
