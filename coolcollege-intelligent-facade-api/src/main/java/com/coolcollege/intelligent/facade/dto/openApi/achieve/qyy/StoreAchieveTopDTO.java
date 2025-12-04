package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreAchieveTop
 * @Description:门店业绩排行top3(实时消息)
 * @date 2023-03-30 9:44
 */
@Data
public class StoreAchieveTopDTO {

    /**
     * 组织id 找群
     */
    private String dingDeptId;

    /**
     * 门店top排行
     */
    private List<StoreAchieveTop> storeSalesTopList;

    @Data
    public static class StoreAchieveTop{

        /**
         * 组织id(门店id)
         */
        private Long dingDeptId;

        /**
         * 组织名称(门店名称)
         */
        private String deptName;

        /**
         * 总销售额
         */
        private BigDecimal salesAmt;

        /**
         * 毛利率
         */
        private BigDecimal profitRate;

        /**
         * 客单价
         */
        private BigDecimal cusPrice;

        /**
         * 连带率
         */
        private BigDecimal jointRate;

        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 商品款号
         */
        private String goodsId;

        /**
         * 商品年份
         */
        private String goodsYear;

        /**
         * 商品销量
         */
        private Integer goodsSalesNum;

        /**
         * 销售额
         */
        private BigDecimal goodsSalesAmt;

        /**
         * 库存
         */
        private Integer inventoryNum;

        /**
         * 商品封面
         */
        private String goodsImage;

        /**
         * 商品链接
         */
        private String goodsUrl;

        /**
         * 季节
         */
        private String goodsSeason;
    }

}
