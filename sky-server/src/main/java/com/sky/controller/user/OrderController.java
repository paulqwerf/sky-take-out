package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.LinkOption;
import java.util.HashMap;
import java.util.Map;

@RestController("userOrderController")
@Api(tags = "C端订单相关接口")
@RequestMapping("/user/order")
@Slf4j
@Builder
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WebSocketServer webSocketServer;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

        Map map = new HashMap<>();
        map.put("type",1);//1表示来单提醒
        map.put("orderId",orderSubmitVO.getId());//订单id
        map.put("content","订单号："+orderSubmitVO.getOrderNumber());
        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);

        return Result.success(orderSubmitVO);
    }

    @ApiOperation("历史订单查询")
    @GetMapping("/historyOrders")
    public Result<PageResult> checkHistory(OrdersPageQueryDTO ordersPageQueryDTO){
       PageResult pageResult = orderService.pageQuery4User(ordersPageQueryDTO);
       return Result.success(pageResult);
    }
    @ApiOperation("查询订单详情")
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> checkById(@PathVariable Long id){
        OrderVO orderVO = orderService.checkById(id);
        return Result.success(orderVO);
    }

    @ApiOperation("再来一单")
    @PostMapping("/repetition/{id}")
    public Result againOrder(@PathVariable Long id){
        orderService.againOrder(id);
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){
        orderService.cancel(id);
        return Result.success();
    }
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }
}
