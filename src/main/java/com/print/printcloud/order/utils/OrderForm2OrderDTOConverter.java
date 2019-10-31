package com.print.printcloud.order.utils;

import com.print.printcloud.order.dataobject.OrderDetail;
import com.print.printcloud.order.dto.OrderDTO;
import com.print.printcloud.order.form.OrderForm;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 郑钦泓
 * 2019-10-31 20:53
 */
@Slf4j
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        orderDTO.setBuyerAddress(orderForm.getAddress());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());

        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderForm.getItems().stream().forEach((e)->{

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductId(e);
            orderDetailList.add(orderDetail);
        });


        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }
}
