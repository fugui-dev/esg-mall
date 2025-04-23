package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class ProductNumberCmd {

    private String address;

    private Long productId;

    private Long skuId;

    private Integer number;
}
