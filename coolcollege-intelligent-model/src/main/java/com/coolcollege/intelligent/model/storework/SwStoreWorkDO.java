package com.coolcollege.intelligent.model.storework;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-09-08 02:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwStoreWorkDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("店务名称")
    private String workName;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("店务有效期开始时间")
    private Date beginTime;

    @ApiModelProperty("店务有效期结束时间")
    private Date endTime;

    @ApiModelProperty("店务说明")
    private String workDesc;

    @ApiModelProperty("逾期是否允许继续执行，0：否，1：是")
    private Integer overdueContinue;

    @ApiModelProperty("店务状态 进行中ongoing 停止stop")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("店务创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("人员信息{commentPersonInfo:[{type:person,value:}{type:position,value:}],cooperatePersonInfo:[{type:person,value:}{type:position,value:}]}")
    private String personInfo;

    @ApiModelProperty("AI使用范围")
    private String aiRange;
}