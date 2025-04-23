package org.example.merchant.core;

import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.OrderPageQryCmd;
import org.example.merchant.bean.cmd.OrderUpdateCmd;
import org.example.merchant.bean.dto.OrderDTO;

public interface OrderService {

    MultiResponse<OrderDTO> page(OrderPageQryCmd orderPageQryCmd);


    SingleResponse update(OrderUpdateCmd orderUpdateCmd);
}
