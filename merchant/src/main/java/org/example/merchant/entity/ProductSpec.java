package org.example.merchant.entity;

import lombok.Data;

@Data
public class ProductSpec extends BaseEntity{

    private Long productId;

    private String specName;   // 规格名称（如"颜色"）

    private String specValue; // 可选值（如"黑,白,蓝"用逗号分隔）
}
