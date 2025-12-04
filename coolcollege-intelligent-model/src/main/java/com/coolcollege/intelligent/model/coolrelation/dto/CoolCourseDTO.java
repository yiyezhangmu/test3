package com.coolcollege.intelligent.model.coolrelation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 酷学院课程信息dto
 * @author ：xugangkun
 * @date ：2021/5/17 14:29
 */
@Data
public class CoolCourseDTO {
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
     * 多个课程信息
     */
    private List<CoolCourseListDTO> coolCourseListDTOList;
}
