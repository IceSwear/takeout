package com.kk.service.impl;

import com.kk.model.entities.ShoppingCart;
import com.kk.model.persistence.ShoppingCartMapper;
import com.kk.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * @author Spike Wong
 * @since 2022-07-04
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
