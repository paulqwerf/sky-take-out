package com.sky.controller.admin;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "商家订单管理接口")
@RequestMapping("/admin/order")
@RestController("adminOrderController")
@Builder
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> checkOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.pageQuery4Admin(ordersPageQueryDTO);
        return Result.success(pageResult);

    }
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statisticsOrder(){
        OrderStatisticsVO orderStatisticsVO = orderService.statisticsOrder();
        return Result.success(orderStatisticsVO);
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result completeOrder(@PathVariable Long id){
        orderService.completeOrder(id);
        return Result.success();
    }
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }
    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> checkOrderDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.checkById(id);
        return Result.success(orderVO);
    }
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliverOrder(@PathVariable Long id){
        orderService.deliverOrder(id);
        return Result.success();
    }

}
