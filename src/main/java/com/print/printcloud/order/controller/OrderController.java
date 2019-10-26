package com.print.printcloud.order.controller;

import com.print.printcloud.order.dto.OrderDTO;
import com.print.printcloud.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 郑钦泓
 * 2019-10-25 21:53
 */

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    //创建订单
    //TODO

    //查找一份订单
    @GetMapping("/findOne")
    @ResponseBody
    public OrderDTO findOne(String orderId){

        return orderService.findOne(orderId);
    }

    //查询某个用户的所有订单列表
    @GetMapping("/findUserOrderList")
    @ResponseBody
    public Page<OrderDTO> findUserOrderList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                                            @RequestParam String buyerOpenid){
        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findUserOrderList(buyerOpenid,request);
    }

    //取消订单
    @PostMapping("/cancel")
    @ResponseBody
    public OrderDTO cancel(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        return orderService.cancel(orderDTO);
    }

    //修改订单为待配送或待收货
    @PostMapping("/send")
    @ResponseBody
    public OrderDTO send(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        return orderService.send(orderDTO);
    }

    //修改订单为已配送或已收货
    @PostMapping("/finish")
    @ResponseBody
    public OrderDTO finish(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        return orderService.finish(orderDTO);
    }

    //修改订单为支付成功
    @PostMapping("/paid")
    @ResponseBody
    public OrderDTO paid(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        return orderService.paid(orderDTO);
    }

    //查询所有订单
    @GetMapping("/findList")
    @ResponseBody
    public Page<OrderDTO> findList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size
                                   ){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findList(request);
    }

    //查询状态为待打印的订单
    @GetMapping("/findWaitPrint")
    @ResponseBody
    public Page<OrderDTO> findWaitPrint(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size
                                        ){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findWaitPrint(request);
    }

    //查询状态为待配送的订单
    @GetMapping("/findWaitSend")
    @ResponseBody
    public Page<OrderDTO> findWaitSend(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size
                                        ){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findWaitSend(request);
    }

    //查询状态为已配送的订单
    @GetMapping("/findFinishSend")
    @ResponseBody
    public Page<OrderDTO> findFinishSend(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size
                                       ){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findFinishSend(request);
    }

    //查询状态为已取消的订单
    @GetMapping("/findCancel")
    @ResponseBody
    public Page<OrderDTO> findCancel(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size
                                         ){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findCancel(request);
    }

    //查询状态为待接单的订单
    @GetMapping("/findWaitRecevice")
    @ResponseBody
    public Page<OrderDTO> findWaitRecevice(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                           String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findWaitRecevice(buyerOpenid,request);
    }

    //查询状态为待收货的订单
    @GetMapping("/findWaitGet")
    @ResponseBody
    public Page<OrderDTO> findWaitGet(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                           String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findWaitGet(buyerOpenid,request);
    }

    //查询状态为已收货的订单
    @GetMapping("/findFinishGet")
    @ResponseBody
    public Page<OrderDTO> findFinishGet(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                      String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findFinishGet(buyerOpenid,request);
    }

    //查询状态为用户个人已取消的订单
    @GetMapping("/findUserCancel")
    @ResponseBody
    public Page<OrderDTO> findUserCancel(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                        String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findUserCancel(buyerOpenid,request);
    }

    //查询状态为用户个人未支付的订单
    @GetMapping("/findUserNoPay")
    @ResponseBody
    public Page<OrderDTO> findUserNoPay(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return orderService.findUserNoPay(buyerOpenid,request);
    }
}
