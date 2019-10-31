package com.print.printcloud.order.service;

import com.print.printcloud.order.dataobject.Paper;

/**
 * Created by 郑钦泓
 * 2019-10-31 21:36
 */
public interface PaperService {

    Paper findOne(String pid);
}
