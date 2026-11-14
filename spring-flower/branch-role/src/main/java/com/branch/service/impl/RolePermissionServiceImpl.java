package com.branch.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.entity.RolePermission;
import com.branch.mapper.RolePermissionMapper;
import com.branch.service.RolePermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色权限关联表 服务实现类
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}
