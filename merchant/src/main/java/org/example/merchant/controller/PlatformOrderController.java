package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.OrderPageQryCmd;
import org.example.merchant.bean.cmd.OrderUpdateCmd;
import org.example.merchant.bean.dto.OrderDTO;
import org.example.merchant.core.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/platform/order")
@RequiredArgsConstructor
public class PlatformOrderController {

    @Resource
    private OrderService orderService;


    @PostMapping("/page")
    MultiResponse<OrderDTO> page(@RequestBody OrderPageQryCmd orderPageQryCmd){
        return orderService.page(orderPageQryCmd);
    }

}
