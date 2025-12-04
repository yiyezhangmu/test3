package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BestSellerDTO {
    /**
     * 组织id 找群
     */
    private String dingDeptId;

    /**
     * 主推款标题
     */
    private String title;

    /**
     * 畅销品列表
     */
    private List<BestSellerSub> bestSellerSubList;

    /**
     * 提交时间
     */
    private String time;


    @Data
    public static class BestSellerSub{

        /**
         * 简单卡片-核心卖点
         */
        private String corePoint;

        /**
         * 简单卡片-职业场合
         */
        private String proOccasion;

        /**
         * 休闲场合
         */
        private String leisureOccasion;

        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 商品id/款号
         */
        private String goodsNo;

        /**
         * 商品封面
         */
        private String goodsImage;

        /**
         * 销量
         */
        private Integer salesNum;

        /**
         * 库存
         */
        private Integer inventoryNum;

        /**
         * 类型
         * ws:女鞋
         * ms:男鞋
         * bg:箱包
         */
        private String tag;


        /**
         * 季节
         */
        private String goodsSeason;

        /**
         * 平均单价
         */
        private BigDecimal averagePrice;

        /**
         * 动销
         */
        private BigDecimal shelfsalesratio;
    }
}
