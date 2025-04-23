package org.example.user.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PROCESSING("processing", "订单待处理"),

    CANCEL("cancel","取消订单"),

    CONFIRM("confirm", "订单已确认"),

    FINISH("finish", "订单已完成"),

    RETURN_PROCESSING("return_processing", "退款订单待处理"),

    RETURN_REFUSE("return_refuse","拒绝退款"),

    RETURN_CANCEL("return_cancel","取消退款订单"),

    RETURN_CONFIRM("return_confirm", "退款订单已确认"),

    RETURN_FINISH("return_finish", "退款订单已完成");

    private final String code;

    private final String desc;
}
