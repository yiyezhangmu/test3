package com.coolcollege.intelligent.model.question.dto;

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
 * @date ：Created in 2020/12/22 16:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReportDTO {
    /**
     * 工单父任务id
     */
    private Long unifyTaskId;
    /**
     * 问题图片
     */
    private List<String> questionPhoto;
    /**
     * 整改图片
     */
    private List<String> handlePhoto;
    /**
     * 整改完成时间
     */
    private Long completeTime;
    /**
     * 是否过期解决
     */
    private String overdueCompleteFlag;
    /**
     * 状态
     */
    private String status;
    /**
     * 任务描述（备注）
     */
    private String taskDesc;
    /**
     * 检查人
     */
    private String createUserId;
    private String createUserName;
    /**
     * 整改人
     */
    private String handleUserId;
    private String handleUserName;
    /**
     * 复检人
     */
    private String recheckUserId;
    private String recheckUserName;
}
