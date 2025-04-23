package org.example.user.bean.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailDTO extends  ProductDTO {

    private List<ProductImagesDTO> images;


    private List<ProductSpecDTO> specs;

    private List<ProductSkuDTO> skus;
}
