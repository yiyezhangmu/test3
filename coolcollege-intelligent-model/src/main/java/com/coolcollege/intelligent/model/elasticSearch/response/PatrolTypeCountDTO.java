package com.coolcollege.intelligent.model.elasticSearch.response;

import com.coolcollege.intelligent.model.elasticSearch.annotation.GroupKey;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolTypeCountDTO
 * @Description: 巡店类型->巡店数量
 * @date 2021-10-26 13:37
 */
@Data
public class PatrolTypeCountDTO {

    /**
     * 巡店类型 （线上巡店 线上巡店 定时巡检）
     */
    @GroupKey
    private String patrolType;

    /**
     * 巡店数量
     */
    private Integer patrolTypeNum;

}
