package org.example.merchant.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductSpecDTO {

    private Long id;

    private Long productId;

    private String specName;   // 规格名称（如"颜色"）

    private String specValue; // 可选值（如"黑,白,蓝"用逗号分隔）

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
