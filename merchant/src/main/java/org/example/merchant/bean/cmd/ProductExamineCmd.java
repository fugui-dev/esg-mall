package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class ProductExamineCmd {

    private Long productId;

    private Boolean result;

    private String reason;
}
