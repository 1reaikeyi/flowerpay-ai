package com.branch.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.RoleDTO;
import com.branch.domain.dto.page.RolePageDTO;
import com.branch.domain.entity.Role;
import com.branch.domain.vo.RoleVO;
import result.PageResult;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */
public interface RoleService extends IService<Role> {

    void updateRole(RoleDTO roleDTO);

    void deleteRole(List<Long> ids);

    PageResult<RoleVO> read(RolePageDTO rolePageDTO);

    RoleDTO createRole(RoleDTO roleDTO);
}
