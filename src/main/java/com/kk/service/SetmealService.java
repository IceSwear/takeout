package com.kk.service;

import com.kk.bean.SetmealDTO;
import com.kk.model.entities.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Spike Wong
 * @since 2022-06-28
 */
public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDTO setmealDTO);

    public boolean removeWithDish(List<Long> ids);


    public boolean updateStatus(Integer status,List<Long> ids);

    public SetmealDTO getByIdWithSetMealDish(Long id);

    public void updateWithDish(SetmealDTO setmealDTO);
}
