package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/10 21:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskAgencyVO {
    /**
     * 子任务id
     */
    private Long subTaskId;
    /**
     * 父任务id
     */
    private Long unifyTaskId;
    /**
     * 流程引擎---提交key
     */
    private String flowActionKey;
    /**
     *流程引擎---节点
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
    /**
     *
     */
    private String storeId;
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
    /**
     * 任务处理者id
     */
    private String handleUserId;
    private Long handleTime;
    private String cid;
    private String bizCode;
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
     * 是否逾期
     */
    private Boolean expireFlag;
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
     * 判断用户有无权限处理流程
     */
    private  Boolean editFlag;
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
     * 分组标志
     */
    private String groupSign;
    /**
     * 子任务限制时长
     */
    private Double limitHour;
    /**
     * 相关人员
     */
    private Map<String, List<PersonDTO>> processUser;
    /**
     * 子任务编码
     */
    private String subTaskCode;
    /**
     * 统一审批内容
     */
    private String taskData;
    /**
     * 统一父任务审批内容
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

    /**
     * 检查项信息
     */
    private TbMetaStaTableColumnDO column;

    private Long loopCount;

    private Date firstHandlerTime;

    private Date firstApproveTime;

    private Date handlerEndTime;
}
