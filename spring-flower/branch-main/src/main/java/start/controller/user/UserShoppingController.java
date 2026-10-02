package start.controller.user;

import framework.aop.oparation.OperationEnum;
import common.result.Result;
import model.dto.UserShoppingDTO;
import model.vo.UserShoppingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.UserShoppingService;
import framework.aop.OperationLogging;


import java.util.List;

@RestController
@RequestMapping("/user/shopping")
public class UserShoppingController {
    @Autowired
    private UserShoppingService userShoppingService;

    @PreAuthorize("hasAnyRole('USER')")
    @OperationLogging(operation = OperationEnum.CREATE)
    @PostMapping
    public Result create(@Validated @RequestBody UserShoppingDTO userShoppingDTO){
        UserShoppingDTO dto = userShoppingService.create(userShoppingDTO);
        return Result.success(dto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping
    public Result readAll(){
        List<UserShoppingVO> userShoppingVOList = userShoppingService.readAll();
        return Result.success(userShoppingVOList);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @OperationLogging(operation = OperationEnum.DELETE)
    @DeleteMapping
    public Result delete(@RequestParam Long id){
        userShoppingService.delete(id);
        return Result.success(id);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @OperationLogging(operation = OperationEnum.DELETE)
    @DeleteMapping("/all")
    public Result deleteAll(){
        userShoppingService.deleteAll();
        return Result.success();
    }
}
