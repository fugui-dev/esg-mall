package org.example.user.bean.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDTO {

    private Long id;

    private String name;
    /**
     * 产品图片
     */
    private String imageUrl;


    private String describe;
    /**
     * 分类
     */
    private String classify;
    /**
     * 状态
     */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
