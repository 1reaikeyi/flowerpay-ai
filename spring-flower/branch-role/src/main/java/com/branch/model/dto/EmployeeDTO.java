package com.branch.model.dto;

import com.branch.framework.validation.SexValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 工作
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
    @SexValid
    private String sex;

    /**
     * 状态 0:禁用，1:启用
     */
    
    private Long status;

}
