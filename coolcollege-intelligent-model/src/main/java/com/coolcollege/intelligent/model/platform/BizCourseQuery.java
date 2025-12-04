package com.coolcollege.intelligent.model.platform;

import lombok.Data;

/**
 * 搜索酷学院课程请求对象
 *
 * @author 柳敏 min.liu@coolcollege.cn
 * @since 2021-05-13 17:02
 */
@Data
public class BizCourseQuery {


    private String coolToken;

    /**
     * 分页，从1开始
     */
    private Integer pageNumber = 1;

    /**
     * 分页
     */
    private Integer pageSize = 9;

    /**
     * 课程名称
     */
    private String title;


    /**
     * 课程分类单独id
     */
    private String classifyId;


    /**
     * 酷学院中的企业id
     */
    private String enterpriseId;

    /**
     * 酷学院中的用户id
     */
    private String userId;

}
