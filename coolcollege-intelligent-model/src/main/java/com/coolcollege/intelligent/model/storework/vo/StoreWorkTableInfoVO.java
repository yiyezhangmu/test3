package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.storework.request.StoreWorkColumnInfoRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 店务定义表信息
 * @author wxp
 * @date 2022-09-08 10:48
 */
@ApiModel
@Data
public class StoreWorkTableInfoVO {

    @ApiModelProperty("检查表映射表id")
    private Long tableMappingId;

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

    @ApiModelProperty("检查项列表")
    private List<StoreWorkColumnInfoVO> columnInfoList;

    @ApiModelProperty("检查表扩展信息,前端存开始结束时间")
    private String tableInfo;

    /**
     * 检查表名称
     */
    @ApiModelProperty("检查表名称")
    private String tableName;

    /**
     * 表属性 0:普通表 1:高级表 2:加分表 3:权重表 4:扣分表 5:AI检查表 6:自定义表
     */
    @ApiModelProperty("表属性")
    private Integer tableProperty;

    /**
     * 是否锁定，当发布新任务后表锁定，增加复制表的功能
     */
    @ApiModelProperty("是否锁定，当发布新任务后表锁定，增加复制表的功能 0:未锁定 1：已锁定")
    private Integer locked;

}
