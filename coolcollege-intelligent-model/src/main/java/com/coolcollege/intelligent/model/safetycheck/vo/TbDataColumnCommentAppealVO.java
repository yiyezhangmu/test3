package com.coolcollege.intelligent.model.safetycheck.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@ApiModel
@Data
public class TbDataColumnCommentAppealVO {

    /**
     * 最新点评内容
     */
    private TbDataColumnCommentVO tbDataColumnCommentVO;
    /**
     * 最新申诉内容
     */
    private TbDataColumnAppealVO tbDataColumnAppealVO;


}