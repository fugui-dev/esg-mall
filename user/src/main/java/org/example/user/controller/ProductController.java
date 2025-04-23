package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.user.bean.MultiResponse;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.ProductDetailQryCmd;
import org.example.user.bean.cmd.ProductPageQryCmd;
import org.example.user.bean.dto.ProductDTO;
import org.example.user.bean.dto.ProductDetailDTO;
import org.example.user.common.ProductStatus;
import org.example.user.core.ProductService;
import org.springframework.util.Assert;
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
        //Assert.notNull(productPageQryCmd.getAddress(),"钱包不能为空");
        productPageQryCmd.setStatus(ProductStatus.UP.getCode());
        return productService.page(productPageQryCmd);

    }
    @PostMapping("/get")
    SingleResponse<ProductDetailDTO> get(@RequestBody ProductDetailQryCmd productDetailQryCmd){
        //Assert.notNull(productDetailQryCmd.getAddress(),"钱包不能为空");
        return productService.get(productDetailQryCmd);

    }
}
