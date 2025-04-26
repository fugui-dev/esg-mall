package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.UserLoginCmd;
import org.example.merchant.bean.dto.UserDTO;
import org.example.merchant.bean.dto.UserInfoDTO;
import org.example.merchant.core.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private  HttpServletRequest request;

    @PostMapping("/login")
    SingleResponse<UserDTO> login(@RequestBody UserLoginCmd userLoginCmd){
        return userService.login(userLoginCmd);
    }

    @PostMapping("/logout")
    SingleResponse logout(){

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }else {
            return SingleResponse.buildFailure("token 不能为空");
        }
        return userService.logout(token);
    }

}
