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
        queryWrapper.eq(Order::getMerchantAddress,orderPageQryCmd.getMerchantAddress());
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
    public SingleResponse update(OrderUpdateCmd orderUpdateCmd) {

        LambdaQueryWrapper<Merchant> merchantLambdaQueryWrapper = new LambdaQueryWrapper<>();
        merchantLambdaQueryWrapper.eq(Merchant::getAddress,orderUpdateCmd.getAddress());

        Merchant merchant = merchantMapper.selectOne(merchantLambdaQueryWrapper);
        Assert.notNull(merchant,"商户不存在");
        Assert.isTrue(MerchantStatus.ENABLE.getCode().equals(merchant.getStatus()),"商户未启用");

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(merchant.getAddress().equals(order.getMerchantAddress()),"无权操作");

        order.setStatus(orderUpdateCmd.getStatus());

        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }
}
