package org.example.merchant.bean.cmd;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class UserCreateCmd {

    private String username;

    private String password;

    private String realName;

    private String phone;

    /**
     * 角色
     */
    private String role;


}
