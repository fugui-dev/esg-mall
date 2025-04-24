package org.example.merchant.controller;

import lombok.RequiredArgsConstructor;
import org.example.merchant.bean.cmd.MerchantQryCmd;
import org.example.merchant.bean.dto.MerchantDTO;
import org.example.merchant.bean.dto.UserInfoDTO;
import org.example.merchant.common.UserStatus;
import org.example.merchant.core.MerchantService;
import org.example.merchant.core.UserService;
import org.example.merchant.handler.BusinessException;
import org.example.merchant.util.JwtUtil;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class BaseController {

    private final HttpServletRequest request;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    private final MerchantService merchantService;



    public Long getMerchantId() {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserId(token);
        if (Objects.isNull(userId)) {
            throw new BusinessException("token无效");
        }

        UserInfoDTO userInfoDTO = userService.getUserInfo(userId).getData();
        if (Objects.isNull(userInfoDTO)) {
            throw new BusinessException("用户不存在");
        }

        if(userInfoDTO.getStatus().equals(UserStatus.DISABLE.getCode())){
            throw new BusinessException("用户已被禁用");
        }


        MerchantQryCmd merchantQryCmd = new MerchantQryCmd();
        merchantQryCmd.setUserId(userId);
        MerchantDTO merchantDTO = merchantService.get(merchantQryCmd).getData();

        return merchantDTO.getId();
    }

    public Long getUserId() {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.getUserId(token);
        if (Objects.isNull(userId)) {
            throw new BusinessException("token无效");
        }
        return userId;
    }




}
