package start.controller.user;

import framework.aop.oparation.OperationEnum;
import common.result.Result;
import model.vo.FestivalDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.FestivalDetailService;
import framework.aop.OperationLogging;

@RestController
@RequestMapping("/user/festivalDetail")
public class FestivalDetailController {

    @Autowired
    private FestivalDetailService festivalDetailService;

    @PreAuthorize("hasAnyRole('USER','EMP')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
//        return Result.success(flowerService.getById(id));
        FestivalDetailVO flowerDetailVO = festivalDetailService.readCache(id);
        return Result.success(flowerDetailVO);
    }

}
