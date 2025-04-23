package org.example.user.bean.cmd;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateCmd {

    private String address;

    private List<OrderDetailCreateCmd> orderDetailList;
}
