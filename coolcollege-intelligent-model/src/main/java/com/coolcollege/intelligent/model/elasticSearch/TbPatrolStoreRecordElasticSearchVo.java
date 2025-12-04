package com.coolcollege.intelligent.model.elasticSearch;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/8/12 10:17
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStoreRecordElasticSearchVo {
    /**
     * 自增id
     */
    private Long id;

    /**
     * 父任务id
     */
    private Long taskId;

    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店经纬度
     */
    private String storeLongitudeLatitude;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域路径
     */
    private String regionWay;

    /**
     * 巡店人id
     */
    private String supervisorId;

    /**
     * 巡店人姓名
     */
    private String supervisorName;

    /**
     * 巡店开始时间
     */
    private Date signStartTime;

    /**
     * 巡店结束时间
     */
    private Date signEndTime;

    /**
     * 巡店开始地址
     */
    private String signStartAddress;

    /**
     * 巡店结束地址
     */
    private String signEndAddress;

    /**
     * 巡店开始定位经纬度
     */
    private String startLongitudeLatitude;

    /**
     * 巡店结束定位经纬度
     */
    private String endLongitudeLatitude;

    /**
     * 签到状态 1正常 2异常
     */
    private Integer signInStatus;

    /**
     * 签退状态 1正常 2异常
     */
    private Integer signOutStatus;

    /**
     * 巡店时长：毫秒
     */
    private Long tourTime;

    /**
     * 状态：default,submitted,saved,draft
     */
    private String taskStatus;

    /**
     * 巡店记录状态
     */
    private Integer status;

    /**
     * 巡店类型:offline,online,information,ai
     */
    private String patrolType;

    /**
     * 巡店检查表类型 DEFINE(自定义) STANDARD(标准检查表)
     */
    private String tableType;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 循环任务循环轮次
     */
    private Long loopCount;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 子任务审批链开始时间
     */
    private Date subBeginTime;

    /**
     * 子任务审批链结束时间
     */
    private Date subEndTime;


    private Long score;

    private Integer failNum;

    private Integer passNum;

    private Integer inapplicableNum;

    private Integer  openSummary;

    private String summary;

    private String summaryPicture;

    private Integer openSignature;

    private String supervisorSignature;

    private Integer openSubmitFirst;

    private Long dataTableId;

    private String businessType;

    private static final long serialVersionUID = 1L;

    private Date editTime;

    private String tableName;

    private  String description;

    private Integer supportScore;

    private  Integer submitStatus;

    private  Integer businessStatus;

    private String auditUserId;

    private Date auditTime;

    private String auditPicture;

    private String auditOpinion;

    private String auditUserName;

    private String auditRemark;

    private String metaTableIds;

    /**
     * 巡店检查类型  巡店检查: PATROL_STORE 巡店复审 PATROL_RECHECK
     */
    private String businessCheckType;

}
