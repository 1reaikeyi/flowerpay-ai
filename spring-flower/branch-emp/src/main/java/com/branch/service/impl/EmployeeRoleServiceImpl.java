package com.branch.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.entity.EmployeeRole;
import com.branch.mapper.EmployeeRoleMapper;
import com.branch.service.EmployeeRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */
@Service
public class EmployeeRoleServiceImpl extends ServiceImpl<EmployeeRoleMapper, EmployeeRole> implements EmployeeRoleService {

}
