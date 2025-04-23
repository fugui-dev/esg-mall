package org.example.merchant.entity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.merchant.entity.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
