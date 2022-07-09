package com.kk.service.impl;

import com.kk.model.entities.Employee;
import com.kk.model.persistence.EmployeeMapper;
import com.kk.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * @author Spike Wong
 * @since 2022-06-13
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
