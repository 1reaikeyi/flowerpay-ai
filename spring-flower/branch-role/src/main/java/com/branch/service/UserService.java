package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.dto.LoginDTO;
import com.branch.model.dto.UserDTO;
import com.branch.model.entity.User;

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
