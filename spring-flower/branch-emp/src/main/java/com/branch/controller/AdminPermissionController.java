package com.branch.controller;

import com.branch.domain.dto.PermissionDTO;
import com.branch.domain.vo.PermissionVO;
import com.branch.service.PermissionService;
import framework.aop.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.branch.result.Result;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/permission")
public class AdminPermissionController {

    @Autowired
    private PermissionService permissionService;

    @Logging
    @PostMapping
    public Result addPermission(@RequestBody PermissionDTO permissionDTO) {
        PermissionDTO dto = permissionService.createPermission(permissionDTO);
        return Result.success(dto);
    }
    @Logging
    @GetMapping("/all")
    public Result getRolePermissions() {
        Map<String, List<PermissionVO>> vo = permissionService.read();
        return Result.success(vo);
    }
    @Logging
    @PutMapping
    public Result updatePermission(@RequestBody PermissionDTO permissionDTO) {
        permissionService.updatePermission(permissionDTO);
        return Result.success(permissionDTO.getId());
    }
    @Logging
    @DeleteMapping
    public Result deletePermission(List<Long> ids) {
        permissionService.delete(ids);
        return Result.success(ids);
    }
}
