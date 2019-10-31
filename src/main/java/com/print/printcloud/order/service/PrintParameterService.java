package com.print.printcloud.order.service;

import com.print.printcloud.order.dataobject.PrintParameter;

/**
 * Created by 郑钦泓
 * 2019-10-31 21:07
 */

public interface PrintParameterService {

    PrintParameter findOne(String fileId);
}
