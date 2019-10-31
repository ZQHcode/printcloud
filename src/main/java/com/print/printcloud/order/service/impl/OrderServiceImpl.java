package com.print.printcloud.order.service.impl;

import com.print.printcloud.order.dao.OrderDetailRepository;
import com.print.printcloud.order.dao.OrderMasterRepository;
import com.print.printcloud.order.dataobject.OrderDetail;
import com.print.printcloud.order.dataobject.OrderMaster;
import com.print.printcloud.order.dataobject.Paper;
import com.print.printcloud.order.dataobject.PrintParameter;
import com.print.printcloud.order.dto.OrderDTO;
import com.print.printcloud.order.enums.OrderStatusEnum;
import com.print.printcloud.order.enums.PayStatusEnum;
import com.print.printcloud.order.enums.ResultEnum;
import com.print.printcloud.order.exception.OrderException;
import com.print.printcloud.order.service.OrderService;
import com.print.printcloud.order.service.PaperService;
import com.print.printcloud.order.service.PayService;
import com.print.printcloud.order.service.PrintParameterService;
import com.print.printcloud.order.utils.KeyUtil;
import com.print.printcloud.order.utils.OrderMaster2OrderDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by 郑钦泓
 * 2019-10-24 21:31
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private PrintParameterService printParameterService;

    @Autowired
    private PayService payService;

    @Autowired
    private PaperService paperService;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        String orderId = KeyUtil.genUniqueKey();
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);

//        List<CartDTO> cartDTOList = new ArrayList<>();

        //1. 查询（数量, 价格）
        for (OrderDetail orderDetail: orderDTO.getOrderDetailList()) {
            
            PrintParameter printParameter =  printParameterService.findOne(orderDetail.getProductId());
            if (printParameter == null) {
                throw new OrderException(ResultEnum.File_NOT_EXIST);
            }

            Paper paper = paperService.findOne("1");
            //2. 计算订单总价
            orderDetail.setProductName(printParameter.getFileName());
            BigDecimal productPrice = paper.getPprice()
                    .multiply(new BigDecimal(printParameter.getPages()));
            //设置文件的价格
            orderDetail.setProductPrice(productPrice);
            orderAmount = productPrice
                    .multiply(new BigDecimal(printParameter.getNumber()))
                    .add(orderAmount);

            //订单详情入库
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setOrderId(orderId);
            orderDetail.setProductQuantity(printParameter.getNumber());
            orderDetail.setPageId("1");
            orderDetailRepository.save(orderDetail);
        }


        //3. 写入订单数据库（orderMaster和orderDetail）
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        BigDecimal sendAmount = new BigDecimal(1);
        orderMaster.setSendAmount(sendAmount);
        orderAmount = orderAmount.add(sendAmount);
        orderMaster.setOrderAmount(orderAmount);

        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {


        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);
        if (orderMaster == null) {
            throw new OrderException(ResultEnum.ORDER_NOT_EXIST);
        }

        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new OrderException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster, orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findUserOrderList(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid, pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));


        }
        return new PageImpl<OrderDTO>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDTO cancel(OrderDTO orderDTO) {

        OrderMaster orderMaster = new OrderMaster();

        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【取消订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        Integer payStatus = orderDTO.getPayStatus();
        orderDTO.setPayStatus(PayStatusEnum.WAIT.getCode());
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【取消订单】更新失败, orderMaster={}", orderMaster);
            throw new OrderException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //如果已支付, 需要退款
        if (orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS.getCode())) {
//            payService.refund(orderDTO);
        }

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO send(OrderDTO orderDTO) {

        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【配送订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);
        }

        if (!orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS.getCode())){
            log.error("【配送订单】订单状态不正确, orderId={}, payStatus={}", orderDTO.getOrderId(), orderDTO.getPayStatus());
            throw new OrderException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.WAIT_SEND.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【配送订单】更新失败, orderMaster={}", orderMaster);
            throw new OrderException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(OrderDTO orderDTO) {
        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.WAIT_SEND.getCode())) {
            log.error("【完结订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.SENT.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【完结订单】更新失败, orderMaster={}", orderMaster);
            throw new OrderException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }

    @Override
    public OrderDTO paid(OrderDTO orderDTO) {

        //判断支付状态
        if (!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("【订单支付完成】订单支付状态不正确, orderDTO={}", orderDTO);
            throw new OrderException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //修改支付状态
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        orderDTO.setOrderStatus(OrderStatusEnum.NEW.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【订单支付完成】更新失败, orderMaster={}", orderMaster);
            throw new OrderException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findWaitPrint(Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatus(OrderStatusEnum.NEW.getCode(), PayStatusEnum.SUCCESS.getCode(), pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findWaitSend(Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatus(OrderStatusEnum.WAIT_SEND.getCode(), PayStatusEnum.SUCCESS.getCode(), pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findFinishSend(Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatus(OrderStatusEnum.SENT.getCode(), PayStatusEnum.SUCCESS.getCode(), pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findCancel(Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatus(OrderStatusEnum.CANCEL.getCode(), PayStatusEnum.WAIT.getCode(), pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findWaitRecevice(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatusAndBuyerOpenid(OrderStatusEnum.NEW.getCode(),PayStatusEnum.SUCCESS.getCode(),buyerOpenid,pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findWaitGet(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatusAndBuyerOpenid(OrderStatusEnum.WAIT_SEND.getCode(),PayStatusEnum.SUCCESS.getCode(),buyerOpenid,pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findFinishGet(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatusAndBuyerOpenid(OrderStatusEnum.SENT.getCode(),PayStatusEnum.SUCCESS.getCode(),buyerOpenid,pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findUserCancel(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatusAndBuyerOpenid(OrderStatusEnum.CANCEL.getCode(),PayStatusEnum.WAIT.getCode(),buyerOpenid,pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public Page<OrderDTO> findUserNoPay(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByOrderStatusAndPayStatusAndBuyerOpenid(OrderStatusEnum.NEW.getCode(),PayStatusEnum.WAIT.getCode(),buyerOpenid,pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        for (OrderDTO orderDTO : orderDTOList){

            orderDTO.setOrderDetailList(orderDetailRepository.findByOrderId(orderDTO.getOrderId()));

        }

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    @Transactional
    public void deleteByOrderId(String orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);
        if (orderMaster == null) {
            throw new OrderException(ResultEnum.ORDER_NOT_EXIST);
        }

        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new OrderException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }

        //判断订单状态
        Integer orderStatus = orderMaster.getOrderStatus();
        if (!(orderStatus.equals(OrderStatusEnum.CANCEL.getCode())) && !(orderStatus.equals(OrderStatusEnum.SENT.getCode()))){

            log.error("【取消订单】订单状态不正确, orderId={}, orderStatus={}", orderMaster.getOrderId(), orderMaster.getOrderStatus());
            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);
        }
        orderDetailRepository.deleteByOrderId(orderId);
        orderMasterRepository.deleteById(orderId);
    }

    @Override
    @Transactional
    public List<OrderDTO> batchFinish(List<OrderDTO> orderDTO) {
        orderDTO.stream().forEach(e->finish(e));
        return orderDTO;

    }

    @Override
    @Transactional
    public List<OrderDTO> batchWaitSend(List<OrderDTO> orderDTO) {
        orderDTO.stream().forEach(e->send(e));
        return orderDTO;
    }

    @Override
    @Transactional
    public void batchDeleteByOrderId(List<String> orderId) {
        orderId.stream().forEach(e->deleteByOrderId(e));
    }
}
