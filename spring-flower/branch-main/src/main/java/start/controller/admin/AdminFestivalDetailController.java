package start.controller.admin;

import framework.aop.oparation.OperationEnum;
import common.result.Result;
import lombok.extern.slf4j.Slf4j;
import model.dto.FestivalDetailDTO;
import model.vo.FestivalDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.FestivalDetailService;
import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/admin/festivalDetail")
@Slf4j
public class AdminFestivalDetailController {
    @Autowired
    private FestivalDetailService festivalDetailService;

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.CREATE)
    @PostMapping
    public Result add(@RequestBody FestivalDetailDTO festivalDetailDTO) {
        FestivalDetailDTO dto = festivalDetailService.create(festivalDetailDTO);
        return Result.success(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readById(@RequestParam Long id) {
//        return Result.success(flowerService.getById(id));
        FestivalDetailVO flowerDetailVO = festivalDetailService.readCache(id);
        return Result.success(flowerDetailVO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody FestivalDetailDTO festivalDetailDTO) {
        festivalDetailService.updateCache(festivalDetailDTO);
        return Result.success(festivalDetailDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.DELETE)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) {
        festivalDetailService.deleteCache(ids);
        return Result.success(ids);
    }
}
