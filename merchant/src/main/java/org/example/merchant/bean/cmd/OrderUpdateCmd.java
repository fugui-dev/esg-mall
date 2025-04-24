package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class OrderUpdateCmd {

    private Long merchantId;

    private Long id;

    private String status;

}
