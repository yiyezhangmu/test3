package com.coolcollege.intelligent.model.platform;

import lombok.Data;

import java.util.List;

/**
 * @author 柳敏 min.liu@coolcollege.cn
 * @since 2021-05-13 15:59
 */
@Data
public class SysCourseClassify {

    /**
     * 课程分类id
     */
    private String id;

    /**
     * 课程分类名称
     */
    private String name;

    /**
     * 上级id
     */
    private Long parentId;

    /**
     * TODO
     */
    private Integer orderId;

    /**
     * 子集
     */
    private List<SysCourseClassify> children;

    private Long courseCount;

    /**
     * 0 分类下没有数据
     * 1 分类下有数据
     */
    private Integer secondMenuFlag;

    private Long level;

}
