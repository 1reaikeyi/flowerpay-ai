package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.PermissionDTO;
import com.branch.domain.entity.Permission;
import com.branch.domain.vo.PermissionVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */

public interface PermissionService extends IService<Permission> {

    void updatePermission(PermissionDTO permissionDTO);

    void delete(List<Long> ids);

    Map<String, List<PermissionVO>> read();

    PermissionDTO createPermission(PermissionDTO permissionDTO);
}
