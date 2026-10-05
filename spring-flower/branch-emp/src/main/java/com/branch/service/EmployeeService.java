package com.branch.service;




import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.EmployeeDTO;
import com.branch.domain.dto.EmployeePageDTO;
import com.branch.domain.dto.LoginDTO;
import com.branch.domain.dto.EditPasswordDTO;
import com.branch.domain.entity.Employee;
import com.branch.domain.vo.EmployeeVO;
import common.result.PageResult;

import java.util.List;

/**
 * 员工 Service（对应 employee 表）
 */

public interface EmployeeService extends IService<Employee> {

    Employee findEmployeename(String username);

    String admin1(LoginDTO loginDTO);
    void admin2();

    void register(EmployeeDTO employeeDTO);
    String login(LoginDTO loginDTO);
    void logout();

    EmployeeVO readById(Long id);
    PageResult<EmployeeVO> readPage(EmployeePageDTO employeePageDTO);
    void updateByObject(EmployeeDTO employeeDTO);
    void deleteById(List<Long> ids);
    void updatePassword(EditPasswordDTO editPasswordDTO);
}
