package com.branch.controller;

import com.branch.domain.dto.EmployeeDTO;
import com.branch.domain.dto.EmployeePageDTO;
import com.branch.domain.dto.EditPasswordDTO;
import com.branch.domain.vo.EmployeeVO;
import com.branch.service.*;
import framework.aop.oparation.OperationEnum;
import framework.result.PageResult;
import framework.result.Result;
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
    @Autowired
    private RoleService roleService;
    @Autowired
    private EmployeeRoleService employeeRoleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RolePermissionService rolePermissionService;

    @PreAuthorize("hasAnyAuthority('admin:read')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/employee")
    public Result readById(@RequestParam Long id) {
        EmployeeVO employeeVO = employeeService.readById(id);
        return Result.success(employeeVO);
    }
    @PreAuthorize("hasAnyAuthority('admin:read')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/all")
    public Result readPage( @Validated EmployeePageDTO employeePageDTO) {
        PageResult<EmployeeVO> employeeVOPageResult = employeeService.readPage(employeePageDTO);
        return Result.success(employeeVOPageResult);
    }
    @PreAuthorize("hasAnyAuthority('admin:delete')")
    @OperationLogging(operation = OperationEnum.DELETE)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids) {
        employeeService.deleteById(ids);
        return Result.success(ids);
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

}
