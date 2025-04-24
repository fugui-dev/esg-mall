package org.example.merchant.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.MerchantQryCmd;
import org.example.merchant.bean.cmd.MerchantRegisterCmd;
import org.example.merchant.bean.cmd.MerchantUpdateCmd;
import org.example.merchant.bean.dto.MerchantDTO;
import org.example.merchant.bean.dto.MerchantRegisterDTO;
import org.example.merchant.common.MerchantStatus;
import org.example.merchant.core.MerchantService;
import org.example.merchant.entity.Config;
import org.example.merchant.entity.Merchant;
import org.example.merchant.entity.mapper.ConfigMapper;
import org.example.merchant.entity.mapper.MerchantMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class MerchantServiceImpl implements MerchantService {

    @Resource
    private MerchantMapper merchantMapper;

    @Override
    public SingleResponse<MerchantDTO> get(MerchantQryCmd merchantQryCmd) {

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getUserId,merchantQryCmd.getUserId());

        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        if (Objects.isNull(merchant)){
            return SingleResponse.buildFailure("请先完善商户信息");
        }

        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(merchant.getId());
        merchantDTO.setUserId(merchant.getUserId());
        merchantDTO.setAddress(merchant.getAddress());
        merchantDTO.setName(merchant.getName());
        merchantDTO.setDescribe(merchant.getDescribe());
        return SingleResponse.of(merchantDTO);
    }

    @Override
    public SingleResponse<MerchantRegisterDTO> register(MerchantRegisterCmd merchantRegisterCmd) {

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getUserId,merchantRegisterCmd.getUserId());

        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        if (Objects.isNull(merchant)){
            merchant = new Merchant();
            merchant.setUserId(merchantRegisterCmd.getUserId());
            merchant.setAddress(merchantRegisterCmd.getAddress());
            merchant.setName(merchantRegisterCmd.getName());
            merchant.setDescribe(merchantRegisterCmd.getDescribe());
            merchant.setContact(merchantRegisterCmd.getContact());
            merchantMapper.insert(merchant);
        }

        MerchantRegisterDTO merchantRegisterDTO = new MerchantRegisterDTO();
        BeanUtils.copyProperties(merchant, merchantRegisterDTO);

        return SingleResponse.of(merchantRegisterDTO);
    }

    @Override
    public SingleResponse update(MerchantUpdateCmd merchantUpdateCmd) {

        Merchant merchant = merchantMapper.selectById(merchantUpdateCmd.getId());
        if (Objects.isNull(merchant)){
            return SingleResponse.buildFailure("商户不存在");
        }
        if (merchant.getUserId() != merchantUpdateCmd.getUserId()){
            return SingleResponse.buildFailure("商户不属于当前用户");
        }

        merchant.setAddress(merchantUpdateCmd.getAddress());
        merchant.setName(merchantUpdateCmd.getName());
        merchant.setDescribe(merchantUpdateCmd.getDescribe());
        merchant.setContact(merchantUpdateCmd.getContact());

        merchantMapper.updateById(merchant);
        return SingleResponse.buildSuccess();
    }
}
