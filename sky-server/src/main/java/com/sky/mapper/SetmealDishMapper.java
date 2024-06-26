package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealIdByDishId(List<Long> ids);

    void insert(List<SetmealDish> setmealDishes);

    @Select("select * from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);


    List<SetmealDish> getBySetmealIds(List<Long> ids);

    void deleteBySetmealIds(List<Long> ids);

    @Select("select dish_id from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    List<Long> getDishIdsBySetmealId(Long setmealId);
}
