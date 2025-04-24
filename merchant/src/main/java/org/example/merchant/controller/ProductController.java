package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.ProductDTO;
import org.example.merchant.bean.dto.ProductDetailDTO;
import org.example.merchant.core.ProductService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/merchant/product")
@RequiredArgsConstructor
public class ProductController {

    @Resource
    private ProductService productService;
    @Resource
    private BaseController baseController;

    @PostMapping("/page")
    MultiResponse<ProductDTO> page(@RequestBody ProductPageQryCmd productPageQryCmd){
        Long merchantId = baseController.getMerchantId();
        productPageQryCmd.setMerchantId(merchantId);
        return productService.page(productPageQryCmd);
    }

    @PostMapping("/info")
    SingleResponse<ProductDetailDTO> get(@RequestBody ProductDetailQryCmd productDetailQryCmd){
        Long merchantId = baseController.getMerchantId();
        productDetailQryCmd.setMerchantId(merchantId);
        return productService.get(productDetailQryCmd);
    }

    @PostMapping("/create")
    SingleResponse create(@RequestBody ProductCreateCmd productCreateCmd){
        Long merchantId = baseController.getMerchantId();
        productCreateCmd.setMerchantId(merchantId);
        return productService.create(productCreateCmd);
    }

    @PostMapping("/update")
    SingleResponse update(@RequestBody ProductUpdateCmd productUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        productUpdateCmd.setMerchantId(merchantId);
        return productService.update(productUpdateCmd);
    }

    @PostMapping("/up")
    SingleResponse up(@RequestBody ProductUpCmd productUpCmd){
        Long merchantId = baseController.getMerchantId();
        productUpCmd.setMerchantId(merchantId);
        return productService.up(productUpCmd);
    }

    @PostMapping("/down")
    SingleResponse down(@RequestBody ProductDownCmd productDownCmd){
        Long merchantId = baseController.getMerchantId();
        productDownCmd.setMerchantId(merchantId);
        return productService.down(productDownCmd);
    }

    @PostMapping("/number")
    SingleResponse number(@RequestBody ProductNumberCmd productNumberCmd){
        Long merchantId = baseController.getMerchantId();
        productNumberCmd.setMerchantId(merchantId);
        return productService.number(productNumberCmd);
    }

    @PostMapping("/images/add")
    SingleResponse addProductImages(@RequestBody ProductImagesAddCmd productImagesAddCmd){
        Long merchantId = baseController.getMerchantId();
        productImagesAddCmd.setMerchantId(merchantId);
        return productService.addProductImages(productImagesAddCmd);
    }

    @PostMapping("/images/delete")
    SingleResponse deleteProductImages(@RequestBody  ProductImagesDeleteCmd productImagesDeleteCmd){
        Long merchantId = baseController.getMerchantId();
        productImagesDeleteCmd.setMerchantId(merchantId);
        return productService.deleteProductImages(productImagesDeleteCmd);
    }

    @PostMapping("/spec/add")
    SingleResponse createProductSpec(@RequestBody  ProductSpecCreateCmd productSpecCreateCmd){
        Long merchantId = baseController.getMerchantId();
        productSpecCreateCmd.setMerchantId(merchantId);
        return productService.createProductSpec(productSpecCreateCmd);
    }

    @PostMapping("/spec/update")
    SingleResponse updateProductSpec(@RequestBody ProductSpecUpdateCmd productSpecUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        productSpecUpdateCmd.setMerchantId(merchantId);
        return productService.updateProductSpec(productSpecUpdateCmd);
    }

    @PostMapping("/spec/delete")
    SingleResponse deleteProductSpec(@RequestBody ProductSpecDeleteCmd  productSpecDeleteCmd){
        Long merchantId = baseController.getMerchantId();
        productSpecDeleteCmd.setMerchantId(merchantId);
        return productService.deleteProductSpec(productSpecDeleteCmd);
    }

    @PostMapping("/sku/add")
    SingleResponse createProductSku(@RequestBody ProductSkuCreateCmd productSkuCreateCmd){
        Long merchantId = baseController.getMerchantId();
        productSkuCreateCmd.setMerchantId(merchantId);
        return productService.createProductSku(productSkuCreateCmd);
    }

    @PostMapping("/sku/update")
    SingleResponse updateProductSku(@RequestBody ProductSkuUpdateCmd productSkuUpdateCmd){
        Long merchantId = baseController.getMerchantId();
        productSkuUpdateCmd.setMerchantId(merchantId);
        return productService.updateProductSku(productSkuUpdateCmd);
    }

    @PostMapping("/sku/delete")
    SingleResponse deleteProductSku(@RequestBody ProductSkuDeleteCmd productSkuDeleteCmd){
        Long merchantId = baseController.getMerchantId();
        productSkuDeleteCmd.setMerchantId(merchantId);
        return productService.deleteProductSku(productSkuDeleteCmd);
    }
}
