package org.example.user.entity;

import lombok.Data;
@Data
public class ProductImages extends BaseEntity {

    private Long productId;

    private String url;

    /**
     * 是否主图：0-否 1-是
     */
    private Integer isMain;
}
