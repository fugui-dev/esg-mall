package org.example.merchant.bean.cmd;

import lombok.Data;
import org.example.merchant.bean.PageQuery;
@Data
public class ProductPageQryCmd extends PageQuery {

    private String address;

    private String name;

    private String classify;

    private String status;
}
