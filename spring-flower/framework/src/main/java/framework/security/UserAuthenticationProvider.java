package framework.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/**
 * 普通用户认证 Provider
 * 只接受 UserAuthenticationToken，自动路由到 LoginUserService 查询 user 表
 */
public class UserAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public boolean supports(Class<?> authentication) {
        return UserAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

