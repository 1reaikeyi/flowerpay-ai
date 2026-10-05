package com.branch.service.login;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import framework.bo.LoginUserDetails;
import framework.bo.UserBO;
import com.branch.mapper.EmployeeMapper;
import com.branch.mapper.RolePermissionMapper;
import common.constant.RoleConstant;
import common.constant.StatusConstant;
import com.branch.domain.entity.Employee;
import com.branch.domain.entity.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工认证：查询 employee 表，赋予 ROLE_EMP
 */
@Service
public class LoginEmpService implements UserDetailsService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeMapper.selectOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUsername, username));
        if (employee == null) {
            throw new UsernameNotFoundException("员工不存在: " + username);
        }
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRole, RoleConstant.ROLE_EMP)
                        .eq(RolePermission::getStatus, StatusConstant.ENABLE)
        );
        List<String> permissionList = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissions) {
            permissionList.add(rolePermission.getPermissionCode());
        }
        UserBO userBO = new UserBO(
                employee.getId(),
                employee.getUsername(),
                employee.getPassword(),
                permissionList,
                RoleConstant.ROLE_EMP,
                employee.getStatus()
        );
        return new LoginUserDetails(userBO);
    }
}
