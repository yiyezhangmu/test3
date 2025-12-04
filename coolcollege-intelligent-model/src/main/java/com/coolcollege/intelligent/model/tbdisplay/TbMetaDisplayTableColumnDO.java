package com.coolcollege.intelligent.model.tbdisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @Description:
* @Author:
* @CreateDate: 2021-03-02 17:24:31
*/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TbMetaDisplayTableColumnDO {
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
     * 检查项名称
     */
    private String columnName;

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 标准图
     */
    private String standardPic;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否删除:0:未删除，1.删除
     */
    private Integer deleted;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 修改人id
     */
    private String editUserId;

    /**
     * 修改人名称
     */
    private String editUserName;


    /**
     *  陈列快捷项id
     */
    private Long quickColumnId;

    /**
     * 检查项分数
     */
    private BigDecimal score;

    /**
     * 检查类型  1-检查内容   0-检查项
     */
    private Integer checkType;

    /**
     * 是否AI检查
     */
    private Integer isAiCheck;

    /**
     * ai标准描述
     */
    private String aiCheckStdDesc;

    /**
     * 检查图片,0不强制1强制
     */
    private Integer mustPic;

}