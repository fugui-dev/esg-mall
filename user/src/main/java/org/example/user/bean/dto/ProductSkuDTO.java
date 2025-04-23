package org.example.user.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
