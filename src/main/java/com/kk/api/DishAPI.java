package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kk.bean.DishDTO;
import com.kk.common.R;
import com.kk.model.entities.Category;
import com.kk.model.entities.Dish;
import com.kk.model.entities.DishFlavor;
import com.kk.service.CategoryService;
import com.kk.service.DishFlavorService;
import com.kk.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Spike Wong
 * @since 2022-06-28
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishAPI {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    private static final String PATTERN_CATEGORY = "category:";

    /**
     * add new dish
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> addDish(@RequestBody DishDTO dishDTO) {
        log.info("DishDTO:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //delete all
//        Set<String> keys = stringRedisTemplate.keys("category:*");
//        stringRedisTemplate.delete(keys);
//        String key = PATTERN_CATEGORY + dishDTO.getCategoryId();
//        stringRedisTemplate.delete(key);
        return R.success("Add successfully");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDTO> dishDTOPage = new Page<>();
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        //set order
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //copy object
        dishService.page(pageInfo, lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, dishDTOPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDTO> list = records.stream().map((item) -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(item, dishDTO);
            Long categoryId = item.getCategoryId(); //分类id
            //query by id
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDTO.setCategoryName(categoryName);
            return dishDTO;
        }).collect(Collectors.toList());
        dishDTOPage.setRecords(list);
        return R.success(dishDTOPage);
    }

    /**
     * select by id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<DishDTO> update(@PathVariable Long id) {
        DishDTO byIdWithFlavor = dishService.getByIdWithFlavor(id);
        String key = PATTERN_CATEGORY + byIdWithFlavor.getCategoryId();
//        stringRedisTemplate.delete(key);
        return R.success(byIdWithFlavor);
    }

    /**
     * update dish
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> update(@RequestBody DishDTO dishDTO) {
        dishService.updateWithFlavor(dishDTO);
        //redis remove data
//        String key = PATTERN_CATEGORY + dishDTO.getCategoryId();
//        stringRedisTemplate.delete(key);
        return R.success("Update dish successfully!");
    }

    /**
     * slect by categoryId
     *
     * @param categoryId
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> selectByCategoryId(String categoryId) {
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        //status should be 1
//        lambdaQueryWrapper.eq(Dish::getStatus, 1).eq(Dish::getCategoryId, categoryId);
//        return R.success(dishService.list(lambdaQueryWrapper));
//    }

    /**
     * slect by categoryId
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "dishCache", key = "#categoryId")
    public R<List<DishDTO>> selectByCategoryId(String categoryId) {
        //get list content from redis
//        String key = PATTERN_CATEGORY + categoryId;
//        String value = stringRedisTemplate.opsForValue().get(key);
        //judge if value is not null,directly return through Redis
//        if (!Objects.isNull(value)) {
//            List<DishDTO> dishDTOList = JSON.parseArray(value, DishDTO.class);
//            return R.success(dishDTOList);
//        }
        //if value is null,just get from sql,and also save copy in Redis finally
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //status should be 1
        lambdaQueryWrapper.eq(Dish::getStatus, 1).eq(Dish::getCategoryId, categoryId);
        List<Dish> list1 = dishService.list(lambdaQueryWrapper);

        List<DishDTO> dishDTOList = list1.stream().map((item) -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(item, dishDTO);
            Long categoryId1 = item.getCategoryId(); //分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId1);
            String categoryName = category.getName();
            dishDTO.setCategoryName(categoryName);
            // current dish
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, id);
            List<DishFlavor> list = dishFlavorService.list(queryWrapper);
            dishDTO.setFlavors(list);
            return dishDTO;
        }).collect(Collectors.toList());
        //save copy in redis,duration is 60 mins;
//        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(dishDTOList), 60, TimeUnit.MINUTES);
        return R.success(dishDTOList);
    }


    @PostMapping("/status/{status}")
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        boolean b = dishService.updateStatus(status, ids);
        if (!b) {
            R.error("Fail to update，pls check if all status of items are same");
        }
        return R.success("Update successfully~");
    }


    /**
     * deleted by list<Long> ids
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "dishCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids).eq(Dish::getStatus, 1);
        int count = dishService.count(queryWrapper);
        if (count > 0) {
            return R.error("Items on shelves cannot be deleted!");
        }
        return dishService.removeByIds(ids) ? R.success("remove successfully") : R.error("fail to remove");
    }
}

