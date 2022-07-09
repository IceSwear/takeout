package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.bean.SetmealDTO;
import com.kk.common.R;
import com.kk.model.entities.*;
import com.kk.service.CategoryService;
import com.kk.service.DishFlavorService;
import com.kk.service.SetmealDishService;
import com.kk.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Spike Wong
 * @since 2022-06-28
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealAPI {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        //upgrate
        Page<SetmealDTO> setmealDTOPage = new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, lambdaQueryWrapper);
        //copy
        BeanUtils.copyProperties(pageInfo, setmealDTOPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDTO> list = records.stream().map((item) -> {
            SetmealDTO setmealDTO = new SetmealDTO();
            //copy
            BeanUtils.copyProperties(item, setmealDTO);
            Long categoryId = item.getCategoryId();
            //select by category id
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDTO.setCategoryName(categoryName);
            }
            return setmealDTO;
        }).collect(Collectors.toList());
        setmealDTOPage.setRecords(list);
        return R.success(setmealDTOPage);
    }

    /**
     * add new setmeal
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("new setmeal dto：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return R.success("Add successfully~~");
    }

    /**
     * remove by ids
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = {"setmealCache}", "{SetmealDish"}, allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        boolean b = setmealService.removeWithDish(ids);
        if (!b) {
            return R.error("Fail to delete, still exit on sales item!");
        }
        return R.success("Remove successfully!!");
    }

    /**
     * make status to on sales on ban sales
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = {"setmealCache", "SetmealDish"}, allEntries = true)
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        //   @RequestBody Setmeal setmeal
        boolean updateStatus = setmealService.updateStatus(status, ids);
        if (!updateStatus) {
            return R.error("Fail to update，pls check whether all status of items are same!!");
        }
        return R.success("Update successfully！");
    }

    /**
     * select by status & category ID
     *
     * @param status
     * @param categoryId Ca @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#categoryId")
    public R<List<Setmeal>> getByCategoryAndStatus(Integer status, Long categoryId) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Setmeal::getStatus, status).eq(Setmeal::getCategoryId, categoryId).orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * get by id
     *
     * @param id
     * @return
     */

    @GetMapping("/{id}")
    public R<SetmealDTO> getById(@PathVariable Long id) {
        //study design mode
        SetmealDTO byIdWithSetMealDish = setmealService.getByIdWithSetMealDish(id);
        return R.success(byIdWithSetMealDish);
    }


    /**
     * update setmeal and display
     *
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @CacheEvict(value = {"setmealCache", "SetmealDish"}, allEntries = true)
    public R<String> update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateWithDish(setmealDTO);
        return R.success("Update successfully");
    }


    //** maybe it's return a list of setmeal dish
    @GetMapping("/dish/{setmealId}")
    @Cacheable(value = "SetmealDish", key = "#setmealId")
    public R<List<SetmealDish>> getBySetmealId(@PathVariable Long setmealId) {
//        Setmeal setmeal = setmealService.getById(setmealId);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        // get id from setmeal dish
        return R.success(list);
    }
}

