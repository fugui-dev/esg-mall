package org.example.merchant.bean.cmd;

import lombok.Data;
import org.example.merchant.bean.PageQuery;

import java.time.LocalDateTime;

@Data
public class OrderPageQryCmd extends PageQuery {
    /**
     * 用户钱包
     */
    private String address;

    /**
     * 卖家地址
     */
    private Long merchantId;
    /**
     * 商品名称
     */
    private String name;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
