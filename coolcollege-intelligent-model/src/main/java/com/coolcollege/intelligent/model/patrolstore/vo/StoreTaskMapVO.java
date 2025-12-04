package com.coolcollege.intelligent.model.patrolstore.vo;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : yezhe
 * @date ：2021/01/06 16:00
 */
@Data
public class StoreTaskMapVO {
    /**
     * 用户名
     */
    private String userId;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 巡店历史
     */
    private List<StoreTaskHistoryDTO> history;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreTaskHistoryDTO {
        /**
         * 经纬度
         */
        private String longitudeLatitude;
        /**
         * 巡店结束时间
         */
        private Date endTime;
        /**
         * 门店id
         */
        private String storeId;
        /**
         * 用户名称
         */
        private String storeName;
        /**
         * 得分
         */
        private Integer score;
        /**
         * 巡店方案名
         */
        private String templateName;
        /**
         * 钉钉corpId
         */
        private String corpId;
        /**
         * 巡店记录id
         */
        private Long recordId;
        /**
         * 模板id
         */
        private String templateId;
        /**
         * 任务详情id
         */
        private Long taskDetailId;

    }

}
