package com.branch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 路由
     */
    private String path;

    /**
     * 方法
     */
    private String method;

    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态 0禁用 1启用
     */
    private Byte status;

    /**
     * 备注
     */
    private String remark;

}
