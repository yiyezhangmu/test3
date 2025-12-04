package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.store.dto.BasicsAreaDTO;
import com.coolcollege.intelligent.model.unifytask.dto.ProductInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskParentListVO {

    /**
     * ID
     */
    private Long id;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 是否逾期
     */
    private Boolean expireFlag = false;
    /**
     * 任务描述
     */
    private String taskDesc;
    /**
     * 开始时间
     */
    private Long beginTime;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 任务创建者
     */
    private String createUserId;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 任务创建时间
     */
    private Long createTime;
    /**
     * 对应定时任务id
     */
    private String scheduleId;
    /**
     * 对应流程模板id
     */
    private String templateId;
    /**
     * 父任务状态
     */
    private String parentStatus;
    /**
     * 门店
     */
    private List<BasicsAreaDTO> storeList;

    /**
     * 任务关联表单
     */
    private List<UnifyFormDataDTO> formData;
    /**
     * 总数量
     */
    private Long totalCount;

    /**
     * 完成任务数量
     */
    private Long completeCount;

    /**
     * 进行中数量
     */
    private Long ongoingCount;

    /**
     * 进行中已逾期数量
     */
    private Long ongoingCountOve;


    /**
     * 任务更新时间
     */
    private Long updateTime;
    /**
     * 处理人流程节点
     */
    private TaskProcessDTO handerProcess;
    /**
     * 编辑权限
     */
    private Boolean editFlag;
    /**
     * 任务周期 DAY MONTH YEAR
     */
    private String taskCycle;
    /**
     * 运行规则ONCE单次/LOOP循环
     */
    private String runRule;
    /**
     * 执行日期（周(1234567)，月(1~31)
     */
    private String runDate;
    /**
     * 任务状态
     */
    private Integer statusType;

    private List<String> collaboratorId;

    /**
     * 节点信息
     */
    @ApiModelProperty("商品类型")
    private ProductInfoDTO productInfoDTO;

    @ApiModelProperty("商品类型")
    private List<ProductInfoDTO> productInfoDTOList;
}
