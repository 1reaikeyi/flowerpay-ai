package com.branch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.branch.domain.entity.User;
import org.springframework.stereotype.Repository;

/**
 * 用户 Mapper（对应 user 表）
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
}
