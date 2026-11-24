package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.dto.UserShoppingDTO;
import com.branch.model.entity.UserShopping;
import com.branch.model.vo.UserShoppingVO;

import java.util.List;


/**
 * 购物车 Service（对应 user_shopping 表）
 */

public interface UserShoppingService extends IService<UserShopping> {
    void deleteAll();
    void delete(Long id);
    List<UserShoppingVO> readAll();
    UserShoppingDTO create(UserShoppingDTO userShoppingDTO);

}
