package com.branch.service;




import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.*;
import com.branch.domain.dto.page.EmployeePageDTO;
import com.branch.domain.entity.Employee;
import com.branch.domain.vo.EmployeeVO;
import com.branch.result.PageResult;

import java.util.List;

/**
 * 员工 Service（对应 employee 表）
 */

public interface EmployeeService extends IService<Employee> {

    Employee findEmployeename(String username);

    void registerAdmin(EmployeeDTO employeeDTO);
    void registerEmployee(EmployeeDTO employeeDTO);
    String login(LoginDTO loginDTO);
    void logout();

    EmployeeVO readById(Long id);
    PageResult<EmployeeVO> readPage(EmployeePageDTO employeePageDTO);
    void updateBySelf(EmployeeDTO employeeDTO);
    void deleteById(List<Long> ids);
    void updatePassword(EditPasswordDTO editPasswordDTO);

    void postRoleTOEmployee(EmployeeRoleDTO employeeRoleDTO);

    void deleteRoleTOEmployee(EmployeeRoleDTO employeeRoleDTO);

    void postRoleTOPermission(RolePermissionDTO rolePermissionDTO);

    void deleteRoleTOPermission(RolePermissionDTO rolePermissionDTO);
}
