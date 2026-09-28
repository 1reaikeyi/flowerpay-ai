package start.admin;

import framework.aop.oparation.OperationEnum;
import common.result.PageResult;
import common.result.Result;
import lombok.extern.slf4j.Slf4j;
import model.dto.FlowerCategoryPageDTO;
import model.dto.FlowerCategoryDTO;
import model.vo.FestivalVO;
import model.vo.FlowerCategoryVO;
import model.vo.FlowerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.FlowerCategoryService;
import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class AdminFlowerCategoryController {

    @Autowired
    private FlowerCategoryService flowerCategoryService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.CREATE)
    @PostMapping
    public Result create(@RequestBody FlowerCategoryDTO flowerCategoryDTO) {
        FlowerCategoryDTO saved = flowerCategoryService.create(flowerCategoryDTO);
        return Result.success(saved);
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_EMP') or hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readByType(@RequestParam("type") Long type) {
        List<FlowerCategoryVO> flowerCategoryVOList = flowerCategoryService.readByType(type);
        return Result.success(flowerCategoryVOList);
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_EMP') or hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/all")
    public Result readPage( @Validated FlowerCategoryPageDTO flowerCategoryPageDTO) {
        PageResult<FlowerCategoryVO> flowerCategoryVOPageResult = flowerCategoryService.readPage(flowerCategoryPageDTO);
        return Result.success(flowerCategoryVOPageResult);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody FlowerCategoryDTO categoryDTO) {
        flowerCategoryService.updateByObject(categoryDTO);
        return Result.success(categoryDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.DELETE)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) {
        flowerCategoryService.deleteById(ids);
        return Result.success(ids);
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_EMP') or hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/of/flower")
    public Result readFlower(@RequestParam("id") Long categoryId) {
        List<FlowerVO> flowerVOList = flowerCategoryService.readFlower(categoryId);
        return Result.success(flowerVOList);
    }
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_EMP') or hasAuthority('ROLE_ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("of/festival")
    public Result getFestival(@RequestParam("id") Long categoryId) {
        List<FestivalVO> festivalList = flowerCategoryService.readFestival(categoryId);
        return Result.success(festivalList);
    }
}
