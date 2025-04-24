package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.OrderPageQryCmd;
import org.example.merchant.bean.cmd.OrderUpdateCmd;
import org.example.merchant.bean.dto.OrderDTO;
import org.example.merchant.core.OrderService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private BaseController baseController;

    @PostMapping("/page")
    MultiResponse<OrderDTO> page(@RequestBody OrderPageQryCmd orderPageQryCmd){
        Long merchantId = baseController.getMerchantId();
        orderPageQryCmd.setMerchantId(merchantId);
        return orderService.page(orderPageQryCmd);

    }

    /**
     * 订单确认
     * @param orderUpdateCmd
     * @return
     */
    @PostMapping("/confirm")
    SingleResponse confirmOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        orderUpdateCmd.setMerchantId(merchantId);
        return orderService.confirmOrder(orderUpdateCmd);
    }

    /**
     * 订单关闭
     * @param orderUpdateCmd
     * @return
     */
    @PostMapping("/close")
    SingleResponse closeOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        orderUpdateCmd.setMerchantId(merchantId);
        return orderService.closeOrder(orderUpdateCmd);
    }

    /**
     * 拒绝退款
     * @param orderUpdateCmd
     * @return
     */
    @PostMapping("/return/refuse")
    SingleResponse returnRefuseOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        orderUpdateCmd.setMerchantId(merchantId);
        return orderService.returnRefuseOrder(orderUpdateCmd);
    }

    /**
     * 确认退款
     * @param orderUpdateCmd
     * @return
     */
    @PostMapping("/return/confirm")
    SingleResponse returnConfirmOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        orderUpdateCmd.setMerchantId(merchantId);
        return orderService.returnConfirmOrder(orderUpdateCmd);
    }


    /**
     * 关闭退款
     * @param orderUpdateCmd
     * @return
     */
    @PostMapping("/return/close")
    SingleResponse returnCloseOrder( @RequestBody OrderUpdateCmd orderUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        orderUpdateCmd.setMerchantId(merchantId);
        return orderService.returnCloseOrder(orderUpdateCmd);
    }
}
