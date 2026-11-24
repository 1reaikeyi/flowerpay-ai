package com.branch.model.dto;

import com.branch.model.entity.Role;
import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 在职部门
     */
    private String work;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private String sex;

    private Role role;
}
