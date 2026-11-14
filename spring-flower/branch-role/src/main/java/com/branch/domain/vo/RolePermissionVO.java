package com.branch.domain.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色-权限关联视图对象（三表连接查询结果）
 * </p>
 *
 * @author flower
 * @since 2026-10-21
 */
@Getter
@Setter
public class RolePermissionVO {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 权限信息（角色无权限时为 null）
     */
    private PermissionVO permission;
}
