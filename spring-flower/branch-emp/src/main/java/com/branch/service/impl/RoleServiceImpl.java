package com.branch.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.entity.Role;
import com.branch.mapper.RoleMapper;
import com.branch.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
