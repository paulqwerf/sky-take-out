package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper {

    @Insert("insert into sky_take_out.category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUES( " +
            "#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    void update(Category category);


    Page<Category> getPageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
}
