package com.branch.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.dto.RoleDTO;
import com.branch.domain.dto.page.RolePageDTO;
import com.branch.domain.entity.Role;
import com.branch.domain.vo.RoleVO;
import com.branch.mapper.RoleMapper;
import com.branch.service.RoleService;
import common.constant.ErrorConstant;
import common.exception.EmployeeFailedException;
import org.springframework.stereotype.Service;
import com.branch.result.PageResult;

import java.util.List;
import java.util.stream.Collectors;

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
    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = BeanUtil.toBean(roleDTO, Role.class);
        this.save(role);
        RoleDTO dto = BeanUtil.toBean(role, RoleDTO.class);
        return dto;
    }
    @Override
    public PageResult<RoleVO> read(RolePageDTO rolePageDTO) {
        IPage page = new Page(rolePageDTO.getPage(),rolePageDTO.getPageSize());
        IPage<Role> roles = this.page(page);
        List<RoleVO> voList = roles.getRecords().stream()
                .map(role -> BeanUtil.copyProperties(role, RoleVO.class))
                .collect(Collectors.toList());
        PageResult<RoleVO> result = new PageResult<>();
        result.setTotal(roles.getTotal());
        result.setList(voList);                         // 当前页数据
        result.setPageNum(roles.getCurrent());  // 当前页码
        result.setPageSize(roles.getSize());    // 每页条数
        return result;
    }

    @Override
    public void updateRole(RoleDTO roleDTO) {
        if (roleDTO.getId() == null) {
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }
    }

    @Override
    public void deleteRole(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }
        this.removeByIds(ids);
    }
}
