package com.branch.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.branch.domain.dto.PermissionDTO;
import com.branch.domain.dto.RoleDTO;
import com.branch.domain.dto.page.RolePageDTO;
import com.branch.domain.vo.PermissionVO;
import com.branch.domain.vo.RoleVO;
import com.branch.service.PermissionService;
import com.branch.service.RoleService;
import framework.aop.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import result.PageResult;
import result.Result;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {
    @Autowired
    private RoleService roleService;

    @Logging
    @PostMapping
    public Result addRole(@RequestBody RoleDTO roleDTO) {
        RoleDTO dto = roleService.createRole(roleDTO);
        return Result.success(dto);
    }
    @Logging
    @GetMapping("/all")
    public Result getRoleList(@Validated RolePageDTO rolePageDTO ) {
        PageResult<RoleVO> roleList = roleService.read(rolePageDTO);
        return Result.success(roleList);
    }
    @Logging
    @PutMapping
    public Result updateRole(@RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return Result.success(roleDTO.getId());
    }
    @Logging
    @DeleteMapping
    public Result deleteRole(List<Long> ids) {
        roleService.deleteRole(ids);
        return Result.success(ids);
    }
}
