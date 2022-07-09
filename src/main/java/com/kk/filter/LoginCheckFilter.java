package com.kk.filter;

import com.alibaba.fastjson.JSON;
import com.kk.common.BaseContextOfEmployee;
import com.kk.common.BaseContextOfUser;
import com.kk.common.R;
import com.kk.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @Description: filter, to check if login already and make action per conditions
 * @Author:Spike Wong
 * @Date:2022/6/15
 */

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    @Autowired
    EmployeeService employeeService;

    //path matcher,support wildcard character
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // Force transfer into httpSerletFormat
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.get URI of the current request
        String requestURI = request.getRequestURI();
        log.info("URI of request:{}", requestURI);
        //Define request paths that do not need to be processed
        String[] urls = new String[]{"/employee/login", "/employee/logout", "/backend/**", "/front/**", "/common/**", "/api_test/**", "/swagger-resources/**", "/v2/**", "/user/sendMsg", "/user/login", "/dish/list"};
        String[] urlsOfFront = new String[]{"/user/**", "/shoppingCart/**", "/category/list", "/addressBook/**", "/dish/**", "/order/**", "/setmeal/**"};
        //judge if uri is for front end request
        boolean frontRequestCheck = check(urlsOfFront, requestURI);
        //package a method to filter the urls
        boolean check = check(urls, requestURI);
        log.info("Session的ID为:{}", request.getSession().getId());
        //true means that don't need to do anything ,just let it go
        if (check) {
            log.info("White list：{},uncessary to operate!", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //get entries from session
        Long userId = (Long) request.getSession().getAttribute("user");
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        //as long asu serId is not null from session, give it to thread local
        if (!Objects.isNull(userId)) {
            log.info("User login already,set current id:{}", userId);
            BaseContextOfUser.setCurrentId(userId);
        }
        //session 中如果不等于空，说明登陆了，也直接return不处理
        //大概懂了，这一步如果终端同时登录会出事，session问题，后台如果用到同一个session
        //为空就走，不为空就放行
        //judge if employeeId is null,null =false,do next, not null =true
        if (employeeId != null) {
            log.info("Employee login already，set current id:{}", employeeId);
            //setID valuve to threadlocal
            BaseContextOfEmployee.setCurrentId(employeeId);
            //continue the
            filterChain.doFilter(request, response);
            return;
        }
        //if process comes here,it means that employee log out,but we don't know if user login
        //so here we need to judge if user id is null,null=false,not null =true
        if (userId != null && frontRequestCheck) {
//            Employee byUserId = employeeService.getById(userId);
            //here means that user login,employee log out,we need to check it requet URI is from front end,other wise jump to the end
            log.info("User login and request is for front end ，user's id is:{}", userId);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("NOT LOGIN!!");
        //NOT LOGIN,prevent to visit other static html.
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    /**
     * Path match
     *
     * @param urls
     * @param requestURI
     * @return false= not match，true=match
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
