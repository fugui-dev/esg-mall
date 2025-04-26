package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.Selector;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.ProductDTO;
import org.example.merchant.bean.dto.ProductDetailDTO;
import org.example.merchant.common.CommonConstant;
import org.example.merchant.common.ProductCategory;
import org.example.merchant.core.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/category")
    MultiResponse<Selector> category(){

        List<Selector> selectors = new ArrayList<>();

        for (ProductCategory productCategory: ProductCategory.values()){
            Selector selector = new Selector();
            selector.setKey(productCategory.getCode());
            selector.setValue(productCategory.getDesc());

            selectors.add(selector);
        }

        return MultiResponse.of(selectors);

    }


}
