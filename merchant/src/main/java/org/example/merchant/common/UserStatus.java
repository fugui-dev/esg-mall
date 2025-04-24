package org.example.merchant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    ENABLE("enable", "启用"),

    DISABLE("disable", "禁用");

    private final String code;

    private final String desc;
} 