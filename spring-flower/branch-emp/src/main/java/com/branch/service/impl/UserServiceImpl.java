package com.branch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.service.UserService;
import common.constant.ErrorConstant;
import common.constant.JwtConstant;
import common.constant.RedisPrefixConstant;
import common.exception.LoginFailedException;
import common.exception.UserFailedException;

import common.constant.RoleConstant;
import framework.properties.JwtProperties;
import framework.util.JwtUtil;
import com.branch.mapper.UserMapper;
import com.branch.domain.dto.LoginDTO;
import com.branch.domain.dto.UserDTO;
import com.branch.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import framework.security.UserAuthenticationToken;
import framework.security.SecurityContextParam;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户 Service（对应 user 表）
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入密码加密器 Bean
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AuthenticationManager authenticationManager; // 注入认证管理器

    @Override
    public User findUsername(String username) {
        return this.lambdaQuery().eq(User::getUsername, username).one();
    }

    @Override
    public void register(UserDTO userDTO) {
        User user = this.findUsername(userDTO.getUsername());
        if (user != null) {
            throw new UserFailedException(ErrorConstant.USERNAME_EXIST);
        }
        if(user.getPassword() == null) {
            user.setPassword(passwordEncoder.encode("123456"));
        }
        User user1 = BeanUtil.copyProperties(userDTO, User.class);
        user1.setPassword(user1.getPassword());
        save(user1);
    }

    @Override
    public String login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 使用 UserAuthenticationToken → 自动路由到 UserAuthenticationProvider → LoginUserService
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(username, password);
        authenticationManager.authenticate(authenticationToken);

        User user = this.findUsername(username);
        Map<String,Object> map = new HashMap<>();
        map.put(JwtConstant.ID, user.getId());
        map.put(JwtConstant.NAME, user.getUsername());
        map.put(JwtConstant.Role,RoleConstant.ROLE_USER);

        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), map);
        stringRedisTemplate.opsForValue().set(RedisPrefixConstant.USER_AUTH_PREFIX+ user.getId(), token,
                jwtProperties.getAdminTtl(), TimeUnit.SECONDS);
        return token;
    }

    @Override
    public void logout() {
        // 获取当前登录用户ID
        Long userId = SecurityContextParam.getCurrentUserId();
        if (userId == null) {
            throw new LoginFailedException(ErrorConstant.ACCOUNT_NOT_EXIST);
        }
        stringRedisTemplate.delete(RedisPrefixConstant.USER_AUTH_PREFIX + userId);
        SecurityContextHolder.clearContext();
    }

    @Override
    public void updateByObject(UserDTO userDTO) {

    }
}
