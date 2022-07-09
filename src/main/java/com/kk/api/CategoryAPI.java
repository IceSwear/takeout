package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.common.R;
import com.kk.model.entities.Category;
import com.kk.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Spike Wong
 * @since 2022-06-28
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryAPI {
    @Autowired
    CategoryService categoryService;

    //Regarding adding new dish or s  etdish,there is only one method to achieve with different type value
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("Category：{}", category);
        boolean save = categoryService.save(category);
        if (save) {
            return R.success("Add category successfully");
        }
        return R.error("Fail to add!");
    }

    /**
     * page --to display the information of mysql
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Integer page, @RequestParam Integer pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //set condition_ order by
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // order by sort value
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, queryWrapper);
        log.info("");
        return R.success(pageInfo);
    }


    /**
     * delete by id,to judge condition before delete
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids) {
        log.info("deleted id:{}", ids);
        return categoryService.removeDIY(ids) ? R.success("Delete Successfully") : R.error("Fail to delete");
    }

    /**
     * update category
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("Update Category：{}", category);
        return categoryService.updateById(category) ? R.success("Update Category Successfully!") : R.error("Fail to delete!");
    }

    /**
     * querry by type and order by sort+ update time
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categories = categoryService.list(lambdaQueryWrapper);
        return R.success(categories);
    }
}


