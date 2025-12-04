package com.coolcollege.intelligent.common.enums.safetycheck;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 食安稽核通知枚举
 * @author wxp
 */
public enum FoodCheckNoticeEnum {
    REFUSE("1", "稽核报告审批拒绝通知", "您有一份【{storeName}】稽核报告被审批拒绝，请点击查看并修改巡检结果。"),
    SIGNATURE("2", "稽核报告签字通知", "您有一份稽核报告等待您签字确认"),
    BIGSTOREMANAGERAPPROVE("3", "稽核报告审批通知", "您有一份【{storeName}】稽核报告等待您审批，请及时处理。"),
    LEADERAPPROVE("4", "稽核报告审批通知", "您有一份【{storeName}】稽核报告等待您审批，请及时处理。"),
    AFTERHANDLECCINFO("afterHandleCcInfo", "现场稽核抄送通知", "【{storeName}】现场稽核报告抄送给您，请查收。"),
    AFTERAPPROVECCINFO("afterApproveCcInfo", "稽核报告完成通知", "您有一份【{storeName}】稽核报告，请查收。"),
    APPEALRESULTCCINFO("appealResultCcInfo", "申诉结果抄送通知", "【{storeName}】的申诉结果抄送给您，请查收。"),
    APPEALAPPROVE("appealApprove", "稽核报告申诉通知", "您有一份【{storeName}】稽核报告申诉等待您审批，请及时处理。"),
    ;

    protected static final Map<String, FoodCheckNoticeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(FoodCheckNoticeEnum::getNode, Function.identity()));

    private String node;

    private String title;

    private String content;

    FoodCheckNoticeEnum(String node, String title, String content) {
        this.node = node;
        this.title = title;
        this.content = content;
    }

    public String getNode() {
        return node;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public static FoodCheckNoticeEnum getByNode(String node) {
        return map.get(node);
    }
}
