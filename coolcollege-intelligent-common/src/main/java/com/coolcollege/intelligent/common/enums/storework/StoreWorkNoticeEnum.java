package com.coolcollege.intelligent.common.enums.storework;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 店务通知枚举
 * @author wxp
 */

public enum StoreWorkNoticeEnum {
    START_REMIND("start_remind", "今日工作提醒", "您的门店【{storeName}】今天有【{cycleName}】任务【{dutyName}】已经开始，记得处理哦。"),
    BEFORE_START_REMIND("before_start_remind", "今日工作提醒", "您的门店【{storeName}】今天有【{cycleName}】任务【{dutyName}】【{minute}】分钟后开始，记得处理哦。"),
    BEFORE_END_REMIND("before_end_remind", "今日工作提醒", "您的门店【{storeName}】今天有【{cycleName}】任务【{dutyName}】未完成，请尽快处理哦。"),
    AFTER_HANDLE_REMIND_COMMENT("after_handle_remind_comment", "工作通知", "您管辖的门店【{storeName}】已完成【{cycleName}】【{dutyName}】，记得点评哦。"),
    AFTER_COMMENT_REMIND_HANDLER("after_comment_remind_handler", "工作通知", "您的门店【{storeName}】【{cycleName}】【{dutyName}】已点评，得分【{score}】。"),
    TURN_NOTICE("turn", "转交工作通知", "【{fromUserName}】给你转交门店【{storeName}】【{cycleName}】【{dutyName}】任务，请尽快完成。"),
    ;


    protected static final Map<String, StoreWorkNoticeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(StoreWorkNoticeEnum::getOperate, Function.identity()));

    private String operate;

    private String title;

    private String content;

    StoreWorkNoticeEnum(String operate, String title, String content) {
        this.operate = operate;
        this.title = title;
        this.content = content;
    }

    public String getOperate() {
        return operate;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public static StoreWorkNoticeEnum getByOperate(String operate) {
        return map.get(operate);
    }
}
