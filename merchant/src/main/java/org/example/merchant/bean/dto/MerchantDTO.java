package org.example.merchant.bean.dto;

import lombok.Data;

@Data
public class MerchantDTO {


    private Boolean register;

    private String status;

    private String name;

    private String describe;

    /**
     * 联系方式
     */
    private String contact;
}
