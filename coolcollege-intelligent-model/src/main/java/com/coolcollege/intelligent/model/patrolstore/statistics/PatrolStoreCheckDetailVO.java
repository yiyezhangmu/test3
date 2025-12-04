package com.coolcollege.intelligent.model.patrolstore.statistics;

import com.coolcollege.intelligent.model.storework.vo.HandlerUserVO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskProcessVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author jeffrey
 * @date 2020/12/10
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreCheckDetailVO {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long taskId;

    @ApiModelProperty("任务类型:PATROL_STORE_OFFLINE,PATROL_STORE_ONLINE")
    private String patrolType;

    @ApiModelProperty("稽核记录id")
    private Long businessId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径(新)")
    private String regionWay;

    @ApiModelProperty("任务描述")
    private String taskDesc;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("巡店人工号")
    private String supervisorJobNum;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("巡店开始时间")
    private Date signStartTime;

    @ApiModelProperty("巡店结束时间")
    private Date signEndTime;

    @ApiModelProperty("大区稽核状态 0: 待稽核 1:已稽核")
    private Integer bigRegionCheckStatus;

    @ApiModelProperty("战区稽核状态 0: 待稽核 1:已稽核")
    private Integer warZoneCheckStatus;

    @ApiModelProperty("大区稽核人id")
    private String bigRegionUserId;

    @ApiModelProperty("大区稽核人姓名")
    private String bigRegionUserName;

    @ApiModelProperty("大区稽核人工号")
    private String bigRegionUserJobNum;

    @ApiModelProperty("大区稽核时间")
    private Date bigRegionCheckTime;

    @ApiModelProperty("战区稽核人id")
    private String warZoneUserId;

    @ApiModelProperty("战区稽核人姓名")
    private String warZoneUserName;

    @ApiModelProperty("战区稽核人工号")
    private String warZoneUserJobNum;

    @ApiModelProperty("战区稽核时间")
    private Date warZoneCheckTime;

    @ApiModelProperty("多个检查表的ID")
    private String metaTableIds;

    @ApiModelProperty("检查表的名称")
    private String metaTableName;

    @ApiModelProperty("删除标记")
    private Byte deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("创建者姓名")
    private String createUserName;

    @ApiModelProperty("循环任务的循环批次")
    private Long loopCount;

    @ApiModelProperty("子任务审批链开始时间")
    private Date subBeginTime;

    @ApiModelProperty("子任务审批链结束时间")
    private Date subEndTime;

    @ApiModelProperty("复审的巡店记录id")
    private Long recheckBusinessId;

    @ApiModelProperty("运行规则")
    private String runRule;

    @ApiModelProperty("任务周期")
    private String taskCycle;

    @ApiModelProperty("总项数")
    private Integer totalCalColumnNum;

    private List<UnifyPersonDTO> handerUserList;

    @ApiModelProperty("抄送人")
    private List<UnifyPersonDTO> ccUserList;

    @ApiModelProperty("审核人")
    private List<UnifyPersonDTO> aduitUserList;

    @ApiModelProperty("指定巡店人/审批人/抄送人范围")
    private TaskProcessVO assignPeopleRang;

    @ApiModelProperty("巡店人信息")
    private HandlerUserVO handlerUserVO;


}
