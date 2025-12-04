package com.coolcollege.intelligent.facade.dto.newbelle;

import lombok.Data;

@Data
public class TaskGoodsDetailDTO {
    /**
     * 货号
     */
    private String ArticleNo;

    /**
     * 图片
     */
    private String pictureUrl;

    /**
     * 货品简介
     */
    private String productProfile;

    /**
     * 颜色
     */
    private String color;

    /**
     * 风格
     */
    private String style;

    /**
     * 归属品牌
     */
    private String parentBrand;
}
