package service.authentication;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import framework.bo.LoginUserDetails;
import framework.bo.UserBO;
import mapper.RolePermissionMapper;
import mapper.UserMapper;
import model.constant.RoleConstant;
import model.constant.StatusConstant;
import model.entity.RolePermission;
import model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通用户认证：查询 user 表，赋予 ROLE_USER
 */
@Service
public class LoginUserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRole, RoleConstant.ROLE_USER)
                        .eq(RolePermission::getStatus, StatusConstant.ENABLE)
        );
        List<String> permissionList = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissions) {
            permissionList.add(rolePermission.getPermissionCode());
        }
        UserBO userBO = new UserBO(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                permissionList,
                RoleConstant.ROLE_USER,
                user.getStatus()
        );
        return new LoginUserDetails(userBO);
    }
}
