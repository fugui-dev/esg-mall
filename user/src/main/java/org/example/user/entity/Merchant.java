package org.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Merchant extends BaseEntity{


    private Long userId;
    /**
     * 经营地址
     */
    private String address;
    /**
     * 商户名称
     */
    private String name;

    @TableField("`describe`")
    private String describe;
    /**
     * 联系方式
     */
    private String contact;

}
