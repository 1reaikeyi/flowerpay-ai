package com.branch.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RolePermissionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */

    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permissionId;
}
