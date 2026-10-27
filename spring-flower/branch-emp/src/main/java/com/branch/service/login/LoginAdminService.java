package com.branch.service.login;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.branch.domain.entity.*;
import com.branch.mapper.*;
import com.branch.common.constant.ErrorConstant;
import com.branch.common.StatusConstant;
import com.branch.common.exception.LoginFailedException;
import framework.security.LoginUserDetails;
import framework.bo.UserBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员认证：查询 employee 表，赋予 ROLE_ADMIN
 */
@Service
@Slf4j
public class LoginAdminService implements UserDetailsService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private EmployeeRoleMapper employeeRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.检验用户名
        Employee dbUser = employeeMapper.selectOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUsername, username));
        if (dbUser == null) {
            throw new LoginFailedException("管理员不存在: " + username);
        }
        // 2. 校验用户状态
        if (!StatusConstant.ENABLE.equals(dbUser.getStatus())) {
            log.info("登录用户：{} 已被停用", username);
            throw new LoginFailedException(ErrorConstant.USER_DISABLED);
        }

        // 3. 加载用户角色编码集合
        Set<String> roles = loadRoles(dbUser.getId());

        // 4. 加载用户权限标识集合
        Set<String> permissions = loadPermissions(dbUser.getId(), roles);
        // 5.context信息
        UserBO userBO = BeanUtil.toBean(dbUser, UserBO.class);

        LoginUserDetails loginUserDetails = new LoginUserDetails();
        loginUserDetails.setUserBO(userBO);
        loginUserDetails.setRoles(roles);
        loginUserDetails.setPermissions(permissions);
        return loginUserDetails;
    }

    private List<Long> load1(Long userId) {
        List<EmployeeRole> userRoles = employeeRoleMapper.selectList(
                new LambdaQueryWrapper<EmployeeRole>().eq(EmployeeRole::getUserId, userId));
        if (userRoles == null || userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream()
                .map(EmployeeRole::getRoleId)
                .collect(Collectors.toList());
        return roleIds;
    }
    /**
     * 加载用户的角色编码集合
     */
    private Set<String> loadRoles(Long userId) {
        List<Long> roleIds = load1(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        return roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());
    }

    private List<Long> load2(Long userId) {
        List<EmployeeRole> userRoles = employeeRoleMapper.selectList(
                new LambdaQueryWrapper<EmployeeRole>().eq(EmployeeRole::getUserId, userId));
        if (userRoles == null || userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream()
                .map(EmployeeRole::getRoleId)
                .collect(Collectors.toList());
        return roleIds;
    }
    /**
     * 加载用户的权限标识集合
     */
    private Set<String> loadPermissions(Long userId, Set<String> roles) {
        List<Long> roleIds = load1(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds));
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return Collections.emptySet();
        }

        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptySet();
        }

        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }
}
