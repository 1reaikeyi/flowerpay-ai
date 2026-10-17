package com.branch.service.login;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import framework.security.LoginUserDetails;
import framework.bo.UserBO;
import com.branch.mapper.UserMapper;
import com.branch.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 普通用户认证：查询 user 表，赋予 ROLE_USER
 */
@Service
public class LoginUserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        UserBO userBO = new UserBO();
        return new LoginUserDetails(userBO,
                null,
                null,
                null,
                null);
    }
}
