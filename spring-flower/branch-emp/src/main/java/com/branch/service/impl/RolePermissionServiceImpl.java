package com.branch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.mapper.RolePermissionMapper;
import com.branch.domain.entity.RolePermission;
import org.springframework.stereotype.Service;
import com.branch.service.RolePermissionService;
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
}
