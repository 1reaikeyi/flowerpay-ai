package com.branch.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.branch.domain.entity.Permission;
import com.branch.domain.vo.RolePermissionVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author flower
 * @since 2026-10-16
 */
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 三表连接查询：role - role_permission - permission
     */
    List<RolePermissionVO> selectRolePermissions();
}
