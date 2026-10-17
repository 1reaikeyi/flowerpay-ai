package com.branch.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.entity.Permission;
import com.branch.mapper.PermissionMapper;
import com.branch.service.PermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
