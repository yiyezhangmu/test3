package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.util.List;

@Data
public class StoreOrderTopDTO {
    /**
     *组织id(找群)
     */
    private Long dingDeptId;
    /**
     * 组织名称(门店名称)
     */
    private String deptName;
    /**
     * 组织类型 STORE、COMP、HQ
     */
    private String deptType;
    /**
     * 大单门店列表
     */
    private List<TopStore> topStoreList;

    /**
     * 截至时间
     */
    private Long etlTm;

    @Data
    public static class TopStore{
        /**
         * 门店id
         */
        private Long storeId;

        /**
         * 门店名称
         */
        private String storeName;
        /**
         * 公司id
         */
        private Long compId;
        /**
         * 公司名称
         */
        private String compName;
        /**
         * 大单笔数
         */
        private Long orderCount;

        /**
         * 总大单数
         */
        private Long totalOrderCount;
    }




}
