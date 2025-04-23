package org.example.merchant.bean.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ProductSkuUpdateCmd {

    private String address;

    private Long id;

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
