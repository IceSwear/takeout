package com.kk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kk.bean.SetmealDTO;
import com.kk.model.entities.Dish;
import com.kk.model.entities.Setmeal;
import com.kk.model.entities.SetmealDish;
import com.kk.model.persistence.SetmealMapper;
import com.kk.service.CategoryService;
import com.kk.service.DishService;
import com.kk.service.SetmealDishService;
import com.kk.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    CategoryService categoryService;
    @Autowired
    DishService dishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        //insert
        this.save(setmealDTO);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDTO.getId());
            Dish dish = dishService.getById(item.getDishId());
            item.setImage(dish.getImage());
            return item;
        }).collect(Collectors.toList());
        //
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public boolean removeWithDish(List<Long> ids) {
        //condition: in ids, ststus should be all 0,if not ,turn false represent it cant be deleted or operated
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            return false;
        }
        //delete them
        this.removeByIds(ids);
        //make a condition: get a result by setmealId= ids,then remove,and turn true finally
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lambdaQueryWrapper);
        return true;
    }

    @Override
    @Transactional
    public boolean updateStatus(Integer status, List<Long> ids) {
        log.info("status：{},ids:{}", status, ids);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        //set a codiction to get if status already still exit
        queryWrapper.eq(Setmeal::getStatus, status);
        int count = this.count(queryWrapper);
        log.info("count:{}", count);
        if (count > 0) {
            return false;
        }
//        List<Setmeal> setmeals = this.listByIds(ids);
//        List<Setmeal> newlist = new ArrayList<>();
//        for (int i = 0; i < setmeals.size(); i++) {
//            if (status == 0) {
//                Setmeal setmeal = setmeals.get(i);
//                setmeal.setStatus(1);
//                newlist.add(setmeal);
//            }
//            if (status == 1) {
//                Setmeal setmeal = setmeals.get(i);
//                setmeal.setStatus(0);
//                newlist.add(setmeal);
//            }
//        }
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Setmeal::getId, ids);
        lambdaUpdateWrapper.set(Setmeal::getStatus, status);
        return this.update(lambdaUpdateWrapper);
        //   lambdaUpdateWrapper.in(Setmeal::getId,ids)
    }

    @Override
    public SetmealDTO getByIdWithSetMealDish(Long id) {
        //
        SetmealDTO setmealDto = new SetmealDTO();
        Setmeal setmealById = this.getById(id);
        BeanUtils.copyProperties(setmealById, setmealDto);
        //List<SetmealDish>
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);
        // categoryName
        Long categoryId = setmealById.getCategoryId();
        String categoryname = categoryService.getById(categoryId).getName();
        setmealDto.setCategoryName(categoryname);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {
        Long setmealDTOId = setmealDTO.getId();
        //update,as DTO
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Setmeal::getId, setmealDTOId);
        //TODO  update(object,wrapper),here wrapper only eq.
        this.update(setmealDTO, updateWrapper);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            //set image
            Dish dish = dishService.getById(item.getDishId());
            item.setImage(dish.getImage());
            //it's only a dto object,so it didn't have a setmealt id
            item.setSetmealId(setmealDTOId);
            return item;
        }).collect(Collectors.toList());
        //remove all per setmeal_id
        LambdaUpdateWrapper<SetmealDish> setmealDishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        setmealDishLambdaUpdateWrapper.eq(SetmealDish::getSetmealId, setmealDTOId);
        //remove
        setmealDishService.remove(setmealDishLambdaUpdateWrapper);
        log.info("updated collet：{}", collect);
        //then inser the new
        setmealDishService.saveBatch(collect);
//        setmealDishService.saveBatch(setmealDishes);
    }


}
