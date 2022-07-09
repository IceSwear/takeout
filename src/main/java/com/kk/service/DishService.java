package com.kk.service;

import com.kk.bean.DishDTO;
import com.kk.model.entities.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Spike Wong
 * @since 2022-06-28
 */
public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDTO dishDTO);

    public  DishDTO getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDTO dishDTO);

    public boolean updateStatus(Integer status, List<Long> ids);


}
