package org.example.user.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {

    IN_REVIEW("in-review", "审核中"),

    UP("up", "上架"),

    DOWN("down", "下架");

    private final String code;

    private final String desc;
}
