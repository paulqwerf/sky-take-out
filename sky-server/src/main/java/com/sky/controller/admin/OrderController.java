package com.sky.controller.admin;

import io.swagger.annotations.Api;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Api(tags = "商家订单管理接口")
@RequestMapping("/admin/order")
@RestController("adminOrderController")
@Builder
@Slf4j
public class OrderController {

}
