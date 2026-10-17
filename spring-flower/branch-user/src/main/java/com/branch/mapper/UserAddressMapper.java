package com.branch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.branch.domain.entity.UserAddress;
import org.springframework.stereotype.Repository;

/**
 * 用户地址簿 Mapper（对应 user_address 表）
 */
@Repository
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
