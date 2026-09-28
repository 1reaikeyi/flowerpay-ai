package start.admin;

import framework.aop.oparation.OperationEnum;
import common.result.Result;
import lombok.extern.slf4j.Slf4j;
import model.dto.FlowerDetailDTO;
import model.vo.FlowerDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.FlowerDetailService;
import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/admin/flowerDetail")
@Slf4j
public class AdminFlowerDetailController {
    @Autowired
    private FlowerDetailService flowerDetailService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.CREATE)
    @PostMapping
    public Result add(@RequestBody FlowerDetailDTO flowerDetailDTO) {
        FlowerDetailDTO dto = flowerDetailService.create(flowerDetailDTO);
        return Result.success(dto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_EMP') or hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
//        return Result.success(flowerService.getById(id));
        FlowerDetailVO flowerDetailVO = flowerDetailService.readCache(id);
        return Result.success(flowerDetailVO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody FlowerDetailDTO flowerDetailDTO) {
        flowerDetailService.updateCache(flowerDetailDTO);
        return Result.success(flowerDetailDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.DELETE)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) {
        flowerDetailService.deleteCache(ids);
        return Result.success(ids);
    }

}
