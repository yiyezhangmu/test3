package com.coolcollege.intelligent.model.msg;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: MessageDealDTO
 * @Description:
 * @date 2022-08-17 10:01
 */
@Data
public class MessageDealDTO {

    public static final String QUESTION_TITLE = "您收到了新的工单，请尽快处理";

    public static final String QUESTION_CONTENT = "##### 工单名称：{taskName}\n" +
            "##### 创建人：{createUserName}\n" +
            "##### 创建时间：{createTime}\n";

    public static final String QW_DKF_QUESTION_CONTENT = "工单名称：{taskName}\n" +
            "创建人：{createUserName}\n" +
            "创建时间：{createTime}";

    public static final String QW_QUESTION_CONTENT = "工单名称：{taskName}\n" +
            "创建人：$userName={createUserName}$\n" +
            "创建时间：{createTime}";

    public static final String QUESTION_CC_CONTENT = "【{storeName}】的工单【{taskName}】已被【{handlerUserName}】处理，点击可查看详情。";

    public static final String QW_QUESTION_CC_CONTENT = "【{storeName}】的工单【{taskName}】已被【$userName={handlerUserName}$】处理，点击可查看详情。";

    public static final String QUESTION_CC_TITLE = "您收到了工单抄送，点击可查看详情";


    public static final String SUPERVISION_HANDLE_CONTENT = "任务名称：{taskName}\n" +
            "截止时间：$userName={createUserName}$\n" +
            "您有一个任务还未完成，点击前往执行。";

    public static final String SUPERVISION_HANDLE_TITLE = "待完成任务提醒";

    public static final String REASSIGN_TITLE = "重新分配任务提醒";

    public static final String APPROVE_TITLE = "待审批工作通知";


    private String title;

    private String content;

    public MessageDealDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
