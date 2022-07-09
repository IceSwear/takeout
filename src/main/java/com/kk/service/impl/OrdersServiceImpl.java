package com.kk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.common.BaseContextOfUser;
import com.kk.model.entities.*;
import com.kk.model.persistence.OrdersMapper;
import com.kk.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author:Spike Wong
 * @Date:2022/7/8
 */
/**
 *
 * @author Spike Wong
 * @since 2022-07-04
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    public boolean submit(Orders orders) {
        //get user
        Long currentId = BaseContextOfUser.getCurrentId();
        //get info of user's chart
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        if (list == null || list.size() == 0) {
            return false;
        }
        //query user
        User user = userService.getById(currentId);
        //query info of address
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (Objects.isNull(addressBook)) {
            return false;
        }
        long id = IdWorker.getId();
        //
        AtomicInteger amount = new AtomicInteger(0);
        // atom to make thread safety
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //check all exit,then save order with info
        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        //1 wait for payment ,2 wait for posting 3. already post,4 finished, 5 cancel
        orders.setStatus(2);
        //amount ,should calculate again
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(id));
        //here I use email instead
        orders.setUserName(user.getEmail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(
                (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                        + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                        + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                        + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        // insert data into order table
        this.save(orders);
        //insert batch data into orderDetails table
        orderDetailService.saveBatch(orderDetails);
        //clean current user's cart's info
        shoppingCartService.remove(queryWrapper);
        return true;
    }
}