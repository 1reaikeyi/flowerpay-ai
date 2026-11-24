package com.branch.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.dto.ShopDTO;
import com.branch.model.entity.Shop;
import com.branch.result.ScrollPart;

public interface ShopService extends IService<Shop> {


    ShopDTO createShop(ShopDTO shopDTO);

    ScrollPart queryByType(Long typeId, Long lastId, Long offset, Double x, Double y);
}
