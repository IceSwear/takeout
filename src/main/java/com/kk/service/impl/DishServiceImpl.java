package com.kk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.kk.bean.DishDTO;
import com.kk.model.entities.Dish;
import com.kk.model.entities.DishFlavor;
import com.kk.model.persistence.DishMapper;
import com.kk.service.DishFlavorService;
import com.kk.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Spike Wong
 * @since 2022-06-28
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;


    /**
     * add dish and save flavor data at the same time
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //add dish
        this.save(dishDTO);
        Long id = dishDTO.getId();
        //
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        // dish
        Dish dish = this.getById(id);
        //copy
        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish, dishDTO);
        //flover
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDTO.setFlavors(list);
        return dishDTO;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //update dish
        this.updateById(dishDTO);
        //remove first
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDTO.getId());
        dishFlavorService.remove(queryWrapper);
        //dish insert
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishDTO.getId());
            //resetID  item.setId(IdWorker.getId());
            item.setId(IdWorker.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public boolean updateStatus(Integer status, List<Long> ids) {
        //set a condtion
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus, status).in(Dish::getId, ids);
//        List<Dish> list = dishService.list(queryWrapper);
        int count = this.count(queryWrapper);
        //judge if count>0;
        if (count > 0) {
            return false;
        }
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ids != null, Dish::getId, ids).set(status != null, Dish::getStatus, status);
        return this.update(updateWrapper);
    }
}
