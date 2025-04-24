package org.example.merchant.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.merchant.bean.MultiResponse;
import org.example.merchant.bean.SingleResponse;
import org.example.merchant.bean.cmd.*;
import org.example.merchant.bean.dto.UserDTO;
import org.example.merchant.bean.dto.UserDetailDTO;
import org.example.merchant.bean.dto.UserInfoDTO;
import org.example.merchant.bean.dto.UserResetPasswordDTO;
import org.example.merchant.common.CommonConstant;
import org.example.merchant.common.UserStatus;
import org.example.merchant.core.UserService;
import org.example.merchant.entity.Merchant;
import org.example.merchant.entity.User;
import org.example.merchant.entity.mapper.MerchantMapper;
import org.example.merchant.entity.mapper.UserMapper;
import org.example.merchant.util.AESUtils;
import org.example.merchant.util.JwtUtil;
import org.example.merchant.util.PasswordGeneratorUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private  RedisTemplate<String, String> redisTemplate;


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

        if (user.getStatus().equals(UserStatus.DISABLE.getCode())){
            String redisKey = CommonConstant.TOKEN_KEY_PREFIX + user.getRole() + ":" + user.getId();
            redisTemplate.delete(redisKey);
        }


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

    @Override
    public MultiResponse<UserDetailDTO> page(UserPageQryCmd userPageQryCmd) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasLength(userPageQryCmd.getUsername()),User::getUsername,userPageQryCmd.getUsername());
        queryWrapper.like(StringUtils.hasLength(userPageQryCmd.getRealName()),User::getRealName,userPageQryCmd.getRealName());
        queryWrapper.like(StringUtils.hasLength(userPageQryCmd.getPhone()),User::getPhone,userPageQryCmd.getPhone());
        queryWrapper.eq(StringUtils.hasLength(userPageQryCmd.getRole()),User::getRole,userPageQryCmd.getRole());
        queryWrapper.eq(StringUtils.hasLength(userPageQryCmd.getStatus()),User::getStatus,userPageQryCmd.getStatus());

        if (StringUtils.hasLength(userPageQryCmd.getMerchantName()) || StringUtils.hasLength(userPageQryCmd.getContact())){
            LambdaQueryWrapper<Merchant> merchantLambdaQueryWrapper = new LambdaQueryWrapper<>();
            merchantLambdaQueryWrapper.like(StringUtils.hasLength(userPageQryCmd.getMerchantName()),Merchant::getName,userPageQryCmd.getMerchantName());
            merchantLambdaQueryWrapper.like(StringUtils.hasLength(userPageQryCmd.getContact()),Merchant::getContact,userPageQryCmd.getContact());

            List<Long> userIds = merchantMapper.selectList(merchantLambdaQueryWrapper).stream().map(Merchant::getUserId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(userIds)){
                return MultiResponse.buildSuccess();
            }

            queryWrapper.in(User::getId,userIds);
        }

        Page<User> users = userMapper.selectPage(Page.of(userPageQryCmd.getPageNum(),userPageQryCmd.getPageSize()),queryWrapper);
        if (CollectionUtils.isEmpty(users.getRecords())){
            return MultiResponse.buildSuccess();
        }

        List<Long> userIds = users.getRecords().stream().map(User::getId).collect(Collectors.toList());

        LambdaQueryWrapper<Merchant> merchantLambdaQueryWrapper = new LambdaQueryWrapper<>();
        merchantLambdaQueryWrapper.in(Merchant::getUserId,userIds);
        Map<Long, Merchant> merchantMap = merchantMapper.selectList(merchantLambdaQueryWrapper).stream().collect(Collectors.toMap(Merchant::getUserId, Function.identity()));

        List<UserDetailDTO> userDetailList = new ArrayList<>();

        for (User user:users.getRecords()){

            UserDetailDTO userDetailDTO = new UserDetailDTO();
            userDetailDTO.setId(user.getId());
            userDetailDTO.setUsername(user.getUsername());
            userDetailDTO.setPhone(user.getPhone());
            userDetailDTO.setRealName(user.getRealName());
            userDetailDTO.setStatus(user.getStatus());
            userDetailDTO.setRole(user.getRole());
            userDetailDTO.setLastLoginTime(user.getLastLoginTime());

            Merchant merchant = merchantMap.get(user.getId());
            if (Objects.nonNull(merchant)){
                userDetailDTO.setDescribe(merchant.getDescribe());
                userDetailDTO.setAddress(merchant.getAddress());
                userDetailDTO.setMerchantId(merchant.getId());
                userDetailDTO.setMerchantName(merchant.getName());
            }

            userDetailList.add(userDetailDTO);
        }


        return MultiResponse.of(userDetailList,(int)users.getTotal());
    }
}
