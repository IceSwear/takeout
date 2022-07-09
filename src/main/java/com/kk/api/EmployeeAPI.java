package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.common.BaseContextOfEmployee;
import com.kk.common.R;
import com.kk.model.entities.Employee;
import com.kk.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author Spike Wong
 * @since 2022-06-13
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeAPI {
    @Autowired
    private EmployeeService employeeService;

    /**
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("Login name:{},Password enter:{}", employee.getUsername(), employee.getPassword());
        //MD5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        log.info("query result:{}, id :{}", emp, emp.getId());
        if (emp == null) {
            return R.error("Fail to login!");
        }
        log.info("ID:{}", emp.getId());
        if (!emp.getPassword().equals(password)) {
            return R.error("Fail to login");
        }
        if (emp.getStatus() == 0) {
            return R.error("Account is baned");
        }
        log.info("saved ID is:{}", emp.getId());
        request.getSession().setAttribute("employee", emp.getId());
        log.info("Session ID :{}", request.getSession().getId());
        emp.setPassword("");
        return R.success(emp);
    }

    /**
     * login out
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //remove entries of employee from sessions
        log.info("Employee login out!");
        request.getSession().removeAttribute("employee");
        BaseContextOfEmployee.setCurrentId(null);
        return R.success("Employee login out!");
    }

    /**
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("info of new employee:{}", employee);
        if (employee == null) {
            return R.error("Fail to add,pls try again!");
        }
        //default password is：123456
        String password = "123456";
        employee.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp != null) {
            return R.error(emp.getUsername() + "Already exist");
        }
        if (employeeService.save(employee)) {
            return R.success("Add successfully");
        }
        //or try，catch
        return R.error("Fail to add");
    }

    /**
     * query and page them
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //set page
        Page p1 = new Page(page, pageSize);
        //set query
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //condition
        queryWrapper.like(StringUtils.isNoneEmpty(name), Employee::getName, name);
        //order by updatetime
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //execute the sql
        employeeService.page(p1, queryWrapper);
        return R.success(p1);
    }

    /**
     * update information of employee by id
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("Updated Info of employee:{}", employee);
//        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
//        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
//        Employee one = employeeService.getOne(lambdaQueryWrapper);
//        if (one != null &&) {
//            return R.error(employee.getUsername() + "已存在");
//        }
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
        boolean update = employeeService.updateById(employee);
        if (update) {
            return R.success("update successfully!");
        }
        return R.error("Fail to update！");
    }

    /**
     * Get by id，display info of employee when update
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("ID={}", id);
        Employee emp = employeeService.getById(id);
        return R.success(emp);
    }
}

