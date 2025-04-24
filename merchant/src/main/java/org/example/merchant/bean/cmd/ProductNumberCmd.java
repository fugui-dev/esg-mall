package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class ProductNumberCmd {

    private Long merchantId;

    private Long productId;

    private Long skuId;

    private Integer number;
}
