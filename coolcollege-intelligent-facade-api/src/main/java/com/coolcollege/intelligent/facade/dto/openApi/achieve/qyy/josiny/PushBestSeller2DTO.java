package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PushBestSeller2DTO {

    private String dingDeptId;

    private Date updateTime;

    private List<DataListSub> dataList;

    @Data
    public static class DataListSub{

        //季节+鞋类
        private String seasonAndFootWear;
        //销量
        private BigDecimal salesVolume;
        //销量星级
        private Integer salesVolumeStar;
        //动销
        private BigDecimal movablePin;
        //动销星级
        private Integer movablePinStar;
        //颜色
        private String color;
        //单价
        private BigDecimal price;
        //库存
        private Integer inventory;
        //铺货
        private Integer distribution;
        //货号
        private String goodsNo;
        //货品图片
        private String goodsPic;
        /**
         * 类型
         * ws:女鞋
         * ms:男鞋
         * bg:箱包
         */
        private String tag;
    }
}
