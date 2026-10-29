package controller;


import com.branch.result.Result;
import com.branch.result.ScrollResult;
import domain.dto.ShopDTO;
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
        ShopDTO dto = shopService.createShop(shopDTO);
        return Result.success(dto);
    }

    @GetMapping("/type")
    public Result ofType(@RequestParam Long typeId,
                         @RequestParam(required = false) Long lastId, @RequestParam(required = false) Long offset,
                         @RequestParam(required = false) Double x, @RequestParam(required = false) Double y) {
        ScrollResult scrollResult = shopService.queryByType(typeId, lastId, offset, x, y);
        return Result.success(scrollResult);
    }
}
