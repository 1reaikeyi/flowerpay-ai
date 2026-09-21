package start.user;

import framework.aop.oparation.enums.OperationEnum;
import common.result.Result;
import model.vo.FlowerDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.FlowerDetailService;
import framework.aop.OperationLogging;

@RestController
@RequestMapping("/user/flowerDetail")
public class FlowerDetailController {

    @Autowired
    private FlowerDetailService flowerDetailService;


    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
        FlowerDetailVO flowerDetailVO = flowerDetailService.readCache(id);
        return Result.success(flowerDetailVO);
    }
}
