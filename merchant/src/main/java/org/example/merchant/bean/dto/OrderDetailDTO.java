package org.example.merchant.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailDTO {

    private Long id;

    private Long orderId;

    private Long productId;
    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品图片
     */
    private String imageUrl;

    private String describe;
    /**
     * 分类
     */
    private String classify;
    /**
     * 规格
     */
    private List<ProductSpecDTO> specs;


    private Long skuId;
    /**
     * 价格
     */
    private String price;
    /**
     * 数量
     */
    private Integer number;
    /**
     * 总金额
     */
    private String amount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
