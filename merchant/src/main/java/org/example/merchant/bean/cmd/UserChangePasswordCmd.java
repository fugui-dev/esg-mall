package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class UserChangePasswordCmd {

    private Long id;

    private String oldPassword;

    private String newPassword;
}
