package com.branch.controller;


import com.branch.model.dto.ShopDTO;
import com.branch.result.Result;
import com.branch.result.ScrollPart;
import com.branch.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @PostMapping
    public Result createShop(ShopDTO shopDTO) {
        ShopDTO dto = shopService.createShop(shopDTO);
        return Result.success(dto);
    }

    @GetMapping("/type")
    public Result ofType(@RequestParam Long typeId,
                         @RequestParam(required = false) Long lastId, @RequestParam(required = false) Long offset,
                         @RequestParam(required = false) Double x, @RequestParam(required = false) Double y) {
        ScrollPart scrollPart = shopService.queryByType(typeId, lastId, offset, x, y);
        return Result.success(scrollPart);
    }
}
