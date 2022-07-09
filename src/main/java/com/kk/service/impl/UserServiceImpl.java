package com.kk.service.impl;

import com.kk.model.entities.User;
import com.kk.model.persistence.UserMapper;
import com.kk.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * @author Spike Wong
 * @since 2022-07-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
