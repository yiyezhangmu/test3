package com.coolcollege.intelligent.common.enums.coolcollege;

/**
 * @author: xuanfeng
 * @date: 2022-04-26 11:13
 */
public enum CoolCollegeTodoListType {
    /**
     * PC端学习任务待办
     */
    TODO_PC_STUDY("todoPcStudy"),
    /**
     * PC端/移动端考试待办
     */
    TODO_EXAM("todoExam"),
    /**
     * 移动端调研任务待办
     */
    TODO_MOBILE_RESEARCH("todoMobileResearch"),
    /**
     * 移动端学习任务待办
     */
    TODO_MOBILE_STUDY("todoMobileStudy"),

    /**
     * 学习项目
     */
    TODO_STUDY_PROJECT("todoStudyProject")
    ;
    private void setCode(String code) {
        this.code = code;
    }

    private String code;
    CoolCollegeTodoListType(String code){
        this.code=code;
    }
    public String getCode() {
        return code;
    }
}
