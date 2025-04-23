package org.example.user.entity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.user.bean.dto.UserDTO;

@Mapper
public interface UserMapper {


    @Select("SELECT address,balance FROM esg.address_relation WHERE address = #{address}")
    UserDTO get(@Param("address") String address);

}
