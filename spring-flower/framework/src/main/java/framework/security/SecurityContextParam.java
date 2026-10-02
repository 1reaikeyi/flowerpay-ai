package framework.security;


import framework.bo.LoginUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Spring Security 用户信息工具类
 *
 */
public class SecurityContextParam {

    /**
     * 获取当前登录用户的ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDetails loginUserDetails) {
            return loginUserDetails.getUserBO().getId();
        }
        return 0L;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDetails loginUserDetails) {
            return loginUserDetails.getUserBO().getUsername();
        }
        return null;
    }
    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDetails loginUserDetails) {
            return loginUserDetails.getUserBO().getRole();
        }
        return null;
    }
    /**
     * 获取当前登录用户权限
     */
    public static List<String> getCurrentPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDetails loginUserDetails) {
            return loginUserDetails.getUserBO().getPermission();
        }
        return null;
    }
}