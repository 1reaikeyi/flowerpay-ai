package com.branch.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.dto.PermissionDTO;
import com.branch.domain.entity.Permission;
import com.branch.domain.vo.PermissionVO;
import com.branch.domain.vo.RolePermissionVO;
import com.branch.mapper.PermissionMapper;
import com.branch.service.PermissionService;
import common.constant.ErrorConstant;
import common.exception.EmployeeFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        Permission permission = BeanUtil.toBean(permissionDTO, Permission.class);
        this.save(permission);
        PermissionDTO dto = BeanUtil.toBean(permission, PermissionDTO.class);
        return dto;
    }

    @Override
    public void updatePermission(PermissionDTO permissionDTO) {
        if (permissionDTO.getId() == null) {
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }
        LambdaUpdateWrapper<Permission> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Permission::getId, permissionDTO.getId());
        if(permissionDTO.getPermissionCode() != null) {
            wrapper.set(Permission::getPermissionCode, permissionDTO.getPermissionCode());
        }
        if(permissionDTO.getPermissionName() != null) {
            wrapper.set(Permission::getPermissionName, permissionDTO.getPermissionName());
        }
        if(permissionDTO.getPath() != null) {
            wrapper.set(Permission::getPath, permissionDTO.getPath());
        }
        if(permissionDTO.getMethod() != null) {
            wrapper.set(Permission::getMethod, permissionDTO.getMethod());
        }
        if(permissionDTO.getSort() != null) {
            wrapper.set(Permission::getSort, permissionDTO.getSort());
        }
        if(permissionDTO.getStatus() != null) {
            wrapper.set(Permission::getStatus, permissionDTO.getStatus());
        }
        if(permissionDTO.getRemark() != null) {
            wrapper.set(Permission::getRemark, permissionDTO.getRemark());
        }
        this.update(wrapper);
    }

    @Override
    public void delete(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }
        this.removeByIds(ids);
    }

    @Override
    public Map<String, List<PermissionVO>> read() {
        List<RolePermissionVO> rolePermissions = permissionMapper.selectRolePermissions();

        Map<String, List<PermissionVO>> map = new HashMap<>();
        for (RolePermissionVO rolePermission : rolePermissions) {
            List<PermissionVO> list = map.computeIfAbsent(rolePermission.getRoleName(), k -> new ArrayList<>());
            if (rolePermission.getPermission() != null) {
                list.add(rolePermission.getPermission());
            }
        }
        return map;
    }

}
