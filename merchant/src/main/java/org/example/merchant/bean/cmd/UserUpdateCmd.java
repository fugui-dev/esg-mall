package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class UserUpdateCmd {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String status;

    /**
     * 角色
     */
    private String role;

}
