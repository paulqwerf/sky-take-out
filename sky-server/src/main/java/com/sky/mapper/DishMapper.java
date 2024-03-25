package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from sky_take_out.dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
//    @Insert("insert into sky_take_out.dish(name, category_id, price, image, description, create_time, update_time, create_user, update_user, status) " +
//            "values (#{name},#{categoryId},#{price},#{image},#{description},#{createTtime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insert(Dish dish);
}
