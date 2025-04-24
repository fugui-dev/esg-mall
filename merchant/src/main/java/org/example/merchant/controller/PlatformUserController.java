package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.UserCreateCmd;
import org.example.merchant.bean.cmd.UserPageQryCmd;
import org.example.merchant.bean.cmd.UserResetPasswordCmd;
import org.example.merchant.bean.cmd.UserUpdateCmd;
import org.example.merchant.bean.dto.UserDetailDTO;
import org.example.merchant.bean.dto.UserResetPasswordDTO;
import org.example.merchant.core.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/platform/user")
@RequiredArgsConstructor
public class PlatformUserController {


    @Resource
    private UserService userService;

    @PostMapping("/add")
    SingleResponse createUser(@RequestBody UserCreateCmd userCreateCmd){
      return userService.createUser(userCreateCmd);
    }

    @PostMapping("/update")
    SingleResponse updateUser(@RequestBody UserUpdateCmd userUpdateCmd){
        return userService.updateUser(userUpdateCmd);
    }

    @PostMapping("/reset/password")
    SingleResponse<UserResetPasswordDTO> resetPassword(@RequestBody UserResetPasswordCmd userResetPasswordCmd){
        return userService.resetPassword(userResetPasswordCmd);
    }

    @PostMapping("/page")
    MultiResponse<UserDetailDTO> page(@RequestBody UserPageQryCmd userPageQryCmd){
        return userService.page(userPageQryCmd);
    }


}
