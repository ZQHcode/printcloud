package com.print.printcloud.order.service.impl;

import com.print.printcloud.order.dao.PaperRepository;
import com.print.printcloud.order.dataobject.Paper;
import com.print.printcloud.order.service.PaperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郑钦泓
 * 2019-10-31 21:37
 */
@Service
@Slf4j
public class PaperServiceImpl implements PaperService {

    @Autowired
    private PaperRepository paperRepository;

    @Override
    public Paper findOne(String pid) {

        return paperRepository.findById(pid).orElse(null);
    }
}
