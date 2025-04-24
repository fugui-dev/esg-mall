package org.example.merchant.bean.cmd;

import lombok.Data;
import org.example.merchant.bean.PageQuery;
@Data
public class UserPageQryCmd extends PageQuery {

    private String username;

    private String realName;

    private String phone;

    private String status;

    private String role;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 联系方式
     */
    private String contact;
}
