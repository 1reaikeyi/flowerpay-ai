package com.branch.filter;

import com.branch.service.login.LoginAdminService;
import framework.properties.JwtProperties;

import common.constant.JwtConstant;
import common.constant.RedisPrefixConstant;

import common.RoleConstant;
import framework.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AdminRefreshRequestFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final LoginAdminService loginAdminService;

    public AdminRefreshRequestFilter(JwtProperties jwtProperties,
                                     StringRedisTemplate stringRedisTemplate,
                                     LoginAdminService loginAdminService) {
        this.jwtProperties = jwtProperties;
        this.stringRedisTemplate = stringRedisTemplate;
        this.loginAdminService = loginAdminService;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);
        if (token == null) {
            // 没有 token，放行，由后面的认证过滤器决定是否 401
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Map<String, Object> claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            if (claims == null) {
                filterChain.doFilter(request, response);
                return;
            }
            String adminId = claims.get(JwtConstant.ID).toString();
            String adminName = claims.get(JwtConstant.NAME).toString();
            String type = claims.get(JwtConstant.Role).toString();
           
            if (!RoleConstant.ROLE_ADMIN.equals(type)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 从 Redis 中查找 admin token（使用正确的 ADMIN_AUTH_PREFIX 前缀）
            String standardToken = stringRedisTemplate.opsForValue().get(RedisPrefixConstant.ADMIN_AUTH_PREFIX + adminId);
            if (!token.equals(standardToken)) {
                log.error("admin Token 验证失败，已注销登录" + adminName);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 滑动过期：admin token 使用 ADMIN_AUTH_PREFIX
            stringRedisTemplate.expire(RedisPrefixConstant.ADMIN_AUTH_PREFIX + adminId,
                    jwtProperties.getAdminTtl(), TimeUnit.SECONDS);

            UserDetails userDetails = loginAdminService.loadUserByUsername(adminName);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,null, userDetails.getAuthorities());
            log.info(userDetails.toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // token 已过期 → 401，前端应引导重新登录
            log.error("admin Token 已过期: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            // 签名不匹配，说明不是 emp token（可能是 user token），交给下一个过滤器处理
            log.debug("admin 过滤器签名不匹配，交给下一个过滤器: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }

    }
}
