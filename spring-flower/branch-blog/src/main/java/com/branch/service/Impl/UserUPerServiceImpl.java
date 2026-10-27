package com.branch.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.dto.UserUPerDTO;
import com.branch.domain.entity.UserUPer;
import com.branch.mapper.UserInfoMapper;
import com.branch.service.UserUPerService;

import org.springframework.stereotype.Service;

/**
 * 博主信息服务实现类 - 实现博主信息相关业务逻辑
 */
@Service
public class UserUPerServiceImpl extends ServiceImpl<UserInfoMapper, UserUPer> implements UserUPerService {
    @Override
    public void updateSelf(UserUPerDTO userUPerDTO) {

    }
}
