package com.kk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kk.model.entities.Orders;

/**
 * @author Spike Wong
 * @since 2022-07-04
 */
public interface OrdersService extends IService<Orders> {

    public boolean submit(Orders orders);

}