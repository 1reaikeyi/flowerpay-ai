package com.branch.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.branch.domain.entity.Employee;
import org.springframework.stereotype.Repository;

/**
 * 员工 Mapper（对应 employee 表）
 */
@Repository
public interface EmployeeMapper extends BaseMapper<Employee> {


}
