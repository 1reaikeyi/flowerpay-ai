package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.LoginDTO;
import com.branch.domain.dto.UserDTO;
import com.branch.domain.entity.User;

/**
 * 用户 Service（对应 user 表）
 */

public interface UserService extends IService<User> {

    User findUsername(String username);
    void register(UserDTO userDTO);
    String login(LoginDTO loginDTO);

    void logout();
    void updateByObject(UserDTO userDTO);
}
