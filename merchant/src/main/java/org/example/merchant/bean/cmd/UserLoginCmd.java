package org.example.merchant.bean.cmd;

import lombok.Data;

@Data
public class UserLoginCmd {

    private String username;

    private String password;
}
