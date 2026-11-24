package com.branch.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.dto.RoleDTO;
import com.branch.model.dto.page.RolePageDTO;
import com.branch.model.entity.Role;
import com.branch.model.vo.RoleVO;
import com.branch.result.PagePart;

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

    PagePart<RoleVO> read(RolePageDTO rolePageDTO);

    RoleDTO createRole(RoleDTO roleDTO);
}
