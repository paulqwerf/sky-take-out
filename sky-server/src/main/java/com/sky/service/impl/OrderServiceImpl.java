package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.properties.WeChatProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1、业务异常
        //收获地址是否存在
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车是否非空
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(currentId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList == null|| shoppingCartList.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //2、向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(currentId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));

        orderMapper.insert(orders);
        //3、向订单明细表插入n条数据

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        //4、清空购物车
        shoppingCartMapper.deleteAll(shoppingCart);
        //5、返回数据
        return OrderSubmitVO.builder()
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();
    }

    @Override
    public PageResult pageQuery4User(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);

        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList<>();

        //判断读取出来的page是否为空
        if(page != null && !page.isEmpty()){
            //把每一个orders传入到orderVO
            for (Orders orders : page) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());
                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            }

        }
        return new PageResult(page.getTotal(),list);

    }

    @Override
    public OrderVO checkById(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(list);


        return orderVO;
    }

    @Override
    public void againOrder(Long id) {
//       1、获取用户id
        Long currentId = BaseContext.getCurrentId();
        // 2、查订单明细表 通过订单id，找到本次订单具体的菜品
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        //3、创建购物车列表，并把查询到的订单明细拷贝到购物车中
        List<ShoppingCart> list = orderDetailList.stream().map(s -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(s, shoppingCart, "id");
            shoppingCart.setUserId(currentId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartMapper.insertBetch(list);
    }

    @Transactional
    @Override
    public void cancel(Long id) {
        Long currentId = BaseContext.getCurrentId();
        Orders ordersDB = orderMapper.getById(id);
        if(ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (ordersDB.getStatus()>2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        if(ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            //微信实现退款
            orders.setPayStatus(Orders.REFUND);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason("用户取消");
        orderMapper.update(orders);
    }
}
