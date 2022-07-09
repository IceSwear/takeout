package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kk.common.BaseContextOfUser;
import com.kk.common.R;
import com.kk.model.entities.ShoppingCart;
import com.kk.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Spike Wong
 * @since 2022-07-04
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartAPI {

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("informaiton of cart：{}", shoppingCart);
        //add userid, one option is to use , HttpSession httpSession
        //shoppingCart.setUserId((Long) httpSession.getAttribute("user"));
        //current userID
        Long currentId = BaseContextOfUser.getCurrentId();
        shoppingCart.setUserId(currentId);
        //in advance ,to set a condition for query
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        //Try to get dish id
        Long dishId = shoppingCart.getDishId();
        if (Objects.isNull(dishId)) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }
        //select * from table where userid =? and dish/setmeal id is ??
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if (Objects.isNull(cart)) {
            //save the new one
            shoppingCartService.save(shoppingCart);
            //return and end
            return R.success(shoppingCart);
        }
        //increase one pc for current added item,increase number of cart and get them to set the previous shopping cart
        Integer integer = cart.getNumber() + 1;
        shoppingCart.setNumber(integer);
        //set id
        shoppingCart.setId(cart.getId());
        shoppingCartService.updateById(shoppingCart);
        //return,we can see the qty from user-interface
        return R.success(shoppingCart);
    }

    /**
     * check the cart,make a query to get current user's cart
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContextOfUser.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        log.info("Display list:{}", list);
        //return cart list
        return R.success(list);
    }


    /**
     * sub the goods
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}", shoppingCart);
        Long dishId = shoppingCart.getDishId();
        //set a condtion firstly
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContextOfUser.getCurrentId());
        if (Objects.isNull(dishId)) {
            //select by setmild id
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }
        //select exit in cart
        ShoppingCart exit = shoppingCartService.getOne(queryWrapper);
        //TODO
        //get qty
        int number = exit.getNumber();
        if (number == 1) {
            //prevent -1 happened;
            shoppingCartService.removeById(exit.getId());
        } else {
            exit.setNumber(number - 1);
            shoppingCartService.updateById(exit);
        }
        return R.success("Update successfully！");
    }

    /**
     * clean all of the goods in the chart
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> deleteAll() {
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShoppingCart::getUserId, BaseContextOfUser.getCurrentId());
        shoppingCartService.remove(updateWrapper);
        return R.success("Clear cart successfully!");
    }
}

