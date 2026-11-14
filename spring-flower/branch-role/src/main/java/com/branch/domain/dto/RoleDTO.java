package com.branch.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
@Data
public class RoleDTO implements Serializable {
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
