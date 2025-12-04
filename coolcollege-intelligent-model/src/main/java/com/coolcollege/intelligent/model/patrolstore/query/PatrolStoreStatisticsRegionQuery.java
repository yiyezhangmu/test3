package com.coolcollege.intelligent.model.patrolstore.query;

import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/22 14:08
 */
@Data
public class PatrolStoreStatisticsRegionQuery extends PatrolStoreStatisticsBaseQuery {

    /**
     * 区域id列表  选择区域树查询时使用
     */
    private List<String> regionIds;

    /**
     * 区域id  获取子区域统计时使用
     */
//    @NotBlank(message = "请选择区域后查询")
    private String regionId;

    /**
     * 人员id
     */
    private String userId;

    /**
     * 是否是获取子区域
     */
    private boolean getChild = false;

    /**
     * 门店Id
     */
    private List<String> storeIds;

    /**
     * 巡店方式
     */
    private String patrolType;

    CurrentUser user;

    private Boolean getDirectStore = false;
    /**
     * 检查表ID
     */
    private Long metaTableId;

    /**
     * 包含所有下级
     */
    private Boolean containAllChild = false;
}
