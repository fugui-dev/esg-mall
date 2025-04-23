package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.UserQryCmd;
import org.example.user.bean.dto.UserDTO;
import org.example.user.core.UserService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/info")
    SingleResponse<UserDTO> get(@RequestBody UserQryCmd userQryCmd){
        Assert.notNull(userQryCmd.getAddress(),"钱包地址不能为空");
        return userService.get(userQryCmd);
    }
}
