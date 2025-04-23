package org.example.user.entity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.user.entity.Order;


@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
