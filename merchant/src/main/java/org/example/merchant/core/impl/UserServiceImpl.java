package org.example.merchant.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.UserDTO;
import org.example.merchant.bean.dto.UserInfoDTO;
import org.example.merchant.bean.dto.UserResetPasswordDTO;
import org.example.merchant.common.UserStatus;
import org.example.merchant.core.UserService;
import org.example.merchant.entity.Merchant;
import org.example.merchant.entity.User;
import org.example.merchant.entity.mapper.MerchantMapper;
import org.example.merchant.entity.mapper.UserMapper;
import org.example.merchant.util.AESUtils;
import org.example.merchant.util.JwtUtil;
import org.example.merchant.util.PasswordGeneratorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private JwtUtil jwtUtil;


    @Override
    public SingleResponse createUser(UserCreateCmd userCreateCmd) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userCreateCmd.getUsername());

        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return SingleResponse.buildFailure("用户名已存在");
        }

        String password;
        try {
            password = AESUtils.aesDecrypt(userCreateCmd.getPassword());
        } catch (Exception e) {
            return SingleResponse.buildFailure("密码解密失败");
        }
        User user = new User();
        user.setUsername(userCreateCmd.getUsername());
        user.setPassword(DigestUtils.sha1Hex(password));
        user.setStatus(UserStatus.ENABLE.getCode());
        user.setRealName(userCreateCmd.getRealName());
        user.setRole(userCreateCmd.getRole());
        user.setPhone(userCreateCmd.getPhone());

        userMapper.insert(user);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse updateUser(UserUpdateCmd userUpdateCmd) {

        User user = userMapper.selectById(userUpdateCmd.getId());
        if (user == null) {
            return SingleResponse.buildFailure("用户不存在");
        }

        user.setUsername(userUpdateCmd.getUsername());
        user.setRealName(userUpdateCmd.getRealName());
        user.setPhone(userUpdateCmd.getPhone());
        user.setStatus(userUpdateCmd.getStatus());
        user.setRole(userUpdateCmd.getRole());
        userMapper.updateById(user);

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse changePassword(UserChangePasswordCmd userChangePasswordCmd) {

        User user = userMapper.selectById(userChangePasswordCmd.getId());
        if (user == null) {
            return SingleResponse.buildFailure("用户不存在");
        }

        String oldPassword = null;
        try {
            oldPassword = AESUtils.aesDecrypt(userChangePasswordCmd.getOldPassword());
        } catch (Exception e) {

            return SingleResponse.buildFailure("旧密码解密失败");
        }

        if (!(DigestUtils.sha1Hex(oldPassword).equals(user.getPassword()))) {
            return SingleResponse.buildFailure("原密码错误");
        }

        String newPassword = null;
        try {
            newPassword = AESUtils.aesDecrypt(userChangePasswordCmd.getNewPassword());
        } catch (Exception e) {
            return SingleResponse.buildFailure("新密码解密失败");
        }

        user.setPassword(DigestUtils.sha1Hex(newPassword));

        userMapper.updateById(user);

        return SingleResponse.buildSuccess();

    }

    @Override
    public SingleResponse<UserDTO> login(UserLoginCmd userLoginCmd) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userLoginCmd.getUsername());

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return SingleResponse.buildFailure("用户不存在");
        }

        String password = null;
        try {
            password = AESUtils.aesDecrypt(userLoginCmd.getPassword());
        } catch (Exception e) {
            return SingleResponse.buildFailure("密码解密失败");
        }

        if (!(DigestUtils.sha1Hex(password).equals(userLoginCmd.getPassword()))) {
            return SingleResponse.buildFailure("密码错误");
        }

        if (Objects.equals(user.getStatus(), UserStatus.DISABLE.getCode())) {
            return SingleResponse.buildFailure("账号已禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setStatus(user.getStatus());
        userDTO.setRealName(user.getRealName());
        userDTO.setPhone(user.getPhone());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        userDTO.setToken(jwtUtil.generateToken(user));
        return SingleResponse.of(userDTO);
    }

    @Override
    public SingleResponse<UserInfoDTO> getUserInfo(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return SingleResponse.buildFailure("用户不存在");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setRealName(user.getRealName());
        userInfoDTO.setPhone(user.getPhone());
        userInfoDTO.setRole(user.getRole());
        userInfoDTO.setStatus(user.getStatus());
        userInfoDTO.setLastLoginTime(user.getLastLoginTime());

        return SingleResponse.of(userInfoDTO);

    }

    @Override
    public SingleResponse logout(String token) {
        jwtUtil.invalidateToken(token);
        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse<UserResetPasswordDTO> resetPassword(UserResetPasswordCmd userResetPasswordCmd) {

        User user = userMapper.selectById(userResetPasswordCmd.getId());
        if (user == null) {
            return SingleResponse.buildFailure("用户不存在");
        }

        String password = PasswordGeneratorUtil.generateRandomPassword(10);
        user.setPassword(DigestUtils.sha1Hex(password));

        userMapper.updateById(user);

        UserResetPasswordDTO userResetPasswordDTO = new UserResetPasswordDTO();
        userResetPasswordDTO.setNewPassword(password);

        return SingleResponse.of(userResetPasswordDTO);
    }
}
