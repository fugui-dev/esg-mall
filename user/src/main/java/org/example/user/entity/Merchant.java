package org.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Merchant extends BaseEntity{


    private String address;

    private String name;
    @TableField("`describe`")
    private String describe;

    /**
     *状态
     */
    private String status;

}
