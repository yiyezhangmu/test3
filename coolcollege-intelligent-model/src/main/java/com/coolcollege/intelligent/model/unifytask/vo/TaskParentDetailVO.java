package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
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
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/24 22:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskParentDetailVO {

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
    private List<BasicsStoreDTO> storeList;

    /**
     * 用户输入门店范围
     */
    private List<BasicsStoreDTO> inputStoreScopeList;


    /**
     * 任务关联表单
     */
    private List<UnifyFormDataDTO> formData;
    /**
     * 任务更新者
     */
    private String updateUserId;
    /**
     * 更新人名称
     */
    private String updateUserName;
    /**
     * 任务更新时间
     */
    private Long updateTime;
    /**
     * 流程信息
     */
    private List<TaskProcessDTO> process;
    /**
     * 节点信息
     */
    private String nodeInfo;
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
     * 选区域模式
     */
    private Boolean regionModel;


    @ApiModelProperty("协作人id列表")
    private List<String> collaboratorIdList;


    @ApiModelProperty("协作人列表")
    private List<PersonDTO> collaboratorList;

    /**
     * 百丽货号
     */
    private String productNo;

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

}