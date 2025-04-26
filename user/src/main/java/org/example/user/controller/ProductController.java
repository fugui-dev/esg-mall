package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.user.bean.MultiResponse;
import org.example.user.bean.Selector;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.ProductDetailQryCmd;
import org.example.user.bean.cmd.ProductPageQryCmd;
import org.example.user.bean.dto.ProductDTO;
import org.example.user.bean.dto.ProductDetailDTO;
import org.example.user.common.ProductCategory;
import org.example.user.common.ProductStatus;
import org.example.user.core.ProductService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    @Resource
    private ProductService productService;

    @PostMapping("/page")
    MultiResponse<ProductDTO> page(@RequestBody ProductPageQryCmd productPageQryCmd){
        productPageQryCmd.setStatus(ProductStatus.UP.getCode());
        return productService.page(productPageQryCmd);

    }
    @PostMapping("/get")
    SingleResponse<ProductDetailDTO> get(@RequestBody ProductDetailQryCmd productDetailQryCmd){
        return productService.get(productDetailQryCmd);

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
