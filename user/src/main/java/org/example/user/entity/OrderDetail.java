package org.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class OrderDetail extends BaseEntity {

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

    @TableField("`describe`")
    private String describe;
    /**
     * 分类
     */
    private String classify;
    /**
     * 规格
     */
    private String specs;

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
}
