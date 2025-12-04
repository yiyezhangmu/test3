package com.coolcollege.intelligent.model.patrolstore.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检查项基础详情
 * 
 * @author 叶哲
 * @date 2020/12/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsDataTableQuery extends PatrolStoreStatisticsBaseQuery {
    /**
     * 记录id
     */
    private Long businessId;
    /**
     * 检查表  废弃 废弃 废弃  统一用metaTableIds
     */
    private Long metaTableId;

    /**
     * 检查表
     */
    private List<Long> metaTableIds;

    private List<Long> metaColumnIds;
    /**
     * 所属门店
     */
    private String storeId;

    /**
     * 所属门店
     */
    private List<String> storeIdList;
    /**
     * 巡店状态
     */
    private Integer status;
    /**
     * 巡店人
     */
    private String supervisorId;

    /**
     * 父任务id
     */
    private Long taskId;

    private Long loopCount;

    private Boolean levelInfo;

    private Long regionId;

    /**
     * 所属区域
     */
    private List<String> regionIdList;

    /**
     * 所属区域
     */
    private List<String> regionPathList;


    private String regionPath;

    /**
     * 1 巡店记录列表  2 任务数据列表
     */
    private Integer type;

    /**
     * 检查结果项
     */
    private String checkResult;

    private Boolean isDefine;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 是否完成
     */
    private Boolean isComplete;

    /**
     * 任务类型 线上巡店，线下巡店，定时巡检
     */
    private String patrolType;

    /**
     * 是否为自准巡店
     */
    private Boolean autonomousPatrol;

    /**
     * 巡店人id列表
     */
    private List<String> userIdList;

    @ApiModelProperty("复审人id列表")
    private List<String> recheckIdList;

    @ApiModelProperty("复审类型 0:可复审 1:已复审")
    private Integer recheckStatus;

    @ApiModelProperty(value = "复巡店检查: PATROL_STORE 巡店复审 PATROL_RECHECK", hidden = true)
    private String businessCheckType;

    /**
     * 巡店类型列表
     */
    private List<String> patrolTypeList;

    @ApiModelProperty("签退开始时间")
    private Date signOutBeginDate;
    @ApiModelProperty("签退结束时间")
    private Date signOutEndDate;

    @ApiModelProperty("巡店人集合")
    private List<String> supervisorIds;

    @ApiModelProperty("true (不使用创建时间为30天限制)  = false（使用）")
    private Boolean createTimeIsNull=false;

    @ApiModelProperty("签到开始时间")
    private Date signInBeginDate;
    @ApiModelProperty("签到结束时间")
    private Date signInEndDate;

    public List<Long> getMetaTableIds() {
        if(metaTableId != null){
            if(CollectionUtils.isEmpty(metaTableIds)){
                metaTableIds = new ArrayList<>();
                metaTableIds.add(metaTableId);
            }else{
                metaTableIds.add(metaTableId);
            }
            return metaTableIds.stream().distinct().collect(Collectors.toList());
        }
        return metaTableIds;
    }
}
