package com.coolcollege.intelligent.common.enums.supervison;

import java.util.Arrays;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2024/9/19 14:12
 * @Version 1.0
 */

public enum TaskGroupEnum {

    STORE_MANAGER_MEETING("STORE_MANAGER_MEETING","店长会议"),
    CLOSE_LOOP_FEEDBACK("CLOSE_LOOP_FEEDBACK","闭环反馈"),
    ;
    private String groupCode;

    private String groupName;


    TaskGroupEnum( String groupCode,String groupName) {
        this.groupCode = groupCode;
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }


    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public static final List<TaskGroupEnum> getAllTaskGroupEnum(){
        return Arrays.asList(TaskGroupEnum.values());
    }

}
