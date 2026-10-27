package com.branch.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */

    private Long id;

    /**
     * 角色编码
     */

    private String roleCode;

    /**
     * 角色名称
     */

    private String roleName;

    /**
     * 排序
     */

    private Integer sort;

    /**
     * 状态 0禁用 1启用
     */

    private Byte status;
}
