package service.authentication;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import framework.bo.LoginUserDetails;
import framework.bo.UserBO;
import mapper.EmployeeMapper;
import mapper.RolePermissionMapper;
import model.constant.RoleConstant;
import model.constant.StatusConstant;
import model.entity.Employee;
import model.entity.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 管理员认证：查询 employee 表，赋予 ROLE_ADMIN
 * （管理员与员工共用 employee 表，仅角色不同）
 */
@Service
public class LoginAdminService implements UserDetailsService {

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
            throw new UsernameNotFoundException("管理员不存在: " + username);
        }
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRole, RoleConstant.ROLE_ADMIN)
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
                RoleConstant.ROLE_ADMIN,
                employee.getStatus()
        );
        return new LoginUserDetails(userBO);
    }
}
