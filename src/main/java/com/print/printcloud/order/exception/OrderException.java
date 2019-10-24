package com.print.printcloud.order.exception;

import com.print.printcloud.order.enums.ResultEnum;
import lombok.Getter;

/**
 * Created by 郑钦泓
 * 2019-10-24 20:19
 */
@Getter
public class OrderException extends RuntimeException {

    private Integer code;

    public OrderException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
    }

    public OrderException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
