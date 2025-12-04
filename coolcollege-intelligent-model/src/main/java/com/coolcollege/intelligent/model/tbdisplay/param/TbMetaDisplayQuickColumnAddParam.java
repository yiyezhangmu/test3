package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
* @Description:
* @Author:
* @CreateDate: 2021-03-02 17:24:31
*/
@Data
public class TbMetaDisplayQuickColumnAddParam {

    /**
     * 检查项名称
     */
    @NotEmpty(message = "检查项名称不能为空")
    private String columnName;

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