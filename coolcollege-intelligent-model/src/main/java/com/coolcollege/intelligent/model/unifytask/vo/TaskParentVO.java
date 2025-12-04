package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.ProductInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/27 21:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskParentVO {

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
    private List<BasicsStoreDTO> storeList;
    /**
     * 处理人
     */
    private List<PersonDTO> handlePerson;
    /**
     * 审核人
     */
    private List<PersonDTO> approvalPerson;


    /**
     * 处理人new
     */
    private List<GeneralDTO> handlePersonPosition;
    /**
     * 审核人new
     */
    private List<GeneralDTO> approvalPersonPosition;

    /**
     * 任务关联表单
     */
    private List<UnifyFormDataDTO> formData;
    /**
     * 所有数量
     */
    private Integer allCount;
    /**
     * 完成数量
     */
    private Integer endCount;
    /**
     * 催办人员集合
     */
    private Set<String> urgingUser;
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
     * 检查项个数
     */
    private Long metaColumnCount;


    private String storeRange;

    private String handleUserName;

    private String approveUserName;

    private String recheckUserName;

    private String ccUserName;

    /**
     * 门店范围
     */
    private List<GeneralDTO> taskStoreRange;

    @ApiModelProperty("协作人列表")
    private List<PersonDTO> collaboratorList;

    private Integer statusType;

    /**
     * 节点信息
     */
    @ApiModelProperty("商品类型")
    private ProductInfoDTO productInfoDTO;

}
