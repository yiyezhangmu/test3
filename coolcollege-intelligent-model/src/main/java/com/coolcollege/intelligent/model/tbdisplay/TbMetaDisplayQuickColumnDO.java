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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbMetaDisplayQuickColumnDO {
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
     * 检查项名称
     */
    private String columnName;

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新人姓名
     */
    private String editUserName;

    /**
     * 更新人id
     */
    private String editUserId;

    /**
     * 是否删除:0:未删除，1.删除
     */
    private Boolean deleted;

    /**
     * 检查项分数
     */
    private BigDecimal score;

    /**
     * 描述
     */
    private String description;

    /**
     * 标准图
     */
    private String standardPic;

    /**
     * 检查类型  0-快速检查项 1-检查内容
     */
    private Integer  checkType;

    /**
     * 检查图片,0不强制1强制
     */
    private Integer mustPic;
}