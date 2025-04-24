package org.example.merchant.entity;

import lombok.Data;

@Data
public class ProductOperateLog extends BaseEntity{

    private Long productId;

    private String type;

    private String reason;
}
