package com.branch.controller;


import com.branch.domain.dto.*;
import com.branch.domain.dto.page.EmployeePageDTO;
import com.branch.domain.vo.EmployeeVO;
import com.branch.service.*;
import framework.aop.oparation.OperationSuccessEnum;
import com.branch.result.PageResult;
import com.branch.result.Result;
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

    @PostMapping("/register/admin")
    public Result registerAdmin(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.registerAdmin(employeeDTO);
        return Result.success("register");
    }

    @PostMapping("/register/employee")
    public Result registerEmployee(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.registerEmployee(employeeDTO);
        return Result.success("register");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String token = employeeService.login(loginDTO);
        return Result.success(token);
    }

    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping("/logout")
    public Result logout() {
        employeeService.logout();
        return Result.success("logout");
    }
    @PreAuthorize("hasAnyAuthority('admin:read')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/employee")
    public Result readById(@RequestParam Long id) {
        EmployeeVO employeeVO = employeeService.readById(id);
        return Result.success(employeeVO);
    }
    @PreAuthorize("hasAnyAuthority('admin:read')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/all")
    public Result readPage( @Validated EmployeePageDTO employeePageDTO) {
        PageResult<EmployeeVO> employeeVOPageResult = employeeService.readPage(employeePageDTO);
        return Result.success(employeeVOPageResult);
    }
    @PreAuthorize("hasAnyAuthority('admin:delete')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping("/employee")
    public Result deleteById(@RequestParam List<Long> ids) {
        employeeService.deleteById(ids);
        return Result.success(ids);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("/password")
    public Result updatePassword(@Validated @RequestBody EditPasswordDTO editPasswordDTO) {
        employeeService.updatePassword(editPasswordDTO);
        return Result.success("layout");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("/employee")
    public Result updateBySelf(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateBySelf(employeeDTO);
        return Result.success(employeeDTO.getId());
    }

    @PreAuthorize("hasAnyAuthority('admin:role:post')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping("/role/of/employee")
    public Result postRoleTOEmployee(@RequestBody EmployeeRoleDTO employeeRoleDTO) {
        employeeService.postRoleTOEmployee(employeeRoleDTO);
        return Result.success(employeeRoleDTO.getRoleId() + employeeRoleDTO.getUserId());
    }
    @PreAuthorize("hasAnyAuthority('admin:role:delete')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping("/role/of/employee")
    public Result deleteRoleTOEmployee(@RequestBody EmployeeRoleDTO employeeRoleDTO) {
        employeeService.deleteRoleTOEmployee(employeeRoleDTO);
        return Result.success(employeeRoleDTO.getRoleId() + employeeRoleDTO.getUserId());
    }
    @PreAuthorize("hasAnyAuthority('admin:permission:post')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping("/role/of/permission")
    public Result postRoleTOPermission(@RequestBody RolePermissionDTO rolePermissionDTO) {
        employeeService.postRoleTOPermission(rolePermissionDTO);
        return Result.success(rolePermissionDTO.getRoleId() +rolePermissionDTO.getPermissionId());
    }
    @PreAuthorize("hasAnyAuthority('admin:permission:delete')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping("/role/of/permission")
    public Result deleteRoleTOPermission(@RequestBody RolePermissionDTO rolePermissionDTO) {
        employeeService.deleteRoleTOPermission(rolePermissionDTO);
        return Result.success(rolePermissionDTO.getRoleId() +rolePermissionDTO.getPermissionId());
    }

}
