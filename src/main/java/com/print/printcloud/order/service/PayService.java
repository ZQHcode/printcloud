package com.print.printcloud.order.service;

import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundResponse;
import com.print.printcloud.order.dto.OrderDTO;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 郑钦泓
 * 2019-10-29 10:37
 */
public interface PayService {

    //支付订单
    PayResponse pay(OrderDTO orderDTO,String code);

    //订单回调通知订单支付完成
    PayResponse notify(String notifyData);

    //订单退款
    RefundResponse refund(OrderDTO orderDTO);
}
