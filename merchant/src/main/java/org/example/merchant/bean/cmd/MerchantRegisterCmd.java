package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class MerchantRegisterCmd {

    private String address;

    private String name;

    private String describe;

    /**
     * 联系方式
     */
    private String contact;
}
