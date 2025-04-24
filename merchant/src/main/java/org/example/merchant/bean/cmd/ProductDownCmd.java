package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class ProductDownCmd {

    private Long merchantId;

    private String role;

    private String reason;

    private Long id;
}
