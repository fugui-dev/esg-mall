package org.example.user.bean.cmd;

import lombok.Data;

@Data
public class OrderUpdateCmd {

    private String address;

    private Long id;

    private String status;
}
