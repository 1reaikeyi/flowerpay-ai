package com.branch.controller;

import com.branch.domain.dto.EmployeeDTO;
import com.branch.domain.dto.EmployeePageDTO;
import com.branch.domain.dto.LoginDTO;
import com.branch.domain.dto.EditPasswordDTO;
import com.branch.domain.vo.EmployeeVO;
import com.branch.service.EmployeeService;
import framework.aop.oparation.OperationEnum;
import common.result.PageResult;
import common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import framework.aop.OperationLogging;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String token = employeeService.admin1(loginDTO);
        return Result.success(token);
    }
    @OperationLogging(operation = OperationEnum.CREATE)
    @PostMapping("/logout")
    public Result logout() {
        employeeService.admin2();
        return Result.success("logout");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/employee")
    public Result readById(@RequestParam Long id) {
        EmployeeVO employeeVO = employeeService.readById(id);
        return Result.success(employeeVO);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/all")
    public Result readPage( @Validated EmployeePageDTO employeePageDTO) {
        PageResult<EmployeeVO> employeeVOPageResult = employeeService.readPage(employeePageDTO);
        return Result.success(employeeVOPageResult);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.UPDATE)
    @PutMapping("/password")
    public Result updatePassword(@Validated @RequestBody EditPasswordDTO editPasswordDTO) {
        employeeService.updatePassword(editPasswordDTO);
        return Result.success("layout");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.UPDATE)
    @PutMapping
    public Result updateByObject(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateByObject(employeeDTO);
        return Result.success(employeeDTO.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.DELETE)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) {
        employeeService.deleteById(ids);
        return Result.success(ids);
    }
}
