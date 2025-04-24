package org.example.merchant.bean.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class MerchantDTO {

    private Long id;


    private Long userId;
    /**
     * 经营地址
     */
    private String address;
    /**
     * 商户名称
     */
    private String name;


    private String describe;
    /**
     * 联系方式
     */
    private String contact;
}
