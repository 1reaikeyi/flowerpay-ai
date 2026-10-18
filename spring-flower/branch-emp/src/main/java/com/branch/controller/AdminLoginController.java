package com.branch.controller;

import com.branch.domain.dto.EmployeeDTO;
import com.branch.domain.dto.LoginDTO;
import com.branch.service.EmployeeService;
import result.Result;
import framework.aop.OperationLogging;
import framework.aop.oparation.OperationSuccessEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminLoginController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/register/admin")
    public Result registerAdmin(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.registerAdmin(employeeDTO);
        return Result.success("register");
    }

    @PostMapping("/register/employee")
    public Result registerEmployee(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.registerEmployee(employeeDTO);
        return Result.success("register");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String token = employeeService.login(loginDTO);
        return Result.success(token);
    }

    @OperationLogging(operation = OperationSuccessEnum.CREATE)
    @PostMapping("/logout")
    public Result logout() {
        employeeService.logout();
        return Result.success("logout");
    }
}
