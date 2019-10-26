package com.print.printcloud.order.service;

import com.print.printcloud.order.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by 郑钦泓
 * 2019-10-24 20:45
 */
public interface OrderService {

    /** 创建订单. */
    OrderDTO create(OrderDTO orderDTO);

    /** 查询单个订单. */
    OrderDTO findOne(String orderId);

    /** 查询用户订单列表. */
    Page<OrderDTO> findUserOrderList(String buyerOpenid, Pageable pageable);

    /** 修改订单状态为取消. */
    OrderDTO cancel(OrderDTO orderDTO);

    /** 修改订单状态为待配送或待收货. */
    OrderDTO send(OrderDTO orderDTO);

    /** 修改订单状态为已配送或已收货. */
    OrderDTO finish(OrderDTO orderDTO);

    /** 支付订单. */
    OrderDTO paid(OrderDTO orderDTO);

    /** 查询订单列表. */
    Page<OrderDTO> findList(Pageable pageable);

    /** 查询所有订单状态为待打印的订单*/
    Page<OrderDTO> findWaitPrint(Pageable pageable);

    /** 查询所有订单状态为待配送的订单*/
    Page<OrderDTO> findWaitSend(Pageable pageable);

    /** 查询所有订单状态为已配送的订单*/
    Page<OrderDTO> findFinishSend(Pageable pageable);

    /** 查询所有订单状态为取消的订单*/
    Page<OrderDTO> findCancel(Pageable pageable);


    /** 查询微信用户订单状态为待接单的订单*/
    Page<OrderDTO> findWaitRecevice(String buyerOpenid,Pageable pageable);

    /** 查询微信用户订单状态为待收货的订单*/
    Page<OrderDTO> findWaitGet(String buyerOpenid,Pageable pageable);

    /** 查询微信用户订单状态为已收货的订单*/
    Page<OrderDTO> findFinishGet(String buyerOpenid,Pageable pageable);

    /** 查询微信用户订单状态为取消的订单*/
    Page<OrderDTO> findUserCancel(String buyerOpenid,Pageable pageable);

    /** 查询微信用户订单状态为未支付的订单*/
    Page<OrderDTO> findUserNoPay(String buyerOpenid,Pageable pageable);







}
