package com.kk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kk.model.entities.Category;
import com.kk.model.entities.Dish;
import com.kk.model.entities.Setmeal;
import com.kk.model.persistence.CategoryMapper;
import com.kk.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.service.DishService;
import com.kk.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Spike Wong
 * @since 2022-06-28
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * judge before delete
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeDIY(Long id) {
        // set condition for the query sql syntax
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(queryWrapper);
        //To be sure that if count is not zero,return false to present "unable to delete"
        if (count > 0) {
            return false;
        }
        // set condition for the query sql syntax
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        //To be sure that if count is not zero,return false to present "unable to delete"
        if (count1 > 0) {
            return false;
        }
        //Generally speaking, it must be true as if conditions are all checked above, but we should be more strict anyway..
        return categoryService.removeById(id) ? true : false;
    }
}
