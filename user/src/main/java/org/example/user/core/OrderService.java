package org.example.user.core;


import org.example.user.bean.MultiResponse;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.OrderCreateCmd;
import org.example.user.bean.cmd.OrderPageQryCmd;
import org.example.user.bean.cmd.OrderUpdateCmd;
import org.example.user.bean.dto.OrderDTO;

public interface OrderService {

    MultiResponse<OrderDTO> page(OrderPageQryCmd orderPageQryCmd);

    SingleResponse create(OrderCreateCmd orderCreateCmd);

    SingleResponse returnOrder(OrderUpdateCmd orderUpdateCmd);

    SingleResponse cancelOrder(OrderUpdateCmd orderUpdateCmd);

    SingleResponse finishOrder(OrderUpdateCmd orderUpdateCmd);
}
