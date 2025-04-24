package org.example.merchant.bean.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ProductImagesDeleteCmd {

    private Long merchantId;

    private Long id;

    private List<Long> imagesIdList;
}
