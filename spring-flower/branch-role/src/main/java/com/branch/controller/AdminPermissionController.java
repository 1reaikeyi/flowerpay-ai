package com.branch.controller;

import com.branch.domain.dto.PermissionDTO;
import com.branch.domain.vo.PermissionVO;
import com.branch.service.PermissionService;
import com.branch.framework.aop.OperationLogging;
import com.branch.domain.OperationSuccessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.branch.result.Result;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/permission")
public class AdminPermissionController {

    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("hasAuthority('admin:post')")
    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping("/add")
    public Result addPermission(@RequestBody PermissionDTO permissionDTO) {
        PermissionDTO dto = permissionService.createPermission(permissionDTO);
        return Result.success(dto);
    }
    @PreAuthorize("hasAuthority('admin:get')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/all")
    public Result getRolePermissions() {
        Map<String, List<PermissionVO>> vo = permissionService.read();
        return Result.success(vo);
    }
    @PreAuthorize("hasAuthority('admin:put')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("/update")
    public Result updatePermission(@RequestBody PermissionDTO permissionDTO) {
        permissionService.updatePermission(permissionDTO);
        return Result.success(permissionDTO.getId());
    }
    @PreAuthorize("hasAuthority('admin:delete')")
    @OperationLogging(operation = OperationSuccessEnum.DELETE)
    @DeleteMapping("/delete")
    public Result deletePermission(List<Long> ids) {
        permissionService.delete(ids);
        return Result.success(ids);
    }
}
