package com.coolcollege.intelligent.model.store.dto;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author byd
 */
@ApiModel
@Data
public class StoreOpenRuleBuildDTO {

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private Long beginTime;
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private Long endTime;
    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    private String taskName;


    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("规则类型: TB_DISPLAY_TASK：新陈列任务， PATROL_STORE_ONLINE:视频巡店, PATROL_STORE_OFFLINE:线下巡店")
    private String ruleType;

    @ApiModelProperty("门店规则开业日期天数")
    private Integer openDateDay;

    /**
     * 任务描述
     */
    @ApiModelProperty("任务描述")
    private String taskDesc;

    /**
     * 关联检查表id
     */
    @ApiModelProperty("关联检查表")
    private List<GeneralDTO> form;

    /**
     * 节点信息
     */
    @NotEmpty(message = "流程相关信息不能为空")
    private List<TaskProcessDTO> process;


    @ApiModelProperty("定时执行日期例周一到周五执行“1,2,3,4,5”例每月1号17号执行“1,17”")
    private String runDate;

    @ApiModelProperty("定时任务执行时间例12:00")
    private String calendarTime;

    @ApiModelProperty("子任务有效时间，例5个半小时,5.5")
    private Double limitHour;

    @ApiModelProperty("附件地址")
    private String attachUrl;

    @ApiModelProperty("非表单类任务传递内容例门店信息补全任务“store,address....”")
    private String taskInfo;

    @ApiModelProperty("协作人id列表")
    private List<String> collaboratorIdList;

    @ApiModelProperty("规则id, 新增不需要传")
    private Long ruleId;

    @ApiModelProperty("json对象(输入门店范围)")
    private String extraParam;

    @ApiModelProperty("规则状态: 0:已开启 1:已停用")
    private Integer status;

    @ApiModelProperty("协作人列表，返参")
    private List<PersonDTO> collaboratorUserList;

    @ApiModelProperty("任务范围（传的是regionId）")
    private List<String> taskScope;

    private List<RegionDO> regionList;

}
