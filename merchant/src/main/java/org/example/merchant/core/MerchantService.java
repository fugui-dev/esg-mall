package org.example.merchant.core;

import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.MerchantQryCmd;
import org.example.merchant.bean.cmd.MerchantRegisterCmd;
import org.example.merchant.bean.cmd.MerchantUpdateCmd;
import org.example.merchant.bean.dto.MerchantDTO;
import org.example.merchant.bean.dto.MerchantRegisterDTO;

public interface MerchantService {

    SingleResponse<MerchantDTO> get(MerchantQryCmd merchantQryCmd);


    SingleResponse<MerchantRegisterDTO> register(MerchantRegisterCmd merchantRegisterCmd);


    SingleResponse update(MerchantUpdateCmd merchantUpdateCmd);
}
