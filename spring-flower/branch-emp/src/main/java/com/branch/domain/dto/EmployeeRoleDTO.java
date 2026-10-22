package com.branch.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeRoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */

    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

}
