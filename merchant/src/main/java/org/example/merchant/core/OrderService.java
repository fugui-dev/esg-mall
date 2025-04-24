package org.example.merchant.core;

import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.OrderPageQryCmd;
import org.example.merchant.bean.cmd.OrderUpdateCmd;
import org.example.merchant.bean.dto.OrderDTO;

public interface OrderService {

    MultiResponse<OrderDTO> page(OrderPageQryCmd orderPageQryCmd);

    /**
     * 订单确认
     * @param orderUpdateCmd
     * @return
     */
    SingleResponse confirmOrder(OrderUpdateCmd orderUpdateCmd);

    /**
     * 订单关闭
     * @param orderUpdateCmd
     * @return
     */
    SingleResponse closeOrder(OrderUpdateCmd orderUpdateCmd);

    /**
     * 拒绝退款
     * @param orderUpdateCmd
     * @return
     */
    SingleResponse returnRefuseOrder(OrderUpdateCmd orderUpdateCmd);

    /**
     * 确认退款
     * @param orderUpdateCmd
     * @return
     */
    SingleResponse returnConfirmOrder(OrderUpdateCmd orderUpdateCmd);


    /**
     * 确认退款
     * @param orderUpdateCmd
     * @return
     */
    SingleResponse returnCloseOrder(OrderUpdateCmd orderUpdateCmd);
}
