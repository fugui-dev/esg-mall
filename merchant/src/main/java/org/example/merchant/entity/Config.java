package org.example.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Config extends BaseEntity{

    @TableField("`key`")
    private String key;

    @TableField("`value`")
    private String value;
}
