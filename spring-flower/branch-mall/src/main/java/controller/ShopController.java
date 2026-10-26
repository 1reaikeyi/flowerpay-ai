package controller;

import common.result.Result;
import model.dto.ShopDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.ShopService;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @PostMapping
    public Result createShop(ShopDTO shopDTO) {
        return Result.success(shopService.createShop(shopDTO));
    }

    @GetMapping("/of/type")
    public Result ofType(@RequestParam Long typeId,
                         @RequestParam(required = false) Long lastId,
                         @RequestParam(required = false) Long offset,
                         @RequestParam(required = false) Double x,
                         @RequestParam(required = false) Double y) {
        return Result.success(shopService.queryByType(typeId, lastId, offset, x, y));
    }
}
