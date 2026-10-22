package framework.security;


import common.RoleConstant;
import common.StatusConstant;
import framework.bo.UserBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 作为 Spring Security 的 UserDetails 实现，同时承载：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDetails implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户信息 */
    private UserBO userBO;

    /** 权限集合：权限标识 permission_code */
    private Set<String> permissions;

    /** 角色集合：角色编码 role_code */
    private Set<String> roles;

    /** 登录时间（毫秒时间戳），用于 Token 续期判断 */
    private Long loginTime;

    /** 过期时间（毫秒时间戳），用于 Token 续期判断 */
    private Long expireTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        // 权限标识直接作为 authority，如 admin:read（对应 @PreAuthorize("hasAnyAuthority(...)")）
        if (permissions != null) {
            permissions.stream()
                    .filter(org.springframework.util.StringUtils::hasText)
                    .forEach(code -> authorities.add(new SimpleGrantedAuthority(code)));
        }
//         角色统一补 ROLE_ 前缀并转大写
         if (roles != null) {
             roles.stream()
                     .filter(org.springframework.util.StringUtils::hasText)
                     .map(role -> role.startsWith("ROLE_")
                             ? role.toUpperCase()
                             : "ROLE_" + role.toUpperCase())
                     .forEach(roleAuthority -> authorities.add(new SimpleGrantedAuthority(roleAuthority)));
         }
        // 默认添加 admin 角色
        // 如 admin -> ROLE_ADMIN（对应 hasRole('ADMIN')）
        authorities.add(new SimpleGrantedAuthority(RoleConstant.ROLE_ADMIN));
        return authorities;
    }

    /**
     * 返回数据库中加密后的密码（BCrypt）
     */
    @Override
    public String getPassword() {
        return userBO == null ? null : userBO.getPassword();
    }

    /**
     * 返回登录用户名
     */
    @Override
    public String getUsername() {
        return userBO == null ? null : userBO.getUsername();
    }

    /**
     * 账号是否未过期（true=正常）
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账号是否未锁定（true=正常）
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证（密码）是否未过期（true=正常）
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账号是否启用（true=正常可用）
     */
    @Override
    public boolean isEnabled() {
        return userBO != null && StatusConstant.ENABLE.equals(userBO.getStatus());
    }

}
