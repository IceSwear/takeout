package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kk.common.BaseContextOfUser;
import com.kk.common.R;
import com.kk.model.entities.User;
import com.kk.service.UserService;
import com.kk.util.MailUtil;
import com.kk.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Spike Wong
 * @since 2022-07-03
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserAPI {

    @Autowired
    UserService userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MailUtil mailUtil;

    /**
     * register by email code
     *
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        if (StringUtils.isNoneEmpty(email)) {
            if (Objects.isNull(stringRedisTemplate.opsForValue().get("code:" + email))) {
                String code = ValidateCodeUtils.generateValidateCode(6);
                log.info("Validation code:{}", code);
                stringRedisTemplate.opsForValue().set("code:" + email, code, 300, TimeUnit.SECONDS);
                mailUtil.msg(code, email);
                //save in session
//                session.setAttribute(email, code);
                return R.success("Send successfully!!");
            }
        }
        return R.error("System error,pls try again later!");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        String email = map.get("email").toString();
        String code = map.get("code").toString();
        //get code from session by email
//        Object codeInSession = session.getAttribute(email);
        //GET code from email
        String codeFromRedis = stringRedisTemplate.opsForValue().get("code:" + email);
        //judge if equals
//        if (codeInSession != null && codeInSession.equals(code)) {
        if (codeFromRedis != null && codeFromRedis.equals(code)) {
            LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getEmail, email);
//            User one = userService.getOne(lambdaUpdateWrapper);
            User user = userService.getOne(lambdaUpdateWrapper);
            //judge if user has been already in database
            if (Objects.isNull(user)) {
                //it's  a new user，save in database
                user = new User();//make user a null object
                user.setEmail(email);
                user.setStatus(1);
                userService.save(user);
            }
//            LambdaUpdateWrapper<User> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
//            lambdaUpdateWrapper1.eq(User::getEmail, email);
//            User user = userService.getOne(lambdaUpdateWrapper);

            session.setAttribute("user", user.getId());
            //remove code from redis after user login
            stringRedisTemplate.delete("code:" + email);
            return R.success(user);
        }
        return R.error("Fail to login,please enter corrective validation code!");
    }


    /**
     * user loginout, remove information from session and thread local
     *
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request) {
        log.info("User login out");
        request.getSession().removeAttribute("user");
        BaseContextOfUser.setCurrentId(null);
        return R.success("Login out successfully");
    }
}

