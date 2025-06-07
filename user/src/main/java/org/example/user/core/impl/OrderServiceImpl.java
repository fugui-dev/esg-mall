package org.example.user.core.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.user.bean.MultiResponse;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.*;
import org.example.user.bean.dto.OrderDTO;
import org.example.user.bean.dto.OrderDetailDTO;
import org.example.user.bean.dto.ProductSpecDTO;
import org.example.user.bean.dto.UserDTO;
import org.example.user.common.OrderStatus;
import org.example.user.common.ProductStatus;
import org.example.user.core.OrderService;
import org.example.user.entity.*;
import org.example.user.entity.mapper.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private ProductSpecMapper productSpecMapper;

    @Override
    public MultiResponse<OrderDTO> page(OrderPageQryCmd orderPageQryCmd) {
//        UserDTO userDTO = userMapper.get(orderPageQryCmd.getAddress());
//        Assert.notNull(userDTO, "地址不存在");
//        Assert.notNull(userDTO.getAddress(), "地址不存在");

        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasLength(orderPageQryCmd.getName())) {
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.like(OrderDetail::getName, orderPageQryCmd.getName());

            List<Long> orderIds = orderDetailMapper.selectList(orderDetailLambdaQueryWrapper)
                    .stream()
                    .map(OrderDetail::getOrderId)
                    .distinct()
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(orderIds)) {
                return MultiResponse.buildSuccess();
            }

            queryWrapper.in(Order::getId, orderIds);
        }

        if (Objects.nonNull(orderPageQryCmd.getStartTime()) && Objects.nonNull(orderPageQryCmd.getEndTime())) {
            queryWrapper.between(Order::getCreateTime, orderPageQryCmd.getStartTime(), orderPageQryCmd.getEndTime());
        }

        queryWrapper.eq(Order::getAddress, orderPageQryCmd.getAddress());
        queryWrapper.eq(StringUtils.hasLength(orderPageQryCmd.getStatus()), Order::getStatus, orderPageQryCmd.getStatus());
        queryWrapper.eq(StringUtils.hasLength(orderPageQryCmd.getNumber()),Order::getNumber,orderPageQryCmd.getNumber());
        queryWrapper.orderByDesc(Order::getCreateTime);

        return getOrderDTOMultiResponse(orderPageQryCmd, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse create(OrderCreateCmd orderCreateCmd) {
        try {
//            // 1. 验证用户
//            validateUser(orderCreateCmd.getAddress());

            // 2. 获取并验证商品信息
            List<OrderDetailCreateCmd> orderDetails = orderCreateCmd.getOrderDetailList();
            if (CollectionUtils.isEmpty(orderDetails)) {
                return SingleResponse.buildSuccess();
            }

            List<Long> skuIds = orderDetails.stream()
                    .map(OrderDetailCreateCmd::getSkuId)
                    .sorted()  // 排序防止死锁
                    .collect(Collectors.toList());

            // 3. 批量加锁
            List<RLock> locks = acquireLocks(skuIds);
            if (locks == null) {
                return SingleResponse.buildFailure("系统繁忙，请稍后重试");
            }

            try {
                // 4. 二次检查并处理订单
                return processOrder(orderCreateCmd, skuIds, orderDetails);
            } finally {
                // 5. 释放所有锁
                releaseLocks(locks);
            }
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SingleResponse returnOrder(OrderUpdateCmd orderUpdateCmd) {

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getAddress().equals(orderUpdateCmd.getAddress()),"无权操作");

        Assert.isTrue(order.getStatus().equals(OrderStatus.FINISH.getCode()),"订单状态错误");
        order.setStatus(OrderStatus.RETURN_PROCESSING.getCode());
        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse cancelOrder(OrderUpdateCmd orderUpdateCmd) {

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getAddress().equals(orderUpdateCmd.getAddress()),"无权操作");

        Assert.isTrue(order.getStatus().equals(OrderStatus.PROCESSING.getCode())
                || order.getStatus().equals(OrderStatus.RETURN_PROCESSING.getCode()),"订单状态错误");

        order.setStatus(OrderStatus.CANCEL.getCode());
        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse finishOrder(OrderUpdateCmd orderUpdateCmd) {

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getAddress().equals(orderUpdateCmd.getAddress()),"无权操作");

        Assert.isTrue(order.getStatus().equals(OrderStatus.CONFIRM.getCode()),"订单状态错误");

        order.setStatus(OrderStatus.FINISH.getCode());
        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse returnCancelOrder(OrderUpdateCmd orderUpdateCmd) {

        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getAddress().equals(orderUpdateCmd.getAddress()),"无权操作");

        Assert.isTrue(order.getStatus().equals(OrderStatus.RETURN_PROCESSING.getCode()),"订单状态错误");

        order.setStatus(OrderStatus.FINISH.getCode());
        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse returnFinishOrder(OrderUpdateCmd orderUpdateCmd) {
        Order order = orderMapper.selectById(orderUpdateCmd.getId());
        Assert.notNull(order,"订单不存在");
        Assert.isTrue(order.getAddress().equals(orderUpdateCmd.getAddress()),"无权操作");

        Assert.isTrue(order.getStatus().equals(OrderStatus.RETURN_CONFIRM.getCode()),"订单状态错误");

        order.setStatus(OrderStatus.FINISH.getCode());
        orderMapper.updateById(order);

        return SingleResponse.buildSuccess();
    }


    private void validateUser(String address) {
        UserDTO userDTO = userMapper.get(address);
        Assert.notNull(userDTO, "地址不存在");
        Assert.notNull(userDTO.getAddress(), "地址不存在");
    }

    private List<RLock> acquireLocks(List<Long> skuIds) {
        List<RLock> locks = new ArrayList<>();
        try {
            for (Long skuId : skuIds) {
                RLock lock = redissonClient.getLock("product:sku:stock:lock:" + skuId);
                if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                    releaseLocks(locks);
                    return null;
                }
                locks.add(lock);
            }
            return locks;
        } catch (InterruptedException e) {
            releaseLocks(locks);
            Thread.currentThread().interrupt();
            return null;
        }
    }

//    private SingleResponse processOrder(OrderCreateCmd orderCreateCmd, List<Long> skuIds, List<OrderDetailCreateCmd> orderDetails) {
//
//        List<ProductSku> productSkus = productSkuMapper.selectBatchIds(skuIds);
//
//        List<Long> productIds = orderCreateCmd.getOrderDetailList().stream().map(OrderDetailCreateCmd::getProductId).collect(Collectors.toList());
//
//        // 1. 获取最新商品信息
//        List<Product> products = productMapper.selectBatchIds(productIds);
//
//        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
//
//        Map<Long, OrderDetailCreateCmd> orderDetailMap = orderDetails.stream().collect(Collectors.toMap(OrderDetailCreateCmd::getSkuId, Function.identity()));
//
//        // 2. 验证商品状态和库存
//        for (ProductSku productSku: productSkus) {
//
//            OrderDetailCreateCmd detail = orderDetailMap.get(productSku.getId());
//
//            Product product = productMap.get(detail.getProductId());
//
//            validateProduct(product,productSku, detail.getNumber());
//        }
//
//        // 3. 按商户分组处理订单
//        Map<String, List<Product>> merchantProducts = products.stream().collect(Collectors.groupingBy(Product::getAddress));
//
//        //Map<Long, ProductSku> productSkuMap = productSkus.stream().collect(Collectors.toMap(ProductSku::getId, Function.identity()));
//        // 4. 创建订单并扣减库存
//        merchantProducts.forEach((merchantAddress, merchantProductList) -> {
//            createMerchantOrder(orderCreateCmd, merchantAddress, productSkus , orderDetailMap, merchantProductList);
//        });
//
//        return SingleResponse.buildSuccess();
//    }

    private SingleResponse processOrder(OrderCreateCmd orderCreateCmd, List<Long> skuIds, List<OrderDetailCreateCmd> orderDetails) {
        List<ProductSku> productSkus = productSkuMapper.selectBatchIds(skuIds);
        List<Long> productIds = orderDetails.stream()
                .map(OrderDetailCreateCmd::getProductId)
                .collect(Collectors.toList());

        // 获取商品信息
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Map<Long, OrderDetailCreateCmd> orderDetailMap = orderDetails.stream()
                .collect(Collectors.toMap(OrderDetailCreateCmd::getSkuId, Function.identity()));

        // 验证商品状态和库存
        for (ProductSku productSku : productSkus) {
            OrderDetailCreateCmd detail = orderDetailMap.get(productSku.getId());
            Product product = productMap.get(detail.getProductId());
            validateProduct(product, productSku, detail.getNumber());
        }

        // 按商户地址分组SKU
        Map<Long, List<ProductSku>> merchantSkus = productSkus.stream()
                .collect(Collectors.groupingBy(sku -> {
                    Product product = productMap.get(sku.getProductId());
                    return product.getMerchantId();
                }));

        // 创建各商户订单
        merchantSkus.forEach((merchantId, merchantSkuList) -> {
            List<Product> merchantProducts = merchantSkuList.stream()
                    .map(sku -> productMap.get(sku.getProductId()))
                    .collect(Collectors.toList());

            createMerchantOrder(orderCreateCmd, merchantId, merchantSkuList, orderDetailMap, merchantProducts);
        });

        return SingleResponse.buildSuccess();
    }

    private void validateProduct(Product product,ProductSku productSku, Integer requestedQuantity) {
        Assert.isTrue(ProductStatus.UP.getCode().equals(product.getStatus()), 
                "商品[" + product.getName() + "]未上架");
        Assert.isTrue(productSku.getNumber() >= requestedQuantity,
                "规格[" + productSku.getSpce() + "]库存不足");
    }

    private void createMerchantOrder(OrderCreateCmd orderCreateCmd, Long merchantId, List<ProductSku> productSkus,
            Map<Long, OrderDetailCreateCmd> orderDetailMap, List<Product> products) {

        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        // 1. 获取商户信息
        Merchant merchant = getMerchant(merchantId);

        // 2. 创建订单
        Order order = new Order();
        order.setAddress(orderCreateCmd.getAddress());
        order.setMerchantId(merchantId);
        order.setMerchantName(merchant.getName());
        order.setStatus(OrderStatus.PROCESSING.getCode());
        order.setNumber("NO" + System.currentTimeMillis());
        orderMapper.insert(order);

        // 3. 创建订单详情并扣减库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ProductSku productSku : productSkus) {
            OrderDetailCreateCmd detailCmd = orderDetailMap.get(productSku.getId());
            Product product = productMap.get(productSku.getProductId());
            OrderDetail detail = createOrderDetail(order.getId(), product, productSku, detailCmd);
            orderDetailMapper.insert(detail);

            // 扣减库存
            deductStock(productSku, detailCmd.getNumber());

            totalAmount = totalAmount.add(new BigDecimal(detail.getAmount()));
        }

        // 4. 更新订单总金额
        order.setAmount(totalAmount.toString());
        orderMapper.updateById(order);
    }

    private Merchant getMerchant(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        Assert.notNull(merchant, "商户不存在");
        return merchant;
    }

    private OrderDetail createOrderDetail(Long orderId, Product product,ProductSku productSku, OrderDetailCreateCmd cmd) {

        List<Long> specs = JSONUtil.toList(productSku.getSpce(), Long.class);
        List<ProductSpec> productSpecs = productSpecMapper.selectBatchIds(specs);

        OrderDetail detail = new OrderDetail();
        detail.setOrderId(orderId);
        detail.setClassify(product.getClassify());
        detail.setDescribe(product.getDescribe());
        detail.setName(product.getName());
        detail.setImageUrl(product.getImageUrl());
        detail.setPrice(productSku.getPrice());
        detail.setSpecs(JSONUtil.toJsonStr(productSpecs));
        detail.setNumber(cmd.getNumber());
        detail.setProductId(product.getId());
        detail.setSkuId(productSku.getId());
        detail.setAmount(new BigDecimal(productSku.getPrice()).multiply(BigDecimal.valueOf(cmd.getNumber())).toString());

        return detail;
    }

    private void deductStock(ProductSku productSku, Integer quantity) {
        productSku.setNumber(productSku.getNumber() - quantity);
        int rows = productSkuMapper.updateById(productSku);
        Assert.isTrue(rows > 0, "库存扣减失败");
    }

    private void releaseLocks(List<RLock> locks) {
        if (locks != null) {
            locks.forEach(lock -> {
                if (lock != null && lock.isHeldByCurrentThread()) {
                    try {
                        lock.unlock();
                    } catch (Exception e) {
                        log.error("释放锁失败", e);
                    }
                }
            });
        }
    }

    private MultiResponse<OrderDTO> getOrderDTOMultiResponse(OrderPageQryCmd orderPageQryCmd, LambdaQueryWrapper<Order> queryWrapper) {
        Page<Order> orderPage = orderMapper.selectPage(new Page<>(orderPageQryCmd.getPageNum(), orderPageQryCmd.getPageSize()), queryWrapper);

        if (CollectionUtils.isEmpty(orderPage.getRecords())) {
            return MultiResponse.buildSuccess();
        }

        List<OrderDTO> orderList = buildOrderDTOList(orderPage);
        return MultiResponse.of(orderList, (int) orderPage.getTotal());
    }

    private List<OrderDTO> buildOrderDTOList(Page<Order> orderPage) {
        List<Long> orderIds = orderPage.getRecords().stream().map(Order::getId).collect(Collectors.toList());

        Map<Long, List<OrderDetail>> orderDetailMap = getOrderDetailMap(orderIds);

        return orderPage.getRecords().stream()
                .map(order -> buildOrderDTO(order, orderDetailMap.get(order.getId())))
                .collect(Collectors.toList());
    }

    private Map<Long, List<OrderDetail>> getOrderDetailMap(List<Long> orderIds) {

        LambdaQueryWrapper<OrderDetail> detailQuery = new LambdaQueryWrapper<>();
        detailQuery.in(OrderDetail::getOrderId, orderIds);

        return orderDetailMapper.selectList(detailQuery).stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));
    }

    private OrderDTO buildOrderDTO(Order order, List<OrderDetail> details) {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);

        if (CollectionUtils.isEmpty(details)){

            orderDTO.setOrderDetails(new ArrayList<>());
        }else {
            List<OrderDetailDTO> detailDTOs = details.stream()
                    .map(detail -> {

                        List<ProductSpecDTO> specs = JSONUtil.toList(detail.getSpecs(), ProductSpecDTO.class);
                        OrderDetailDTO dto = new OrderDetailDTO();
                        BeanUtils.copyProperties(detail, dto);
                        dto.setSpecs(specs);
                        return dto;
                    })
                    .collect(Collectors.toList());

            orderDTO.setOrderDetails(detailDTOs);
        }

        return orderDTO;
    }
}
