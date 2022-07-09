package com.kk.service.impl;

import com.kk.model.entities.OrderDetail;
import com.kk.model.persistence.OrderDetailMapper;
import com.kk.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * @author Spike Wong
 * @since 2022-07-04
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
