package com.coolcollege.intelligent.model.storework;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   wxp
 * @date   2022-09-08 02:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwStoreWorkTableMappingDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("作业定时执行日期例周一到周五执行“1,2,3,4,5”例每月1号17号执行“1,17”")
    private String beginDate;

    @ApiModelProperty("作业开始时间 8:00 排序")
    private String beginTime;

    @ApiModelProperty("执行时长，单位秒")
    private Double limitHour;

    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    @ApiModelProperty("任务名称,即检查表名称")
    private String dutyName;

    @ApiModelProperty("分组排序")
    private Integer groupNum;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("处理人信息[{type:person,value:}{type:position,value:}]")
    private String handlePersonInfo;

    @ApiModelProperty("检查表扩展信息")
    private String tableInfo;

    @ApiModelProperty("点评人")
    private String commentPersonInfo;

}