package service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mapper.RolePermissionMapper;
import model.entity.RolePermission;
import org.springframework.stereotype.Service;
import service.RolePermissionService;
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
}
