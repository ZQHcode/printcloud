package com.print.printcloud.order.controller;

import com.print.printcloud.order.dto.OrderDTO;
import com.print.printcloud.order.service.OrderService;
import com.print.printcloud.order.utils.ResultVOUtil;
import com.print.printcloud.order.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 郑钦泓
 * 2019-10-25 21:53
 */

@Controller
@RequestMapping("/order")
@Slf4j
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    //创建订单
    //TODO

    //查找一份订单
    @GetMapping("/findOne")
    @ResponseBody
    public ResultVO findOne(String orderId){

        return ResultVOUtil.success(orderService.findOne(orderId));
    }

    //查询某个用户的所有订单列表
    @GetMapping("/findUserOrderList")
    @ResponseBody
    public ResultVO findUserOrderList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size,
                                            @RequestParam String buyerOpenid){
        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findUserOrderList(buyerOpenid,request));
    }

    //取消订单
    @PostMapping("/cancel")
    @ResponseBody
    public ResultVO cancel(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        orderService.cancel(orderDTO);
        return ResultVOUtil.success();
    }

    //修改订单为待配送或待收货
    @PostMapping("/send")
    @ResponseBody
    public ResultVO send(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        orderService.send(orderDTO);
        return ResultVOUtil.success();
    }

    //修改订单为已配送或已收货
    @PostMapping("/finish")
    @ResponseBody
    public ResultVO finish(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        orderService.finish(orderDTO);
        return ResultVOUtil.success();
    }

    //修改订单为支付成功
    @PostMapping("/paid")
    @ResponseBody
    public ResultVO paid(@RequestParam("orderId") String orderId){

        OrderDTO orderDTO = orderService.findOne(orderId);
        orderService.paid(orderDTO);
        return ResultVOUtil.success();
    }

    //查询所有订单
    @GetMapping("/findList")
    @ResponseBody
    public ResultVO findList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size
                                   ){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findList(request));
    }

    //查询状态为待打印的订单
    @GetMapping("/findWaitPrint")
    @ResponseBody
    public ResultVO findWaitPrint(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size
                                        ){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findWaitPrint(request));
    }

    //查询状态为待配送的订单
    @GetMapping("/findWaitSend")
    @ResponseBody
    public ResultVO findWaitSend(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size
                                        ){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findWaitSend(request));
    }

    //查询状态为已配送的订单
    @GetMapping("/findFinishSend")
    @ResponseBody
    public ResultVO findFinishSend(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size
                                       ){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findFinishSend(request));
    }

    //查询状态为已取消的订单
    @GetMapping("/findCancel")
    @ResponseBody
    public ResultVO findCancel(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "size", defaultValue = "10") Integer size
                                         ){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findCancel(request));

    }

    //查询状态为待接单的订单
    @GetMapping("/findWaitRecevice")
    @ResponseBody
    public ResultVO findWaitRecevice(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                           String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findWaitRecevice(buyerOpenid,request));
    }

    //查询状态为待收货的订单
    @GetMapping("/findWaitGet")
    @ResponseBody
    public ResultVO findWaitGet(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                           String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findWaitGet(buyerOpenid,request));
    }

    //查询状态为已收货的订单
    @GetMapping("/findFinishGet")
    @ResponseBody
    public ResultVO findFinishGet(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                      String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findFinishGet(buyerOpenid,request));
    }

    //查询状态为用户个人已取消的订单
    @GetMapping("/findUserCancel")
    @ResponseBody
    public ResultVO findUserCancel(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                        String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findUserCancel(buyerOpenid,request));
    }

    //查询状态为用户个人未支付的订单
    @GetMapping("/findUserNoPay")
    @ResponseBody
    public ResultVO findUserNoPay(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String buyerOpenid){

        PageRequest request = new PageRequest(page - 1, size);
        return ResultVOUtil.success(orderService.findUserNoPay(buyerOpenid,request));
    }

    //根据订单OrderId删除订单
    @PostMapping("/deleteByOrderId")
    @ResponseBody
    public ResultVO findUserNoPay(@RequestParam("orderId") String orderId){

        orderService.deleteByOrderId(orderId);
        return ResultVOUtil.success();
    }

    /** 批量修改订单状态为已配送或已收货. */
    @PostMapping("/batchFinish")
    @ResponseBody
    public ResultVO batchFinish(@RequestParam("orderIds") List<String> orderIds){

        List<OrderDTO> orderDTOList = orderIds.stream().map(e -> orderService.findOne(e)).collect(Collectors.toList());
        orderService.batchFinish(orderDTOList);
        return ResultVOUtil.success();
    };

    /** 根据orderId批量删除订单*/
    @PostMapping("/batchDeleteByOrderId")
    @ResponseBody
    public ResultVO batchDeleteByOrderId(@RequestParam("orderIds") List<String> orderIds){

        orderService.batchDeleteByOrderId(orderIds);
        return ResultVOUtil.success();
    };
}
