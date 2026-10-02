package start.controller.user;

import framework.aop.oparation.OperationEnum;
import common.result.Result;
import model.dto.FlowerPageDTO;
import model.vo.FlowerDetailVO;
import model.vo.FlowerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.FlowerDetailService;
import service.FlowerService;
import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/user/flower")
public class FlowerController {

    @Autowired
    private FlowerService flowerService;
    @Autowired
    private FlowerDetailService flowerDetailService;

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
//        return Result.success(flowerService.getById(id));
        FlowerVO flowerVO = flowerService.readCache(id);
        return Result.success(flowerVO);
    }

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/all")
    public Result readPage(FlowerPageDTO flowerPageDTO) {
        return Result.success(flowerService.readPage(flowerPageDTO));
    }

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/of/flowerDetail")
    public Result readFlowerDetail(@RequestParam Long id) {
        List<FlowerDetailVO> flowerDetailVOList = flowerService.readFlowerDetail(id);
        return Result.success(flowerDetailVOList);
    }
}
