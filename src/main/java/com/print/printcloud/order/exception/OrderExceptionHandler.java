package com.print.printcloud.order.exception;

import com.print.printcloud.order.utils.*;
import com.print.printcloud.order.vo.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Created by 郑钦泓
 * 2019-10-24 20:24
 */
@ControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(value = OrderException.class)
    public ResultVO<String> exceptionHandler(Exception e) {

        OrderException orderException = (OrderException) e;
        return ResultVOUtil.error(orderException.getCode(), orderException.getMessage());
    }

}
