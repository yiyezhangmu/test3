package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolRecordAuthDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/28 14:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskSubVO {

    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 子任务id
     */
    private Long regionId;
    /**
     * 父任务id
     */
    private Long unifyTaskId;
    /**
     *
     */
    private String storeId;

    /**
     * 门店编码
     */
    private String storeNum;
    /**
     *
     */
    private String storeName;
    /**
     * 子任务状态
     */
    private String subStatus;
    /**
     *
     */
    private String createUserId;
    /**
     *
     */
    private String createUserName;

    private Long createTime;
    private Long handleTime;
    /**
     * 任务处理者id
     */
    private String handleUserId;
    /**
     *
     */
    private String handleUserName;
    /**
     *
     */
    private String remark;
    /**
     * 开始时间
     */
    private Long beginTime;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 任务描述
     */
    private String taskDesc;
    /**
     * 任务描述
     */
    private String taskName;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 流程引擎---提交key
     */
    private String flowActionKey;
    /**
     * 流程引擎---节点
     */
    private String flowNodeNo;
    /**
     * 流程引擎---实例id
     */
    private String flowInstanceId;
    /**
     * 流程引擎---模板id
     */
    private String flowTemplateId;
    /**
     * 流程引擎---循环次数
     */
    private Long flowCycleCount;
    private String cid;
    private String bizCode;
    /**
     * 是否逾期
     */
    private Boolean expireFlag;
    /**
     * 相关人员
     */
    private Map<String, List<PersonDTO>> processUser;
    /**
     * 一键提醒-相关人员
     */
    private Set<String> urgingUser;
    /**
     * 历史
     */
    private List<Object> history;
    /**
     * 父任务创建人
     */
    private String taskCreateUserId;
    /**
     * 父任务创建人
     */
    private String taskCreateUserName;
    /**
     * 节点信息
     */
    private String nodeInfo;
    /**
     * 当前节点审批方式
     */
    private String approveType;
    /**
     * 判断用户有无权限处理流程
     */
    private Boolean editFlag;
    /**
     * 转交权限
     */
    private Boolean turnFlag;
    /**
     * 陈列表数据
     */
    private List<UnifyFormDataDTO> formData;
    /**
     * 父任务创建时间
     */
    private Long parentCreateTime;
    /**
     * 业务循环次数
     */
    private Long groupItem;
    /**
     * 循环任务循环轮次
     */
    private Long loopCount;
    /**
     * 分组标志
     */
    private String groupSign;

    /**
     * 子任务编码(父任务id#门店id)
     */
    private String subTaskCode;

    /**
     * 子任务限制时长
     */
    private Double limitHour;
    /**
     * 统一审批数据
     */
    private String taskData;
    /**
     * 父任务统一审批信息
     */
    private String taskInfo;
    /**
     * 审批链任务开始时间
     */
    private Long subBeginTime;
    /**
     * 审批链任务结束时间
     */
    private Long subEndTime;

    private String attachUrl;

    private String runRule;
    /**
     * 检查项个数
     */
    private Long metaColumnCount;

    /**
     * 任务周期 DAY MONTH YEAR
     */
    private String taskCycle;


    /**
     * 权限
     */
    private PatrolRecordAuthDTO patrolRecordAuth;

    private Long parentTurnSubId;

    private String actionKey;

    /**
     * 处理截止时间
     */
    private Date handlerEndTime;

    /**
     * 是否逾期可执行
     */
    private Boolean overdueRun;

    private boolean isOperateOverdue;

    private boolean overdueTaskContinue;

    /**
     * 任务状态 1：未停止 0：已停止
     */
    private Integer statusType;

}
