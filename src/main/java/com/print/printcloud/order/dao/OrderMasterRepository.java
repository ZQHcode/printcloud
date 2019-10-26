package com.print.printcloud.order.dao;

import com.print.printcloud.order.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 郑钦泓
 * 2019-10-24 17:55
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster, String> {

    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);

    Page<OrderMaster> findByOrderStatusAndPayStatus(Integer orderStatus,Integer payStatus,Pageable pageable);

    Page<OrderMaster> findByOrderStatusAndPayStatusAndBuyerOpenid(Integer orderStatus,Integer payStatus,String buyerOpenid,Pageable pageable);

}
