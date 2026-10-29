package service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.ScrollResult;
import domain.dto.ShopDTO;
import domain.entity.Shop;

public interface ShopService extends IService<Shop> {


    ShopDTO createShop(ShopDTO shopDTO);

    ScrollResult queryByType(Long typeId, Long lastId, Long offset, Double x, Double y);
}
