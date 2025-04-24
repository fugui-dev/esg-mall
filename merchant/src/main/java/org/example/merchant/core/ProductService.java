package org.example.merchant.core;

import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.ProductDTO;
import org.example.merchant.bean.dto.ProductDetailDTO;

public interface ProductService {

    MultiResponse<ProductDTO> page(ProductPageQryCmd productPageQryCmd);

    SingleResponse<ProductDetailDTO> get(ProductDetailQryCmd productDetailQryCmd);

    SingleResponse create(ProductCreateCmd productCreateCmd);

    SingleResponse update(ProductUpdateCmd productUpdateCmd);

    SingleResponse up(ProductUpCmd productUpCmd);

    SingleResponse down(ProductDownCmd productDownCmd);

    SingleResponse number(ProductNumberCmd productNumberCmd);

    SingleResponse addProductImages(ProductImagesAddCmd productImagesAddCmd);

    SingleResponse deleteProductImages(ProductImagesDeleteCmd productImagesDeleteCmd);

    SingleResponse createProductSpec(ProductSpecCreateCmd productSpecCreateCmd);

    SingleResponse updateProductSpec(ProductSpecUpdateCmd productSpecUpdateCmd);

    SingleResponse deleteProductSpec(ProductSpecDeleteCmd  productSpecDeleteCmd);

    SingleResponse createProductSku(ProductSkuCreateCmd productSkuCreateCmd);

    SingleResponse updateProductSku(ProductSkuUpdateCmd productSkuUpdateCmd);

    SingleResponse deleteProductSku(ProductSkuDeleteCmd productSkuDeleteCmd);

    SingleResponse examine(ProductExamineCmd productExamineCmd);
}
