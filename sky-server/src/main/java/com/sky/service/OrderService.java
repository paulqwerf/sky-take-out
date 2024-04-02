package com.sky.service;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    PageResult pageQuery4User(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO checkById(Long id);

    void againOrder(Long id);

    void cancel(Long id);

    PageResult pageQuery4Admin(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statisticsOrder();

    void completeOrder(Long id);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void deliverOrder(Long id);
}
