package com.print.printcloud.order.dao;

import com.print.printcloud.order.dataobject.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 郑钦泓
 * 2019-10-24 20:12
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    List<OrderDetail> findByOrderId(String orderId);


    void deleteByOrderId(String orderId);
}
