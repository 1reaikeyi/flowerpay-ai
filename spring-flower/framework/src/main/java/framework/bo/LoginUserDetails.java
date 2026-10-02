package framework.bo;

import lombok.Getter;
import model.constant.StatusConstant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoginUserDetails implements UserDetails {

    private final UserBO userBO;

    public LoginUserDetails(UserBO userBO) {
        this.userBO = userBO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 边界层：把业务层的 role（角色）与 permission（权限点）统一转换为框架要求的 GrantedAuthority 集合，
        // 供 hasRole / hasAuthority 鉴权使用；Filter 与登录链路均复用此方法，保持单一权威来源
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (userBO.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority(userBO.getRole()));
        }
        if (userBO.getPermission() != null) {
            for (String perm : userBO.getPermission()) {
                authorities.add(new SimpleGrantedAuthority(perm));
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return userBO.getPassword();
    }

    @Override
    public String getUsername() {
        return userBO.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return userBO.getStatus().equals(StatusConstant.ENABLE); }

    public UserBO getUserBO(){
        return userBO;
    }
}