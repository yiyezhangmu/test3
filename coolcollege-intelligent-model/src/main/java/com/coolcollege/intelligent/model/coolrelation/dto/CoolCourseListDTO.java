package com.coolcollege.intelligent.model.coolrelation.dto;

import lombok.Data;

/**
* @Description:  多条酷学院课程信息dto
* @Param:
* @Author: tangziqi
* @Date: 2023/5/23~15:24
*/

@Data
public class CoolCourseListDTO {
    /**
     * 主键
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 课程封面
     */
    private String cover;

    /**
     * 业务类型（0：专题；1：综合； 2：微课制作； 3：学习项目课程；4: ; 6：图文课）
     */
    private Integer bizType;


    /**
     *
     */
    private String resourceId;

    /**
     *
     */
    private String isNewVersion;

    /**
     * 课程分类单独id
     */
    private String classifyId;

    /**
     * 课程分类单独id-app
     */
    private String courseClassify;

    /**
     * 课程来源：1-知识库 2-sop 3-免费课程
     */
    private Integer courseType;

    /**
     *
     */
    private String contentType;
    /**
     *
     */
    private String courseId;
    /**
     *
     */
    private String duration;
    /**
     *
     */
    private String path;
    /**
     *
     */
    private String size;
    /**
     *
     */
    private String type;
    /**
     *
     */
    private String url;
    /**
     *
     */
    private Boolean isCourseware;
}
