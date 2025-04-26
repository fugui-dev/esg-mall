package org.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Product extends BaseEntity{

    private Long merchantId;

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
     * 状态
     */
    private String status;

}
