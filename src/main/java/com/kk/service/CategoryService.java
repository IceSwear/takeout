package com.kk.service;

import com.kk.model.entities.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author Spike Wong
 * @since 2022-06-28
 */
public interface CategoryService extends IService<Category> {

    public boolean removeDIY(Long id);

}
