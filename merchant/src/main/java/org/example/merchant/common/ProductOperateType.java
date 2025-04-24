package org.example.merchant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductOperateType {

    EXAMINE("examine", "审核"),

    DOWN("down", "下架");

    private final String code;

    private final String desc;
}
