package com.branch.service.impl;

import com.branch.domain.entity.LoginLog;
import com.branch.mapper.LoginLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.service.LoginLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统访问记录 服务实现类
 * </p>
 *
 * @author flower
 * @since 2026-10-25
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

}
