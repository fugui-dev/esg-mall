package org.example.merchant.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductImagesDTO {

    private Long id;

    private Long productId;

    private String url;

    /**
     * 是否主图：0-否 1-是
     */
    private Integer isMain;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
