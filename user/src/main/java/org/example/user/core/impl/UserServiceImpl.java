package org.example.user.core.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.UserQryCmd;
import org.example.user.bean.dto.UserDTO;
import org.example.user.core.UserService;
import org.example.user.entity.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public SingleResponse<UserDTO> get(UserQryCmd userQryCmd) {

        UserDTO userDTO = userMapper.get(userQryCmd.getAddress());
        Assert.isTrue(Objects.nonNull(userDTO) && Objects.nonNull(userDTO.getAddress()),"钱包地址未注册");
        return SingleResponse.of(userDTO);
    }
}
