package org.example.merchant.bean.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailDTO extends  ProductDTO {

    private List<ProductImagesDTO> images;

    private List<ProductSpecDTO> specs;

    private List<ProductSkuDTO> skus;
}
