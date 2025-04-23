package org.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "`order`")
public class Order extends BaseEntity{
    /**
     * 买家地址
     */
    private String address;
    /**
     * 卖家地址
     */
    private String merchantAddress;

    private String merchantName;
    /**
     * 订单状态
     */
    private String status;
    /**
     * 总金额
     */
    private String amount;

}
