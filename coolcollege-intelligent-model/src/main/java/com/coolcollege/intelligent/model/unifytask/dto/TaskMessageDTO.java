package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务消息统一封装类
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/10 11:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskMessageDTO {
    /**
     * 操作类型 ADD UPDATE DELETE
     */
    private String operate;
    /**
     * 任务id
     */
    private Long unifyTaskId;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * 操作人id
     */
    private String createUserId;
    /**
     * 操作时间
     */
    private Long createTime;
    /**
     * 数据
     */
    private String data;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 任务信息
     */
    private String taskInfo;

    /**
     * 附件地址
     */
    private String attachUrl;

    /**
     * 任务处理数据
     */
    private String taskHandleData;

    /**
     * 最新任务节点
     */
    private String nodeNo;

    /**
     * 巡店设置
     */
    private EnterpriseStoreCheckSettingDO storeCheckSetting;

    /**
     * 工单定义项id
     */
    private Long taskParentItemId;

    /**
     * 轮次
     */
    private Long loopCount;

    /**
     * 工单记录id
     */
    private Long questionRecordId;

    private String storeId;

    /**
     * 工单类型
     */
    private String questionType;
    /**
     * 备注：转交原因
     */
    private String content;

    public TaskMessageDTO(String operate, Long unifyTaskId, String taskType,
                          String createUserId, Long createTime, String data,
                          String enterpriseId, String taskInfo, String attachUrl){
        this.operate = operate;
        this.unifyTaskId = unifyTaskId;
        this.taskType = taskType;
        this.createUserId = createUserId;
        this.createTime = createTime;
        this.data = data;
        this.enterpriseId = enterpriseId;
        this.taskInfo = taskInfo;
        this.attachUrl = attachUrl;

    }
}
