package org.example.user.bean.cmd;

import lombok.Data;
import org.example.user.bean.PageQuery;

import java.time.LocalDateTime;

@Data
public class OrderPageQryCmd extends PageQuery {
    /**
     * 用户钱包
     */
    private String address;
    /**
     * 商品名称
     */
    private String name;

    private String status;

    private String number;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
