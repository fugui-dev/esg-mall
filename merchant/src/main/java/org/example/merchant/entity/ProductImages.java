package org.example.merchant.entity;

import lombok.Data;
import org.example.merchant.entity.BaseEntity;
@Data
public class ProductImages extends BaseEntity {

    private Long productId;

    private String url;

    /**
     * 是否主图：0-否 1-是
     */
    private Integer isMain;
}
