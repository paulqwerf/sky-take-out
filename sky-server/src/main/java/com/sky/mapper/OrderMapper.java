package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);


    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from sky_take_out.orders where id = #{id}")
    Orders getById(Long id);

    @Delete("delete from sky_take_out.orders where id = #{id}")
    void deleteById(Long id);

    void update(Orders orders);

    @Select("select * from sky_take_out.orders")
    List<Orders> list();
    @Select("select count(*) from sky_take_out.orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from sky_take_out.orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

    Double getByMap(Map map);

    Integer getOrderByMap(Map map);

    List<GoodsSalesDTO> orderTop10(Map map);
}
