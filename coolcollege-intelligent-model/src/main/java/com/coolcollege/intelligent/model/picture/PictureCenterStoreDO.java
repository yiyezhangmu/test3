package com.coolcollege.intelligent.model.picture;

import lombok.Data;

/**
 * @Description: 门店图片
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Data
public class PictureCenterStoreDO {
    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门头照
     */
    private String avatar;
}
