package framework.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/**
 * 管理员认证 Provider
 * 只接受 AdminAuthenticationToken，自动路由到 LoginAdminService 查询 employee 表
 */
public class AdminAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public boolean supports(Class<?> authentication) {
        return AdminAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
