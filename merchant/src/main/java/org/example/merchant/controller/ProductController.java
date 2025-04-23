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
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    @Resource
    private ProductService productService;

    @PostMapping("/page")
    MultiResponse<ProductDTO> page(@RequestBody ProductPageQryCmd productPageQryCmd){
        Assert.isTrue(StringUtils.hasLength(productPageQryCmd.getAddress()),"钱包不能为空");
        return productService.page(productPageQryCmd);
    }

    @PostMapping("/info")
    SingleResponse<ProductDetailDTO> get(@RequestBody ProductDetailQryCmd productDetailQryCmd){
        Assert.isTrue(StringUtils.hasLength(productDetailQryCmd.getAddress()),"钱包不能为空");
        return productService.get(productDetailQryCmd);
    }

    @PostMapping("/create")
    SingleResponse create(@RequestBody ProductCreateCmd productCreateCmd){
        Assert.isTrue(StringUtils.hasLength(productCreateCmd.getAddress()),"钱包不能为空");
        return productService.create(productCreateCmd);
    }

    @PostMapping("/update")
    SingleResponse update(@RequestBody ProductUpdateCmd productUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(productUpdateCmd.getAddress()),"钱包不能为空");
        return productService.update(productUpdateCmd);
    }

    @PostMapping("/up")
    SingleResponse up(@RequestBody ProductUpCmd productUpCmd){
        Assert.isTrue(StringUtils.hasLength(productUpCmd.getAddress()),"钱包不能为空");
        return productService.up(productUpCmd);
    }

    @PostMapping("/down")
    SingleResponse down(@RequestBody ProductDownCmd productDownCmd){
        Assert.isTrue(StringUtils.hasLength(productDownCmd.getAddress()),"钱包不能为空");
        return productService.down(productDownCmd);
    }

    @PostMapping("/number")
    SingleResponse number(@RequestBody ProductNumberCmd productNumberCmd){
        Assert.isTrue(StringUtils.hasLength(productNumberCmd.getAddress()),"钱包不能为空");
        return productService.number(productNumberCmd);
    }

    @PostMapping("/images/add")
    SingleResponse addProductImages(@RequestBody ProductImagesAddCmd productImagesAddCmd){
        Assert.isTrue(StringUtils.hasLength(productImagesAddCmd.getAddress()),"钱包不能为空");
        return productService.addProductImages(productImagesAddCmd);
    }

    @PostMapping("/images/delete")
    SingleResponse deleteProductImages(@RequestBody  ProductImagesDeleteCmd productImagesDeleteCmd){
        Assert.isTrue(StringUtils.hasLength(productImagesDeleteCmd.getAddress()),"钱包不能为空");
        return productService.deleteProductImages(productImagesDeleteCmd);
    }

    @PostMapping("/spec/add")
    SingleResponse createProductSpec(@RequestBody  ProductSpecCreateCmd productSpecCreateCmd){
        Assert.isTrue(StringUtils.hasLength(productSpecCreateCmd.getAddress()),"钱包不能为空");
        return productService.createProductSpec(productSpecCreateCmd);
    }

    @PostMapping("/spec/update")
    SingleResponse updateProductSpec(@RequestBody ProductSpecUpdateCmd productSpecUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(productSpecUpdateCmd.getAddress()),"钱包不能为空");
        return productService.updateProductSpec(productSpecUpdateCmd);
    }

    @PostMapping("/spec/delete")
    SingleResponse deleteProductSpec(@RequestBody ProductSpecDeleteCmd  productSpecDeleteCmd){
        Assert.isTrue(StringUtils.hasLength(productSpecDeleteCmd.getAddress()),"钱包不能为空");
        return productService.deleteProductSpec(productSpecDeleteCmd);
    }

    @PostMapping("/sku/add")
    SingleResponse createProductSku(@RequestBody ProductSkuCreateCmd productSkuCreateCmd){
        Assert.isTrue(StringUtils.hasLength(productSkuCreateCmd.getAddress()),"钱包不能为空");
        return productService.createProductSku(productSkuCreateCmd);
    }

    @PostMapping("/sku/update")
    SingleResponse updateProductSku(@RequestBody ProductSkuUpdateCmd productSkuUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(productSkuUpdateCmd.getAddress()),"钱包不能为空");
        return productService.updateProductSku(productSkuUpdateCmd);
    }

    @PostMapping("/sku/delete")
    SingleResponse deleteProductSku(@RequestBody ProductSkuDeleteCmd productSkuDeleteCmd){
        Assert.isTrue(StringUtils.hasLength(productSkuDeleteCmd.getAddress()),"钱包不能为空");
        return productService.deleteProductSku(productSkuDeleteCmd);
    }
}
