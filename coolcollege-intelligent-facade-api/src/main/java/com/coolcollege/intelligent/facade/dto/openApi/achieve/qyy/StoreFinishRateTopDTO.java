package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreFinishRateTopDTO
 * @Description: 门店完成率top
 * @date 2023-03-30 15:40
 */
@Data
public class StoreFinishRateTopDTO {

    /**
     * 组织id
     */
    private String dingDeptId;

    /**
     * 门店完成率top排行
     */
    private List<StoreFinishRateTop> storeFinishRateTopList;

    @Data
    public static class StoreFinishRateTop{

        /**
         * 组织id
         */
        private Long dingDeptId;

        /**
         * 组织名称
         */
        private String deptName;

        /**
         * 完成率
         */
        private BigDecimal salesRate;

        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 商品款号
         */
        private String goodsId;

        /**
         * 商品销量
         */
        private Integer goodsSalesNum;

        /**
         * 商品年份
         */
        private String goodsYear;

        /**
         * 商品封面
         */
        private String goodsImage;

        /**
         * 商品链接
         */
        private String goodsUrl;
    }

}
