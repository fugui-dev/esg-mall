package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class ProductUpdateCmd {

    private Integer id;

    private Long merchantId;

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

}
