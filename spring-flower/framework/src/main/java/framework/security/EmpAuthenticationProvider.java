package framework.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/**
 * 员工认证 Provider
 * 只接受 EmpAuthenticationToken，自动路由到 LoginEmpService 查询 employee 表
 */
public class EmpAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public boolean supports(Class<?> authentication) {
        return EmpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
