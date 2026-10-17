package com.branch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.branch.domain.entity.UserShopping;
import org.springframework.stereotype.Repository;

/**
 * 购物车 Mapper（对应 user_shopping 表）
 */
@Repository
public interface UserShoppingMapper extends BaseMapper<UserShopping> {
}
