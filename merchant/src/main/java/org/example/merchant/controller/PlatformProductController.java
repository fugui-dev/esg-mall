package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.ProductDTO;
import org.example.merchant.bean.dto.ProductDetailDTO;
import org.example.merchant.common.CommonConstant;
import org.example.merchant.core.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/platform/product")
@RequiredArgsConstructor
public class PlatformProductController {

    @Resource
    private ProductService productService;


    @PostMapping("/page")
    MultiResponse<ProductDTO> page(@RequestBody ProductPageQryCmd productPageQryCmd){
        return productService.page(productPageQryCmd);
    }

    @PostMapping("/info")
    SingleResponse<ProductDetailDTO> get(@RequestBody ProductDetailQryCmd productDetailQryCmd){
        return productService.get(productDetailQryCmd);
    }

    @PostMapping("/down")
    SingleResponse down(@RequestBody ProductDownCmd productDownCmd){
        productDownCmd.setRole(CommonConstant.PLATFORM_ROLE);
        return productService.down(productDownCmd);
    }

    @PostMapping("/examine")
    SingleResponse examine(@RequestBody ProductExamineCmd productExamineCmd){
        return productService.examine(productExamineCmd);
    }


}
