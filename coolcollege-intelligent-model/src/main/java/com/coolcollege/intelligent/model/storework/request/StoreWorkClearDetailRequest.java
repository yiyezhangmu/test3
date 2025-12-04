package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/23 13:51
 * @Version 1.0
 */
@Data
@ApiModel
public class StoreWorkClearDetailRequest extends PageRequest {
    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    @NotBlank(message = "店务周期不能为空")
    private String workCycle;
    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    private Date storeWorkDate;
    @ApiModelProperty("区域ID")
    private List<String> regionIds;
    @ApiModelProperty("区域ID")
    private List<String> storeIds;
    @ApiModelProperty("完成状态")
    private Integer completeStatue;
    @ApiModelProperty("点评状态")
    private Integer commentStatus;

    @ApiModelProperty("合格状态")
    private String eligibleStatus;

    @ApiModelProperty("检查表")
    private String swTable;

    @ApiModelProperty("metaTableId")
    private String metaTableId;

}
