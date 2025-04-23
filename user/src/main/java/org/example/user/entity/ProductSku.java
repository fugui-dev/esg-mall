package org.example.user.entity;

import lombok.Data;

@Data
public class ProductSku extends BaseEntity{

   private Long productId;
    /**
     * 规格
     */
   private String spce;
    /**
     * 价格
     */
    private String price;
    /**
     * 库存
     */
    private Integer number;

}
