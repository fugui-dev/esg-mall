package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.MerchantQryCmd;
import org.example.merchant.bean.cmd.MerchantRegisterCmd;
import org.example.merchant.bean.cmd.MerchantUpdateCmd;
import org.example.merchant.bean.dto.MerchantDTO;
import org.example.merchant.bean.dto.MerchantRegisterDTO;
import org.example.merchant.core.MerchantService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @PostMapping("/info")
    SingleResponse<MerchantDTO> get(@RequestBody MerchantQryCmd merchantQryCmd){
        Assert.isTrue(StringUtils.hasLength(merchantQryCmd.getAddress()),"钱包不能为空");
        return merchantService.get(merchantQryCmd);
    }

    @PostMapping("/register")
    SingleResponse<MerchantRegisterDTO> register(@RequestBody MerchantRegisterCmd merchantRegisterCmd){
        Assert.isTrue(StringUtils.hasLength(merchantRegisterCmd.getAddress()),"钱包不能为空");
        return merchantService.register(merchantRegisterCmd);
    }

    @PostMapping("/update")
    SingleResponse update(@RequestBody MerchantUpdateCmd merchantUpdateCmd){
        Assert.isTrue(StringUtils.hasLength(merchantUpdateCmd.getAddress()),"钱包不能为空");
        return merchantService.update(merchantUpdateCmd);
    }
}
