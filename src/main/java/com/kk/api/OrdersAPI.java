package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.bean.OrderPostDTO;
import com.kk.common.BaseContextOfUser;
import com.kk.common.R;
import com.kk.model.entities.OrderDetail;
import com.kk.model.entities.Orders;
import com.kk.model.entities.ShoppingCart;
import com.kk.service.OrderDetailService;
import com.kk.service.OrdersService;
import com.kk.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Spike Wong
 * @since 2022-07-04
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersAPI {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("Orders:{}", orders);
        boolean submit = ordersService.submit(orders);
        if (submit) {
            return R.success("submit successfully");
        }
        return R.error("fail to submit,check again!");
    }


    /**
     * list current uer's order list
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(Integer page, Integer pageSize) {
        log.info("Current page：{}，：pageSize:{}", page, pageSize);
        //set pageInfo with page and pageSize
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        queryWrapper.eq(Orders::getUserId, BaseContextOfUser.getCurrentId());
        ordersService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);
    }


    /**
     * show by range of time,order nmuber & pageInfo
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String number, String beginTime, String endTime) {
        //To be honest ,it will be more grateful to use vo/dto to receive the data
        log.info("page:{}, pageSize:{},number:{},beginTime:{},:endTime{}", page, pageSize, number, beginTime, endTime);
        //set condtion to include the range of time,order number & pageInfo
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(beginTime != null, Orders::getOrderTime, beginTime);
        queryWrapper.eq(number != null, Orders::getNumber, number);
        queryWrapper.lt(endTime != null, Orders::getOrderTime, endTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo, queryWrapper);
        //return pageInfo
        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> post(@RequestBody OrderPostDTO orderPostDTO) {
        log.info("orderPostDTO{}", orderPostDTO);
        Integer status = orderPostDTO.getStatus();
        //set a condition that select by ID ,and set status to new status
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(orderPostDTO.getId() != null, Orders::getId, orderPostDTO.getId());
        updateWrapper.set(orderPostDTO.getStatus() != null, Orders::getStatus, orderPostDTO.getStatus());
        return ordersService.update(updateWrapper) ? R.success("Update successfully") : R.error("Fail to update!");
    }


    /**
     * place  order again
     *
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<List<ShoppingCart>> again(@RequestBody Map map) {
        //get id from map
        String id = map.get("id").toString();
        //set the condition to search current order's detail
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, id);
        //get orderDetail list
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        //to prepare a object to add shopcart's item
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        //copy one by one
        orderDetailList.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(BaseContextOfUser.getCurrentId());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setNumber(item.getNumber());
            //copy dish Id or setmeal Id, here should judge if the object is Dish or setmeal
            if (!Objects.isNull(item.getDishId())) {
                shoppingCart.setDishFlavor(item.getDishFlavor());
                shoppingCart.setDishId(item.getDishId());
            } else {
                shoppingCart.setSetmealId(item.getSetmealId());
            }
            shoppingCarts.add(shoppingCart);
            return item;
        }).collect(Collectors.toList());
        // search current carts info,then clear them all
        LambdaQueryWrapper<ShoppingCart> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ShoppingCart::getUserId, BaseContextOfUser.getCurrentId());
        shoppingCartService.remove(queryWrapper1);
        //return the shoppingCarts
        shoppingCartService.saveBatch(shoppingCarts);
        return R.success(shoppingCarts);
    }
}

