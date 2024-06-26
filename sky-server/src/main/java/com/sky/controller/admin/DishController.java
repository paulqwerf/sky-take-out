package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Builder
@Slf4j
@Api(tags = "菜品管理接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "新增菜品")
    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }



    @ApiOperation(value = "菜品批量删除")
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids){
        dishService.deleteBetch(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation("启用禁用菜品")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){

        dishService.startOrStop(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation(value = "根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        List<Dish> dish = dishService.getByCategoryId(categoryId);
        return Result.success(dish);
    }

    private void cleanCache(String pattern){
        Set key = redisTemplate.keys(pattern);
        redisTemplate.delete(key);
    }

}
