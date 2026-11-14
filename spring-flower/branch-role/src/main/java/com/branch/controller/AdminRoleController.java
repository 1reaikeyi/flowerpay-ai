package com.branch.controller;

import com.branch.domain.dto.RoleDTO;
import com.branch.domain.dto.page.RolePageDTO;
import com.branch.domain.vo.RoleVO;
import com.branch.service.RoleService;
import com.branch.framework.aop.OperationLogging;
import com.branch.domain.OperationSuccessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.branch.result.PageResult;
import com.branch.result.Result;

import java.util.List;

@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {
    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasAuthority('admin:post')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping("/add")
    public Result addRole(@RequestBody RoleDTO roleDTO) {
        RoleDTO dto = roleService.createRole(roleDTO);
        return Result.success(dto);
    }
    @PreAuthorize("hasAuthority('admin:get')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/all")
    public Result getRoleList(@Validated RolePageDTO rolePageDTO ) {
        PageResult<RoleVO> roleList = roleService.read(rolePageDTO);
        return Result.success(roleList);
    }
    @PreAuthorize("hasAuthority('admin:put')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("/update")
    public Result updateRole(@RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return Result.success(roleDTO.getId());
    }
    
    @PreAuthorize("hasAuthority('admin:delete')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping("/delete")
    public Result deleteRole(List<Long> ids) {
        roleService.deleteRole(ids);
        return Result.success(ids);
    }
}
