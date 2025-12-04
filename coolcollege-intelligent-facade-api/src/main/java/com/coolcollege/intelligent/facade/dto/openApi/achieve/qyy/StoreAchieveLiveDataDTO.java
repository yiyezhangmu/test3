package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreAchieveLiveData
 * @Description:门店业绩实时数据
 * @date 2023-03-30 15:19
 */
@Data
public class StoreAchieveLiveDataDTO {

    /**
     *更新List
     */
    private List<StoreAchieveLiveData> updateList;

    @Data
    public static class StoreAchieveLiveData{

        /**
         * 组织id
         */
        private String dingDeptId;

        /**
         * 组织名称
         */
        private String deptName;

        /**
         * 日期(yyyy-MM-dd)
         */
        private String salesDt;

        /**
         * 日数据
         */
        private StoreAchieveDTO dayData;

        /**
         * 周数据
         */
        private StoreAchieveDTO weekData;

        /**
         * 月数据
         */
        private StoreAchieveDTO monthData;

        /**
         * 奥康要求透过组织架构的compId(用来传不在钉钉组织架构内的分子公司)
         */
        private String compId;

    }

}
