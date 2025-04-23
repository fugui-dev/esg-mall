package org.example.merchant.entity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.example.merchant.entity.Product;
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
