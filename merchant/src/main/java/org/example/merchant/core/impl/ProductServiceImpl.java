package org.example.merchant.core.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.*;
import org.example.merchant.common.MerchantStatus;
import org.example.merchant.common.ProductStatus;
import org.example.merchant.core.ProductService;
import org.example.merchant.entity.*;
import org.example.merchant.entity.mapper.*;
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
    private MerchantMapper merchantMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ProductSpecMapper productSpecMapper;
    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private ConfigMapper configMapper;
    @Override
    public MultiResponse<ProductDTO> page(ProductPageQryCmd productPageQryCmd) {

        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getMerchantId,productPageQryCmd.getMerchantId());
        queryWrapper.like(StringUtils.hasLength(productPageQryCmd.getName()),Product::getName,productPageQryCmd.getName());
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
        Assert.isTrue(product.getMerchantId().equals(productDetailQryCmd.getMerchantId()),"无权操作");
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

    @Override
    public SingleResponse create(ProductCreateCmd productCreateCmd) {

        Merchant merchant = merchantMapper.selectById(productCreateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getMerchantId,productCreateCmd.getMerchantId());
        queryWrapper.eq(Product::getName,productCreateCmd.getName());

        Long count = productMapper.selectCount(queryWrapper);
        Assert.isTrue(count == 0,"不能重复创建商品");

        LambdaQueryWrapper<Config> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(Config::getKey,"ENABLE_PRODUCT_EXAMINE");

        Config config = configMapper.selectOne(configLambdaQueryWrapper);

        Product product = new Product();
        BeanUtils.copyProperties(productCreateCmd,product);
        if (Objects.nonNull(config) && Integer.parseInt(config.getValue()) == 1){
            product.setStatus(ProductStatus.IN_REVIEW.getCode());
        }else {
            product.setStatus(ProductStatus.DRAFT.getCode());
        }
        productMapper.insert(product);

        ProductImages productImages = new ProductImages();
        productImages.setProductId(product.getId());
        productImages.setIsMain(1);
        productImages.setUrl(product.getImageUrl());

        productImagesMapper.insert(productImages);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse update(ProductUpdateCmd productUpdateCmd) {

        Merchant merchant = merchantMapper.selectById(productUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");



        Product product = productMapper.selectById(productUpdateCmd.getId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productUpdateCmd.getMerchantId()),"无权操作");

        product.setClassify(productUpdateCmd.getClassify());
        product.setDescribe(productUpdateCmd.getDescribe());
        product.setImageUrl(productUpdateCmd.getImageUrl());

        productMapper.updateById(product);

        LambdaQueryWrapper<ProductImages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductImages::getProductId,productUpdateCmd.getId());
        queryWrapper.eq(ProductImages::getIsMain,1);
        ProductImages productImages = productImagesMapper.selectOne(queryWrapper);

        productImages.setUrl(product.getImageUrl());
        productImagesMapper.updateById(productImages);
        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse up(ProductUpCmd productUpCmd) {

        Merchant merchant = merchantMapper.selectById(productUpCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productUpCmd.getId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productUpCmd.getMerchantId()),"无权操作");

        product.setStatus(ProductStatus.UP.getCode());
        productMapper.updateById(product);
        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse down(ProductDownCmd productDownCmd) {

        Merchant merchant = merchantMapper.selectById(productDownCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productDownCmd.getId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productDownCmd.getMerchantId()),"无权操作");

        product.setStatus(ProductStatus.DOWN.getCode());
        productMapper.updateById(product);
        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse number(ProductNumberCmd productNumberCmd) {
        // 使用商品ID作为分布式锁的key，确保同一商品的操作串行化
        String lockKey = "product:sku:stock:lock:" + productNumberCmd.getSkuId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，锁自动过期10秒（防止死锁）
            boolean isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return SingleResponse.buildFailure("系统繁忙，请稍后重试");
            }

            // 校验商户
            Merchant merchant = merchantMapper.selectById(productNumberCmd.getMerchantId());
            Assert.notNull(merchant,"商户不存在");


            Product product = productMapper.selectById(productNumberCmd.getProductId());
            Assert.notNull(product,"商品不存在");
            Assert.isTrue(product.getMerchantId().equals(productNumberCmd.getMerchantId()),"无权操作");

            ProductSku productSku = productSkuMapper.selectById(productNumberCmd.getSkuId());
            productSku.setNumber(productNumberCmd.getNumber());

            productSkuMapper.updateById(productSku);
            // 更新库存

            return SingleResponse.buildSuccess();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return SingleResponse.buildFailure("修改失败，请重试");
        } finally {
            // 确保锁被当前线程持有才释放
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public SingleResponse addProductImages(ProductImagesAddCmd productImagesAddCmd) {

        Merchant merchant = merchantMapper.selectById(productImagesAddCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productImagesAddCmd.getId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productImagesAddCmd.getMerchantId()),"无权操作");

        for (String url : productImagesAddCmd.getUrlList()){

            LambdaQueryWrapper<ProductImages> productImagesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            productImagesLambdaQueryWrapper.eq(ProductImages::getProductId,productImagesAddCmd.getId());
            productImagesLambdaQueryWrapper.eq(ProductImages::getUrl,url);

            ProductImages productImages = productImagesMapper.selectOne(productImagesLambdaQueryWrapper);
            if (Objects.nonNull(productImages)){
                continue;
            }

            productImages = new ProductImages();
            productImages.setUrl(url);
            productImages.setProductId(productImagesAddCmd.getId());
            productImages.setIsMain(0);

            productImagesMapper.insert(productImages);
        }

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse deleteProductImages(ProductImagesDeleteCmd productImagesDeleteCmd) {

        Merchant merchant = merchantMapper.selectById(productImagesDeleteCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productImagesDeleteCmd.getId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productImagesDeleteCmd.getMerchantId()),"无权操作");

        LambdaQueryWrapper<ProductImages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductImages::getIsMain,0);
        queryWrapper.in(ProductImages::getId,productImagesDeleteCmd.getImagesIdList());

        productImagesMapper.delete(queryWrapper);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse createProductSpec(ProductSpecCreateCmd productSpecCreateCmd) {

        LambdaQueryWrapper<ProductSpec> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSpec::getProductId,productSpecCreateCmd.getProductId());
        queryWrapper.eq(ProductSpec::getSpecName,productSpecCreateCmd.getSpecName());
        queryWrapper.eq(ProductSpec::getSpecValue,productSpecCreateCmd.getSpecValue());

        ProductSpec productSpec = productSpecMapper.selectOne(queryWrapper);
        if (Objects.nonNull(productSpec)){
            return SingleResponse.buildSuccess();
        }

        Merchant merchant = merchantMapper.selectById(productSpecCreateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productSpecCreateCmd.getProductId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productSpecCreateCmd    .getMerchantId()),"无权操作");

        productSpec = new ProductSpec();
        productSpec.setProductId(productSpecCreateCmd.getProductId());
        productSpec.setSpecName(productSpecCreateCmd.getSpecName());
        productSpec.setSpecValue(productSpecCreateCmd.getSpecValue());

        productSpecMapper.insert(productSpec);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse updateProductSpec(ProductSpecUpdateCmd productSpecUpdateCmd) {

        ProductSpec productSpec = productSpecMapper.selectById(productSpecUpdateCmd.getId());
        Assert.notNull(productSpec,"规格不存在");

        LambdaQueryWrapper<ProductSpec> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSpec::getProductId,productSpec.getProductId());
        queryWrapper.eq(ProductSpec::getSpecName,productSpecUpdateCmd.getSpecName());
        queryWrapper.eq(ProductSpec::getSpecValue,productSpecUpdateCmd.getSpecValue());

        Long count = productSpecMapper.selectCount(queryWrapper);
        if (count > 0){
            return SingleResponse.buildSuccess();
        }


        Merchant merchant = merchantMapper.selectById(productSpecUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productSpec.getProductId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productSpecUpdateCmd.getMerchantId()),"无权操作");

        productSpec.setSpecName(productSpecUpdateCmd.getSpecName());
        productSpec.setSpecValue(productSpecUpdateCmd.getSpecValue());

        productSpecMapper.updateById(productSpec);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse deleteProductSpec(ProductSpecDeleteCmd productSpecDeleteCmd) {

        ProductSpec productSpec = productSpecMapper.selectById(productSpecDeleteCmd.getId());
        Assert.notNull(productSpec,"规格不存在");


        Merchant merchant = merchantMapper.selectById(productSpecDeleteCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productSpec.getProductId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productSpecDeleteCmd.getMerchantId()),"无权操作");

        productSpecMapper.deleteById(productSpecDeleteCmd.getId());

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse createProductSku(ProductSkuCreateCmd productSkuCreateCmd) {

        String spec = JSONUtil.toJsonPrettyStr(productSkuCreateCmd.getSpceList());

        LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSku::getProductId,productSkuCreateCmd.getProductId());
        queryWrapper.eq(ProductSku::getSpce,spec);

        ProductSku productSku = productSkuMapper.selectOne(queryWrapper);
        if (Objects.nonNull(productSku)){
            return SingleResponse.buildSuccess();
        }

        Merchant merchant = merchantMapper.selectById(productSkuCreateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productSkuCreateCmd.getProductId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productSkuCreateCmd.getMerchantId()),"无权操作");

        productSku = new ProductSku();
        productSku.setProductId(productSkuCreateCmd.getProductId());
        productSku.setPrice(productSkuCreateCmd.getPrice());
        productSku.setSpce(spec);
        productSku.setNumber(productSkuCreateCmd.getNumber());

        productSkuMapper.insert(productSku);
        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse updateProductSku(ProductSkuUpdateCmd productSkuUpdateCmd) {

        ProductSku productSku = productSkuMapper.selectById(productSkuUpdateCmd.getId());
        Assert.notNull(productSku,"SKU不存在");


        Merchant merchant = merchantMapper.selectById(productSkuUpdateCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productSku.getProductId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productSkuUpdateCmd.getMerchantId()),"无权操作");

        String spec = JSONUtil.toJsonPrettyStr(productSkuUpdateCmd.getSpceList());

        productSku.setNumber(productSkuUpdateCmd.getNumber());
        productSku.setSpce(spec);
        productSku.setPrice(productSkuUpdateCmd.getPrice());

        productSkuMapper.updateById(productSku);
        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse deleteProductSku(ProductSkuDeleteCmd productSkuDeleteCmd) {

        ProductSku productSku = productSkuMapper.selectById(productSkuDeleteCmd.getId());
        Assert.notNull(productSku,"SKU不存在");
        Merchant merchant = merchantMapper.selectById(productSkuDeleteCmd.getMerchantId());
        Assert.notNull(merchant,"商户不存在");


        Product product = productMapper.selectById(productSku.getProductId());
        Assert.notNull(product,"商品不存在");
        Assert.isTrue(product.getMerchantId().equals(productSkuDeleteCmd.getMerchantId()),"无权操作");

        productSkuMapper.deleteById(productSkuDeleteCmd.getId());

        return SingleResponse.buildSuccess();
    }
}
