package start.controller.user;

import model.constant.ShopConstant;
import framework.aop.oparation.OperationEnum;
import common.result.Result;
import model.vo.ShopVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import framework.aop.OperationLogging;

@RestController
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PreAuthorize("permitAll")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/status")
    public Result read() {
        String shop = stringRedisTemplate.opsForValue().get(ShopConstant.SHOP_STATUS);
        ShopVO shopVO = new ShopVO(shop);
        return Result.success(shopVO);
    }
}
