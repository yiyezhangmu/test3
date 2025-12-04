package com.coolcollege.intelligent.model.metatable.vo;

import lombok.Data;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/3 18:51
 */
@Data
public class TaskStoreMetaTableColVO {

    /**
     * 检查项id
     */
    private Long metaTableColumnId;

    /**
     * 检查项名称
     */
    private String metaTableColumnName;

    /**
     * 检查项上传的图片
     */
    private String checkPics;

    /**
     * 检查项的描述信息
     */
    private String checkText;

    /**
     * 检查项上传的视频
     */
    private String checkVideo;

    /**
     * 自定义检查项值1
     */
    private String value1;

    /**
     * 自定义检查项值2
     */
    private String value2;

    /**
     * 图片数组
     */
    private String photoArray;

    /**
     * 评价备注
     */
    private String remark;

    /**
     * 检查项类型，只有自定义检查项会返回该值
     */
    private String format;

}
