package org.example.merchant.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ProductSkuDTO {


    private Long id;

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
