package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper {
    void insertBatch(List<OrderDetail> orderDetailList);

    @Select("select * from sky_take_out.order_detail where order_id = #{ordersId}")
    List<OrderDetail> getByOrderId(Long ordersId);

    @Delete("delete from sky_take_out.order_detail where order_id = #{id}")
    void deleteByOrderId(Long id);


}
