package org.example.merchant.core.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.OrderPageQryCmd;
import org.example.merchant.bean.cmd.OrderUpdateCmd;
import org.example.merchant.bean.dto.OrderDTO;
import org.example.merchant.bean.dto.OrderDetailDTO;
import org.example.merchant.bean.dto.ProductSpecDTO;
import org.example.merchant.common.MerchantStatus;
import org.example.merchant.common.OrderStatus;
import org.example.merchant.core.OrderService;
import org.example.merchant.entity.Merchant;
import org.example.merchant.entity.Order;
import org.example.merchant.entity.OrderDetail;
import org.example.merchant.entity.Product;
import org.example.merchant.entity.mapper.MerchantMapper;
import org.example.merchant.entity.mapper.OrderDetailMapper;
import org.example.merchant.entity.mapper.OrderMapper;
import org.example.merchant.entity.mapper.ProductMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private MerchantMapper merchantMapper;

    @Override
    public MultiResponse<OrderDTO> page(OrderPageQryCmd orderPageQryCmd) {

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasLength(orderPageQryCmd.getName())){

            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.like(OrderDetail::getName,orderPageQryCmd.getName());

            List<Long> orderIds = orderDetailMapper.selectList(orderDetailLambdaQueryWrapper)
                    .stream()
                    .map(OrderDetail::getOrderId)
                    .distinct()
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(orderIds)){
                return MultiResponse.buildSuccess();
            }

            queryWrapper.in(Order::getId,orderIds);
        }
        if (Objects.nonNull(orderPageQryCmd.getStartTime()) && Objects.nonNull(orderPageQryCmd.getEndTime())){
            queryWrapper.between(Order::getCreateTime,orderPageQryCmd.getStartTime(),orderPageQryCmd.getEndTime());
        }
        queryWrapper.eq(StringUtils.hasLength(orderPageQryCmd.getAddress()),Order::getAddress,orderPageQryCmd.getAddress());
        queryWrapper.eq(Order::getMerchantId,orderPageQryCmd.getMerchantId());
        queryWrapper.eq(StringUtils.hasLength(orderPageQryCmd.getStatus()),Order::getStatus,orderPageQryCmd.getStatus());

        queryWrapper.orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = orderMapper.selectPage(new Page<>(orderPageQryCmd.getPageNum(), orderPageQryCmd.getPageSize()), queryWrapper);
        if (CollectionUtils.isEmpty(orderPage.getRecords())){
            return MultiResponse.buildSuccess();
        }

        List<Long> orderIds = orderPage.getRecords().stream().map(Order::getId).collect(Collectors.toList());

        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.in(OrderDetail::getOrderId,orderIds);

        Map<Long, List<OrderDetail>> orderDetailMap = orderDetailMapper.selectList(orderDetailLambdaQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        List<OrderDTO> orderList = new ArrayList<>();

        for (Order order: orderPage.getRecords()){

            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(order,orderDTO);

            List<OrderDetail> orderDetails = orderDetailMap.get(order.getId());

            List<OrderDetailDTO> orderDetailList = new ArrayList<>();

            for (OrderDetail orderDetail:orderDetails){

                List<ProductSpecDTO> specs = JSONUtil.toList(orderDetail.getSpecs(), ProductSpecDTO.class);

                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                BeanUtils.copyProperties(orderDetail,orderDetailDTO);
                orderDetailDTO.setSpecs(specs);
                orderDetailList.add(orderDetailDTO);
            }

            orderDTO.setOrderDetails(orderDetailList);

            orderList.add(orderDTO);
        }


        return MultiResponse.of(orderList,(int)orderPage.getTotal());
    }

    @Override
    public SingleResponse confirmOrder(OrderUpdateCmd orderUpdateCmd) {


        Merchant merchant = merchantMapper.selectById(orderUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getMerchantId().equals(orderUpdateCmd.getMerchantId()),"无权操作");

        if (!OrderStatus.PROCESSING.getCode().equals(order.getStatus())){
            return SingleResponse.buildFailure("只有待处理的订单才能确认");
        }

        order.setStatus(OrderStatus.CONFIRM.getCode());

        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse closeOrder(OrderUpdateCmd orderUpdateCmd) {

        Merchant merchant = merchantMapper.selectById(orderUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getMerchantId().equals(orderUpdateCmd.getMerchantId()),"无权操作");

        if (!OrderStatus.PROCESSING.getCode().equals(order.getStatus()) &&
                !OrderStatus.CONFIRM.getCode().equals(order.getStatus()) &&
                !OrderStatus.CANCEL.getCode().equals(order.getStatus()) ){
            return SingleResponse.buildFailure("只有待处理、已确认、已取消的订单才能关闭");
        }

        order.setStatus(OrderStatus.CLOSE.getCode());

        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse returnRefuseOrder(OrderUpdateCmd orderUpdateCmd) {

        Merchant merchant = merchantMapper.selectById(orderUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getMerchantId().equals(orderUpdateCmd.getMerchantId()),"无权操作");

        if (!OrderStatus.RETURN_PROCESSING.getCode().equals(order.getStatus())){
            return SingleResponse.buildFailure("只有待处理的退款订单才能拒绝退款");
        }
        order.setStatus(OrderStatus.RETURN_REFUSE.getCode());

        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse returnConfirmOrder(OrderUpdateCmd orderUpdateCmd) {

        Merchant merchant = merchantMapper.selectById(orderUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getMerchantId().equals(orderUpdateCmd.getMerchantId()),"无权操作");

        if (!OrderStatus.RETURN_PROCESSING.getCode().equals(order.getStatus())){
            return SingleResponse.buildFailure("只有待处理的退款订单才能确认退款");
        }
        order.setStatus(OrderStatus.RETURN_CONFIRM.getCode());

        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse returnCloseOrder(OrderUpdateCmd orderUpdateCmd) {

        Merchant merchant = merchantMapper.selectById(orderUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getMerchantId().equals(orderUpdateCmd.getMerchantId()),"无权操作");

        if (!OrderStatus.RETURN_PROCESSING.getCode().equals(order.getStatus()) &&
                !OrderStatus.RETURN_CONFIRM.getCode().equals(order.getStatus()) &&
                !OrderStatus.RETURN_CANCEL.getCode().equals(order.getStatus()) ){
            return SingleResponse.buildFailure("只有待处理、已确认、已取消的退款订单才能关闭");
        }

        order.setStatus(OrderStatus.RETURN_CLOSE.getCode());

        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }
}
