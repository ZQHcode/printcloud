package com.print.printcloud.order.enums;

import lombok.Getter;

/**
 * Created by 郑钦泓
 * 2019-10-24 18:06
 */
@Getter
public enum OrderStatusEnum {

    NEW(0, "待打印或待接单"),
    WAIT_SEND(1, "待配送或待收货"),
    SENT(2, "已配送或已收货"),
    CANCEL(3, "已取消"),
    ;

    private Integer code;

    private String message;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
