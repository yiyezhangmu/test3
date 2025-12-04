package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/18 15:07
 */
@ApiModel
@Data
public class UnifyTaskBuildDTO {

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
    /**
     * 任务描述
     */
    private String taskDesc;
    /**
     * 任务类型
     * 陈列：DISPLAY_TASK
     */
    @NotBlank(message = "任务类型不能为空, 新增 新品上架:ACHIEVEMENT_NEW_RELEASE" +
            "   老品下架:ACHIEVEMENT_OLD_PRODUCTS_OFF")
    private String taskType;
    /**
     * 关联检查表id
     */
    private List<GeneralDTO> form;
    /**
     * 门店id
     * type store:门店，region区域,group分组
     */
    private List<GeneralDTO> storeIds;

    private List<GeneralDTO> taskDisplayStoreScopeList;

    private Boolean regionModel;

    /**
     * 节点信息
     */
    @NotEmpty(message = "流程相关信息不能为空")
    private List<TaskProcessDTO> process;

    /**
     * 任务模式
     * @see com.coolcollege.intelligent.model.enums.UnifyTaskPatternEnum
     * null：审批任务
     */
    private String taskPattern;
    /**
     * 运行规则
     * @see com.coolcollege.intelligent.model.enums.TaskRunRuleEnum
     * 单次/循环
     */
    private String runRule ;
    /**
     * 任务循环方式
     * @see com.coolcollege.intelligent.model.enums.UnifyTaskLoopDateEnum
     * 年月
     */
    private String taskCycle;
    /**
     * 执行日期
     */
    private String runDate;
    /**
     * 定时任务执行时间,例12:00
     */
    private String calendarTime;
    /**
     * 非表单类任务传递内容
     * 例门店信息补全任务
     * “store,address....”
     */
    private String taskInfo;
    /**
     * 子任务限制时间
     */
    private Double limitHour;

    /**
     * 附件地址
     */
    private String attachUrl;

    /**
     * 父任务id
     */
    private Long taskId;

    @ApiModelProperty("协作人id列表")
    private List<String> collaboratorIdList;

    /**
     * 门店id
     * type store:门店，region区域,group分组
     */
    @ApiModelProperty("新增门店指派范围")
    private List<GeneralDTO> addStoreList;

    /**
     * 节点信息
     */
    @ApiModelProperty("新增人员指派范围")
    private List<TaskProcessDTO> addProcessList;

    private String enterpriseId;


    private Long ruleId;

    private String userName;

    private String userId;

    /**
     * 百丽商品货号（只有百丽货品反馈需要传）
     */
    private String productNo;

    /**
     * 是否逾期可执行；1:可执行 0:不可执行
     */
    private int isOperateOverdue;

    private boolean overdueTaskContinue;

    /**
     * 节点信息
     */
    @ApiModelProperty("商品类型")
    private ProductInfoDTO productInfoDTO;


    @ApiModelProperty("商品类型")
    private List<ProductInfoDTO> productInfoDTOList;


    /**
     * 是否进行AI审核 : 0 不需要  1 需要'
     */
    private Boolean aiAudit;

//    /**
//     * 是否逾期可执行；1:可执行 0:不可执行
//     */
//    private int isOperateOverdue;
}
