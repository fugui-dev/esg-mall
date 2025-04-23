package org.example.user.bean.cmd;

import lombok.Data;

@Data
public class OrderDetailCreateCmd {

    private Long productId;

    private Long skuId;

    /**
     * 数量
     */
    private Integer number;
}
