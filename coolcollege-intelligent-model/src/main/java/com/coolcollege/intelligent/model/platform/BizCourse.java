package com.coolcollege.intelligent.model.platform;

import lombok.Data;

/**
 * @author 柳敏 min.liu@coolcollege.cn
 * @since 2021-05-13 16:42
 */
@Data
public class BizCourse {

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
     * 课程分类单独id-pc
     */
    private String classifyId;

    /**
     * 课程分类单独id-app
     */
    private String courseClassify;


}
