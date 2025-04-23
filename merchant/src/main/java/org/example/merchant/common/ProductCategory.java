package org.example.merchant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductCategory {
    FOOD("food", "食品"),

    MENSWEAR("menswear", "男装"),

    SHOES_BAGS("shoes_bags", "鞋包"),

    DEPARTMENT_STORE("department_store", "百货"),

    COSMETICS("cosmetics", "美妆"),

    UNDERWEAR("underwear", "内衣"),

    MOBILE_PHONE("mobile_phone", "手机"),

    FRUITS("fruits", "水果"),

    MEDICINE("medicine", "医药"),

    HOME_TEXTILES("home_textiles", "家纺"),

    APPLIANCES("appliances", "电器"),

    WOMENSWEAR("womenswear", "女装"),

    SPORTS("sports", "运动"),

    FURNITURE("furniture", "家具"),

    CAR_ACCESSORIES("car_accessories", "车品"),

    JEWELRY("jewelry", "饰品"),

    HOME_DECORATION("home_decoration", "家装"),

    COMPUTER("computer", "电脑"),

    MATERNITY_BABY("maternity_baby", "母婴");

    private final String code;
    private final String desc;


}
