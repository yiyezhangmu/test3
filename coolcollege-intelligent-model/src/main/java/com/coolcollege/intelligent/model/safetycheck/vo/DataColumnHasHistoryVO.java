package com.coolcollege.intelligent.model.safetycheck.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author wxp
 * @Date 2023/8/25 10:17
 * @Version 1.0
 */
@ApiModel
@Data
public class DataColumnHasHistoryVO {

    @ApiModelProperty("数据检查项id")
    private Long dataColumnId;

    @ApiModelProperty("点评次数")
    private Integer commentCount;

    @ApiModelProperty("申诉次数")
    private Integer appealCount;

    @ApiModelProperty("检查项提交次数")
    private Integer checkCount;

    @ApiModelProperty("是否有检查历史")
    private Boolean hasCheckHistory;

    @ApiModelProperty("是否有点评历史")
    private Boolean hasCommentHistory;

    @ApiModelProperty("是否有申诉历史")
    private Boolean hasAppealHistory;

    public Boolean getHasCheckHistory() {
        if(checkCount != null && checkCount > 1){
            return true;
        }
        return false;
    }

    public Boolean getHasCommentHistory() {
        if(commentCount != null && commentCount > 1){
            return true;
        }
        return false;
    }

    public Boolean getHasAppealHistory() {
        if(appealCount != null && appealCount > 1){
            return true;
        }
        return false;
    }
}