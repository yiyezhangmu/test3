package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wxp
 * @FileName: RecommendStyleDTO
 * @Description:主推款(实时消息)
 * @date 2023-06-30 9:44
 */
@Data
public class RecommendStyleDTO {

    /**
     * 组织id 找群
     */
    private String dingDeptId;

    /**
     * 主推款标题
     */
    private String title;

    /**
     * 主推款列表
     */
    private List<RecommendStyle> recommendStyleList;

    @Data
    public static class RecommendStyle{
        /**
         * 商品id/款号
         */
        private String goodsId;
        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 商品封面
         */
        private String goodsImage;

        /**
         * 当日销量
         */
        private Integer daySalesNum;

        /**
         * 单月累计销量
         */
        private Integer monthSalesNum;

        /**
         * 库存
         */
        private Integer inventoryNum;

        /**
         * 鞋底
         */
        private String bmaterial;
        /**
         * 鞋面
         */
        private String umaterial;

        /**
         * 季节
         */
        private String goodsSeason;

        /**
         * 铺货率
         */
        private BigDecimal distribution;

        /**
         * 业绩贡献
         */
        private BigDecimal achieveContribution;

        /**
         * 动销率
         */
        private BigDecimal shelfsalesratio;

        /**
         * 库存深度支持
         */
        private Integer deepInventoryNum;
    }

}
