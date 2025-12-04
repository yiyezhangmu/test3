package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/9/26 17:35
 * @Version 1.0
 */
@Data
@ApiModel(value = "门店检查项数据项VO 自定义项")
public class StoreWorkDataTableDefColumnVO {

    @ApiModelProperty("值1")
    private String value1;

    @ApiModelProperty("值2")
    private String value2;

    /**
     * 检查表ID
     */
    private Long metaTableId;

    /**
     * 检查项名称
     */
    private String columnName;

    /**
     *  描述信息
     */
    private String description;

    /**
     * 长度
     */
    private Integer columnLength;

    /**
     * 格式：单选Radio，多选Checkbox，当行文本Input，多行文本Textarea，数字，日期，图片
     */
    private String format;

    /**
     * 是否必填
     */
    private Integer required;

    /**
     * 创建者用户ID
     */
    private String createrUserId;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 多选框的时候，选择的值列表，逗号分隔
     */
    private String chooseValues;

    /**
     * 自定义检查项schema json串
     */
    private String schema;

    /**
     * 删除标记  0:正常 1:删除
     */
    private Integer deleted;



}
