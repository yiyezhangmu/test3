package com.coolcollege.intelligent.model.tbdisplay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableItemDTO {

    private Long snapshotId;

    /**
     * 检查项
     */
    private Long id;

    /**
     * 检查项名称
     */
    private String columnName;

    /**
     * 检查内容名称
     */
    private String contentName;
    /**
     * 标准图
     */
    private String standardPic;
    /**
     * 描述
     */
    private String description;


    /**
     *  陈列快捷项id
     */
    private Long quickColumnId;

    /**
     * 排序字段
     */
    private Integer orderNum;

    private BigDecimal score;

    /**
     * 是否AI检查
     */
    private Integer isAiCheck;

    private String aiCheckStdDesc;

    /**
     * 检查图片,0不强制1强制
     */
    private Integer mustPic;
}
