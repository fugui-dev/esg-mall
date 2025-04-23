package org.example.user.core.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.user.bean.MultiResponse;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.ProductDetailQryCmd;
import org.example.user.bean.cmd.ProductPageQryCmd;
import org.example.user.bean.dto.*;
import org.example.user.core.ProductService;
import org.example.user.entity.Product;
import org.example.user.entity.ProductImages;
import org.example.user.entity.ProductSku;
import org.example.user.entity.ProductSpec;
import org.example.user.entity.mapper.ProductImagesMapper;
import org.example.user.entity.mapper.ProductMapper;
import org.example.user.entity.mapper.ProductSkuMapper;
import org.example.user.entity.mapper.ProductSpecMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductImagesMapper productImagesMapper;
    @Resource
    private ProductSpecMapper productSpecMapper;
    @Resource
    private ProductSkuMapper productSkuMapper;


    @Override
    public MultiResponse<ProductDTO> page(ProductPageQryCmd productPageQryCmd) {

        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasLength(productPageQryCmd.getName()),Product::getName,productPageQryCmd.getName());
        queryWrapper.eq(StringUtils.hasLength(productPageQryCmd.getAddress()),Product::getAddress,productPageQryCmd.getAddress());
        queryWrapper.eq(StringUtils.hasLength(productPageQryCmd.getClassify()),Product::getClassify,productPageQryCmd.getClassify());
        queryWrapper.eq(StringUtils.hasLength(productPageQryCmd.getStatus()),Product::getStatus,productPageQryCmd.getStatus());
        queryWrapper.orderByDesc(Product::getCreateTime);

        Page<Product> productPage = productMapper.selectPage(new Page<>(productPageQryCmd.getPageNum(), productPageQryCmd.getPageSize()), queryWrapper);
        if (CollectionUtils.isEmpty(productPage.getRecords())){
            return MultiResponse.buildSuccess();
        }

        List<ProductDTO> list = new ArrayList<>();

        for (Product product : productPage.getRecords()){
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(product,productDTO);
            list.add(productDTO);
        }

        return MultiResponse.of(list,(int)productPage.getTotal());
    }

    @Override
    public SingleResponse<ProductDetailDTO> get(ProductDetailQryCmd productDetailQryCmd) {

        Product product = productMapper.selectById(productDetailQryCmd.getId());
        Assert.notNull(product,"商品不存在");

        ProductDetailDTO productDetailDTO = new ProductDetailDTO();
        BeanUtils.copyProperties(product,productDetailDTO);


        LambdaQueryWrapper<ProductImages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductImages::getProductId,productDetailQryCmd.getId());

        List<ProductImages> productImagesList = productImagesMapper.selectList(queryWrapper);

        List<ProductImagesDTO> images = new ArrayList<>();

        for (ProductImages productImages : productImagesList){
            ProductImagesDTO productImagesDTO = new ProductImagesDTO();
            BeanUtils.copyProperties(productImages,productImagesDTO);

            images.add(productImagesDTO);
        }


        LambdaQueryWrapper<ProductSpec> specLambdaQueryWrapper = new LambdaQueryWrapper<>();
        specLambdaQueryWrapper.eq(ProductSpec::getProductId,productDetailQryCmd.getId());

        List<ProductSpec> productSpecs = productSpecMapper.selectList(specLambdaQueryWrapper);

        List<ProductSpecDTO> specList = new ArrayList<>();

        for (ProductSpec productSpec : productSpecs){

            ProductSpecDTO productSpecDTO = new ProductSpecDTO();
            BeanUtils.copyProperties(productSpec,productSpecDTO);

            specList.add(productSpecDTO);
        }

        LambdaQueryWrapper<ProductSku> skuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        skuLambdaQueryWrapper.eq(ProductSku::getProductId,productDetailQryCmd.getId());

        List<ProductSku> productSkus = productSkuMapper.selectList(skuLambdaQueryWrapper);

        List<ProductSkuDTO> skuList = new ArrayList<>();

        for (ProductSku productSku:productSkus){

            ProductSkuDTO productSkuDTO = new ProductSkuDTO();
            BeanUtils.copyProperties(productSku,productSkuDTO);

            List<Long> specs = JSONUtil.toList(productSku.getSpce(), Long.class);

            productSkuDTO.setSpceList(specs);

            skuList.add(productSkuDTO);

        }

        productDetailDTO.setSpecs(specList);

        productDetailDTO.setImages(images);

        productDetailDTO.setSkus(skuList);


        return SingleResponse.of(productDetailDTO);
    }

}
