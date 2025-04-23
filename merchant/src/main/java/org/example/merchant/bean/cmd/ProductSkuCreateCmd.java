package org.example.merchant.bean.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ProductSkuCreateCmd {

    private String address;

    private Long productId;
    /**
     * 规格
     */
    private List<Long> spceList;
    /**
     * 价格
     */
    private String price;
    /**
     * 库存
     */
    private Integer number;
}
