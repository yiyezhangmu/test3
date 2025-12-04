package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/15 13:49
 * @Version 1.0
 */
@Data
public class CreateQuestionOrderDTO {

    /**
     * 父工单名称
     */
    private String parentQuestionName;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 门店id
     */
    private List<QuestionDetail> questionList;

    @Data
    public static class QuestionDetail{

        /**
         * 子工单名称
         */
        private String questionName;

        /**
         * 任务截止时间
         */
        private Date endTime;

        /**
         * 门店id
         */
        private String storeId;

        /**
         * 工单描述
         */
        private String taskDesc;

        /**
         * 流程信息
         */
        private List<ProcessInfoDTO> processList;

        /**
         * 任务信息
         */
        private TaskInfoDTO taskInfo;

    }

    @Data
    public static class ProcessInfoDTO{

        /**
         * nodeNo=1是整改节点，为2-4是审批节点,，代表几级审批，最多支持三级审批，nodeNo=cc为抄送
         */
        private String nodeNo;

        /**
         * 节点对应的人和角色
         */
        private List<ProcessUser> user;

    }

    @Data
    public static class TaskInfoDTO{

        /**
         * 检查项id
         */
        private Long metaColumnId;

        /**
         * 工单附件(图片)
         */
        private List<String> photos;
    }

    @Data
    public static class ProcessUser{

        /**
         * 职位:position,人:person
         */
        private String type;

        /**
         * type对应的id
         */
        private String value;

        /**
         * type对应的name
         */
        private String name;

    }
}
