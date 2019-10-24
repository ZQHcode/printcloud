package com.print.printcloud.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by 郑钦泓
 * 2019-10-24 17:47
 */

@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 3938626101358237007L;

    /** 错误码. */
    private Integer code;

    /** 提示信息. */
    private String msg;

    /** 具体内容. */
    private T data;
}