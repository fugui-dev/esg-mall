package org.example.merchant.core;

import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.UserDTO;
import org.example.merchant.bean.dto.UserDetailDTO;
import org.example.merchant.bean.dto.UserInfoDTO;
import org.example.merchant.bean.dto.UserResetPasswordDTO;

public interface UserService {

    SingleResponse createUser(UserCreateCmd userCreateCmd);

    SingleResponse updateUser(UserUpdateCmd userUpdateCmd);

    SingleResponse changePassword(UserChangePasswordCmd userChangePasswordCmd);

    SingleResponse<UserDTO> login(UserLoginCmd userLoginCmd);

    SingleResponse<UserInfoDTO> getUserInfo(Long id);

    SingleResponse logout(String token);

    SingleResponse<UserResetPasswordDTO> resetPassword(UserResetPasswordCmd userResetPasswordCmd);

    MultiResponse<UserDetailDTO> page(UserPageQryCmd userPageQryCmd);



}
