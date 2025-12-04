package com.coolcollege.intelligent.model.metatable;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 
 * 自定义检查表列定义

 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaDefTableColumnDO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 检查表ID
     */
    private Long metaTableId;
    /**
     * 检查项ID
     */
    private Long metaColumnId;

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


    private static final long serialVersionUID = 1L;
}