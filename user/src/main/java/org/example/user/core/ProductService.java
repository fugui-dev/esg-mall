package org.example.user.core;


import org.example.user.bean.MultiResponse;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.ProductDetailQryCmd;
import org.example.user.bean.cmd.ProductPageQryCmd;
import org.example.user.bean.dto.ProductDTO;
import org.example.user.bean.dto.ProductDetailDTO;

public interface ProductService {

    MultiResponse<ProductDTO> page(ProductPageQryCmd productPageQryCmd);

    SingleResponse<ProductDetailDTO> get(ProductDetailQryCmd productDetailQryCmd);

}
