package com.coolcollege.intelligent.model.patrolstore.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author byd
 * @date 2024-09-05 15:54
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreSubmitTableParam {

    @NotNull(message = "巡店记录id不能为空")
    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("多表提交参数")
    List<PatrolStoreSubmitParam> dataTableParamList;

    /**
     * 稽核类型  1：大区稽核 2:战区稽核
     */
    @ApiModelProperty("稽核类型  1：大区稽核 2:战区稽核")
    private Integer checkType;

    @ApiModelProperty("是否修改")
    private Boolean isModify;

    public Boolean getModify() {
        if (isModify == null) {
            return false;
        }
        return isModify;
    }
}
