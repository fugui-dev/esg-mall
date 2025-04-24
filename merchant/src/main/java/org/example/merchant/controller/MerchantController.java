package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.MerchantQryCmd;
import org.example.merchant.bean.cmd.MerchantRegisterCmd;
import org.example.merchant.bean.cmd.MerchantUpdateCmd;
import org.example.merchant.bean.cmd.UserChangePasswordCmd;
import org.example.merchant.bean.dto.MerchantDTO;
import org.example.merchant.bean.dto.MerchantRegisterDTO;
import org.example.merchant.core.MerchantService;
import org.example.merchant.core.UserService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    private final UserService userService;

    private final BaseController baseController;

    @PostMapping("/info")
    SingleResponse<MerchantDTO> get(@RequestBody MerchantQryCmd merchantQryCmd){

        Long userId = baseController.getUserId();
        merchantQryCmd.setUserId(userId);
        return merchantService.get(merchantQryCmd);
    }

    @PostMapping("/register")
    SingleResponse<MerchantRegisterDTO> register(@RequestBody MerchantRegisterCmd merchantRegisterCmd){
        Long userId = baseController.getUserId();
        merchantRegisterCmd.setUserId(userId);
        return merchantService.register(merchantRegisterCmd);
    }

    @PostMapping("/update")
    SingleResponse update(@RequestBody MerchantUpdateCmd merchantUpdateCmd){
        Long userId = baseController.getUserId();
        merchantUpdateCmd.setUserId(userId);
        return merchantService.update(merchantUpdateCmd);
    }

    @PostMapping("/change/password")
    SingleResponse changePassword(@RequestBody UserChangePasswordCmd userChangePasswordCmd){

        Long userId = baseController.getUserId();
        userChangePasswordCmd.setId(userId);
        return userService.changePassword(userChangePasswordCmd);
    }
}
