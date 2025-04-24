package org.example.merchant.bean.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDetailDTO {
    
    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String status;
    
    private Long merchantId;
    /**
     * 角色
     */
    private String role;

    private LocalDateTime lastLoginTime;

    /**
     * 经营地址
     */
    private String address;
    /**
     * 商户名称
     */
    private String merchantName;
    
    
    private String describe;
    /**
     * 联系方式
     */
    private String contact;
}
