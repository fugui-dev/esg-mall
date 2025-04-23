package org.example.user.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    /**
     * 买家地址
     */
    private String address;
    /**
     * 卖家地址
     */
    private String merchantAddress;

    private String merchantName;
    /**
     * 订单状态
     */
    private String status;
    /**
     * 总金额
     */
    private String amount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<OrderDetailDTO> orderDetails;
}
