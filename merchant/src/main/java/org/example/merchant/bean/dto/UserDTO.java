package org.example.merchant.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private String username;

    private String realName;

    private String phone;

    private String status;

    /**
     * 角色
     */
    private String role;

    private LocalDateTime lastLoginTime;

    /**
     * token
     */
    private String token;
}
