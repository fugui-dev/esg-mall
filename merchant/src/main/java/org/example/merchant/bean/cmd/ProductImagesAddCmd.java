package org.example.merchant.bean.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ProductImagesAddCmd {

    private String address;

    private Long id;

    private List<String> urlList;
}
