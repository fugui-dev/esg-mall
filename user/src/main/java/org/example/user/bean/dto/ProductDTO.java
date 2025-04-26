package org.example.user.bean.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDTO {

    private Long id;

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

    private Long merchantId;
    /**
     * 经营地址
     */
    private String merchantAddress;
    /**
     * 商户名称
     */
    private String merchantName;


    private String merchantDescribe;
    /**
     * 联系方式
     */
    private String merchantContact;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
