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
    @Resource
    private ConfigMapper configMapper;

    @Override
    public SingleResponse<MerchantDTO> get(MerchantQryCmd merchantQryCmd) {

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getAddress,merchantQryCmd.getAddress());

        MerchantDTO merchantDTO = new MerchantDTO();

        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        if (Objects.isNull(merchant)){
            merchantDTO.setRegister(Boolean.FALSE);
            return SingleResponse.of(merchantDTO);
        }

        merchantDTO.setRegister(Boolean.TRUE);
        merchantDTO.setStatus(merchant.getStatus());
        merchantDTO.setName(merchant.getName());
        merchantDTO.setDescribe(merchant.getDescribe());
        return SingleResponse.of(merchantDTO);
    }

    @Override
    public SingleResponse<MerchantRegisterDTO> register(MerchantRegisterCmd merchantRegisterCmd) {

        LambdaQueryWrapper<Config> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(Config::getKey,"ENABLE_REGISTER_EXAMINE");

        Config config = configMapper.selectOne(configLambdaQueryWrapper);


        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getAddress,merchantRegisterCmd.getAddress());

        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        if (Objects.isNull(merchant)){
            merchant = new Merchant();
            merchant.setAddress(merchantRegisterCmd.getAddress());
            if (Objects.nonNull(config) && Integer.parseInt(config.getValue()) == 1){
                merchant.setStatus(MerchantStatus.IN_REVIEW.getCode());
            }else {
                merchant.setStatus(MerchantStatus.ENABLE.getCode());
            }
            merchant.setName(merchantRegisterCmd.getName());
            merchant.setDescribe(merchantRegisterCmd.getDescribe());
            merchantMapper.insert(merchant);
        }

        MerchantRegisterDTO merchantRegisterDTO = new MerchantRegisterDTO();
        merchantRegisterDTO.setRegister(Boolean.TRUE);
        merchantRegisterDTO.setStatus(merchant.getStatus());

        return SingleResponse.of(merchantRegisterDTO);
    }

    @Override
    public SingleResponse update(MerchantUpdateCmd merchantUpdateCmd) {

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getAddress,merchantUpdateCmd.getAddress());

        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        merchant.setDescribe(merchantUpdateCmd.getDescribe());
        merchant.setName(merchantUpdateCmd.getName());

        merchantMapper.updateById(merchant);
        return SingleResponse.buildSuccess();
    }
}
