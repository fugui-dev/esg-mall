package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.user.bean.MultiResponse;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.OrderCreateCmd;
import org.example.user.bean.cmd.OrderPageQryCmd;
import org.example.user.bean.cmd.OrderUpdateCmd;
import org.example.user.bean.dto.OrderDTO;
import org.example.user.core.OrderService;
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

    @PostMapping("/page")
    MultiResponse<OrderDTO> page(@RequestBody OrderPageQryCmd orderPageQryCmd){
        Assert.isTrue(StringUtils.hasLength(orderPageQryCmd.getAddress()),"钱包不能为空");
        return orderService.page(orderPageQryCmd);

    }

    @PostMapping("/create")
    SingleResponse create(@RequestBody OrderCreateCmd orderCreateCmd){
        Assert.isTrue(StringUtils.hasLength(orderCreateCmd.getAddress()),"钱包不能为空");
        return orderService.create(orderCreateCmd);
    }

    @PostMapping("/return")
    SingleResponse returnOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(orderUpdateCmd.getAddress()),"钱包不能为空");
        return orderService.returnOrder(orderUpdateCmd);
    }

    @PostMapping("/cancel")
    SingleResponse cancelOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(orderUpdateCmd.getAddress()),"钱包不能为空");
        return orderService.cancelOrder(orderUpdateCmd);
    }

    @PostMapping("/finish")
    SingleResponse finishOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(orderUpdateCmd.getAddress()),"钱包不能为空");
        return orderService.finishOrder(orderUpdateCmd);
    }

    @PostMapping("/return/cancel")
    SingleResponse returnCancelOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(orderUpdateCmd.getAddress()),"钱包不能为空");
        return orderService.returnCancelOrder(orderUpdateCmd);
    }

    @PostMapping("/return/finish")
    SingleResponse returnFinishOrder(@RequestBody OrderUpdateCmd orderUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(orderUpdateCmd.getAddress()),"钱包不能为空");
        return orderService.returnFinishOrder(orderUpdateCmd);
    }

}
