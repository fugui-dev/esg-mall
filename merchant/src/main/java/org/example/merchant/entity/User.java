package org.example.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity{

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String status;

    /**
     * 角色
     */
    private String role;

    private LocalDateTime lastLoginTime;
}
