package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class ProductCreateCmd {


    private Long merchantId;
    private String address;

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
    private String specs;
    /**
     * 价格
     */
    private String price;

}
