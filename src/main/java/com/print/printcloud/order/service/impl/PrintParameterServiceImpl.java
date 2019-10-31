package com.print.printcloud.order.service.impl;

import com.print.printcloud.order.dao.OrderDetailRepository;
import com.print.printcloud.order.dao.PrintParameterRepository;
import com.print.printcloud.order.dataobject.PrintParameter;
import com.print.printcloud.order.service.PrintParameterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郑钦泓
 * 2019-10-31 21:07
 */
@Service
@Slf4j
public class PrintParameterServiceImpl implements PrintParameterService {

    @Autowired
    private PrintParameterRepository printParameterRepository;

    @Override
    public PrintParameter findOne(String fileId) {
        return printParameterRepository.findById(fileId).orElse(null);
    }
}
