package com.branch.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.domain.dto.*;
import com.branch.domain.dto.page.EmployeePageDTO;
import com.branch.domain.entity.Employee;
import com.branch.domain.entity.EmployeeRole;
import com.branch.domain.entity.RolePermission;
import com.branch.domain.entity.User;
import com.branch.domain.vo.EmployeeVO;
import com.branch.mapper.EmployeeMapper;
import com.branch.service.EmployeeRoleService;
import com.branch.service.EmployeeService;
import com.branch.service.RolePermissionService;
import common.constant.RedisPrefixConstant;
import common.constant.ErrorConstant;
import common.constant.JwtConstant;
import common.exception.EmployeeFailedException;
import common.exception.LoginFailedException;
import common.exception.PasswordErrorException;

import result.PageResult;

import common.RoleConstant;
import common.StatusConstant;
import framework.bo.UserBO;
import framework.properties.JwtProperties;
import framework.security.LoginUserDetails;
import framework.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import framework.security.SecurityContextParam;

import framework.security.AdminAuthenticationToken;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 员工 Service（对应 employee 表）
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入密码加密器 Bean
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AuthenticationManager authenticationManager; // 注入认证管理器
    @Autowired
    private EmployeeRoleService employeeRoleService;
    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public Employee findEmployeename(String username) {
        return this.lambdaQuery()
                .eq(Employee::getUsername, username)
                .one();
    }

    @Override
    public void registerAdmin(EmployeeDTO employeeDTO) {
        Employee find = this.findEmployeename(employeeDTO.getUsername());
        if (find != null) {
            throw new LoginFailedException("branch-emp",ErrorConstant.REGISTER_ERROR);
        }
        Employee employee = BeanUtil.toBean(employeeDTO, Employee.class);
        employee.setStatus(StatusConstant.ENABLE);
        if (employee.getPassword() == null) {
            employee.setPassword("123456");
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCreateUser(0L);
        employee.setUpdateUser(0L);
        this.save(employee);
    }

    @Override
    public void registerEmployee(EmployeeDTO employeeDTO) {
        Employee find = this.findEmployeename(employeeDTO.getUsername());
        if (find != null) {
            throw new LoginFailedException("branch-emp",ErrorConstant.REGISTER_ERROR);
        }
        Employee employee = BeanUtil.toBean(employeeDTO, Employee.class);
        employee.setStatus(StatusConstant.ENABLE);
        if (employee.getPassword() == null) {
            employee.setPassword("123456");
        }
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCreateUser(0L);
        employee.setUpdateUser(0L);
        this.save(employee);
    }

    @Override
    public String login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 使用 AdminAuthenticationToken → 自动路由到 AdminAuthenticationProvider → LoginAdminService
        AdminAuthenticationToken authenticationToken = new AdminAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        LoginUserDetails loginUser = (LoginUserDetails) authentication.getPrincipal();
        UserBO userBO = loginUser.getUserBO();
        User employee = BeanUtil.toBean(userBO, User.class);
        Map<String,Object> map = new HashMap<>();
        map.put(JwtConstant.ID, employee.getId());
        map.put(JwtConstant.NAME, employee.getUsername());
        map.put(JwtConstant.Role,RoleConstant.ROLE_ADMIN);
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), map);
        stringRedisTemplate.opsForValue().set(RedisPrefixConstant.ADMIN_AUTH_PREFIX+ employee.getId(), token,
                jwtProperties.getAdminTtl(), TimeUnit.SECONDS);
        return token;
    }

    @Override
    public void logout() {
        Long userId = SecurityContextParam.getCurrentUserId();
        if (userId == null) {
            throw new LoginFailedException("branch-emp",ErrorConstant.ACCOUNT_NOT_EXIST);
        }
        stringRedisTemplate.delete(RedisPrefixConstant.ADMIN_AUTH_PREFIX + userId);
        SecurityContextHolder.clearContext();
    }

    @Override
    public EmployeeVO readById(Long id) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new EmployeeFailedException(ErrorConstant.ACCOUNT_NOT_EXIST);
        }
        EmployeeVO employeeVO = BeanUtil.toBean(employee, EmployeeVO.class);
        return employeeVO;
    }

    @Override
    public PageResult<EmployeeVO> readPage(EmployeePageDTO employeePageDTO) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(employeePageDTO.getEmployeename()!= null,
                Employee::getUsername,employeePageDTO.getEmployeename());
        IPage page = new Page(employeePageDTO.getPage(),employeePageDTO.getPageSize());
        IPage<Employee> employeeIPage = this.page(page,queryWrapper);
        List<EmployeeVO> voList = employeeIPage.getRecords().stream()
                .map(emp -> BeanUtil.copyProperties(emp, EmployeeVO.class))
                .collect(Collectors.toList());
        PageResult<EmployeeVO> result = new PageResult<>();
        result.setTotal(employeeIPage.getTotal());
        result.setList(voList);                         // 当前页数据
        result.setPageNum(employeeIPage.getCurrent());  // 当前页码
        result.setPageSize(employeeIPage.getSize());    // 每页条数
        return result;
    }

    @Override
    public void updateBySelf(EmployeeDTO employeeDTO) {
        // 1. 校验 ID
        if (employeeDTO.getId() == null) {
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }

        // 2. 构建 wrapper，只 set 有值的字段
        LambdaUpdateWrapper<Employee> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Employee::getId, employeeDTO.getId());
        if(StrUtil.isNotBlank(employeeDTO.getWork())){
            updateWrapper.set(Employee::getWork,employeeDTO.getWork());
        }
        if (StrUtil.isNotBlank(employeeDTO.getUsername())) {
            updateWrapper.set(Employee::getUsername, employeeDTO.getUsername());
        }
        if (StrUtil.isNotBlank(employeeDTO.getPhone())) {
            updateWrapper.set(Employee::getPhone, employeeDTO.getPhone());
        }
        if (StrUtil.isNotBlank(employeeDTO.getEmail())) {
            updateWrapper.set(Employee::getEmail, employeeDTO.getEmail());
        }
        if (StrUtil.isNotBlank(employeeDTO.getAvatar())) {
            updateWrapper.set(Employee::getAvatar, employeeDTO.getAvatar());
        }
        if (StrUtil.isNotBlank(employeeDTO.getSex())) {
            updateWrapper.set(Employee::getSex, employeeDTO.getSex());
        }
        if (employeeDTO.getStatus() != null) {
            updateWrapper.set(Employee::getStatus, employeeDTO.getStatus());
        }
        // 密码单独处理（加密）
        if (StrUtil.isNotBlank(employeeDTO.getPassword())) {
            updateWrapper.set(Employee::getPassword,
                    passwordEncoder.encode(employeeDTO.getPassword()));
        }
        this.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }
        this.removeByIds(ids);
    }

    @Override
    public void updatePassword(EditPasswordDTO editPasswordDTO) {
        String newPassword = editPasswordDTO.getNewPassword();
        String confirmPassword = editPasswordDTO.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordErrorException(ErrorConstant.PASSWORD_EDIT_FAILED);
        }
        Long id = SecurityContextParam.getCurrentUserId();
        Employee employee = this.getById(id);
        employee.setPassword(newPassword);
        stringRedisTemplate.delete(RedisPrefixConstant.ADMIN_AUTH_PREFIX + id);
        SecurityContextHolder.clearContext();
        this.updateById(employee);
    }

    @Override
    public void postRoleTOEmployee(EmployeeRoleDTO employeeRoleDTO) {
        EmployeeRole employeeRole = BeanUtil.toBean(employeeRoleDTO, EmployeeRole.class);
        employeeRoleService.save(employeeRole);
    }

    @Override
    public void deleteRoleTOEmployee(EmployeeRoleDTO employeeRoleDTO) {
        if(employeeRoleDTO.getId() == null){
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }
        employeeRoleService.removeById(employeeRoleDTO.getId());
    }

    @Override
    public void postRoleTOPermission(RolePermissionDTO rolePermissionDTO) {
        RolePermission rolePermission = BeanUtil.toBean(rolePermissionDTO, RolePermission.class);
        rolePermissionService.save(rolePermission);
    }

    @Override
    public void deleteRoleTOPermission(RolePermissionDTO rolePermissionDTO) {
        if(rolePermissionDTO.getId() == null){
            throw new EmployeeFailedException(ErrorConstant.OPERATION_ERROR);
        }
        rolePermissionService.removeById(rolePermissionDTO.getId());
    }


}
