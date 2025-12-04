package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 店务记录门店统计查询请求参数
 * @author wxp
 * @date 2022-9-21 19:13
 */
@ApiModel
@Data
public class StoreDataListRequest extends PageRequest {

    /**
     * 区域id列表  选择区域树查询时使用
     */
    @ApiModelProperty("区域id列表")
    private List<String> regionIdList;

    @ApiModelProperty("店务id")
    @NotNull(message = "店务id不能为空")
    private Long storeWorkId;


    @ApiModelProperty("检查表映射表id")
    private Long tableMappingId;

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    private Integer completeStatus;


    /**
     * 店务开始时间
     */
    @ApiModelProperty(value = "店务开始时间(默认近7天的数据)")
    private Long beginTime;

    /**
     * 店务结束时间
     */
    @ApiModelProperty(value = "店务结束时间(默认近7天的数据)")
    private Long endTime;

    /**
     * 店务开始日期
     */
    @ApiModelProperty(value = "店务开始时间(默认近7天的数据)")
    private String beginStoreWorkDate;

    /**
     * 店务结束日期
     */
    @ApiModelProperty(value = "店务结束时间(默认近7天的数据)")
    private String endStoreWorkDate;

    @ApiModelProperty(value = "检查项id")
    private Long metaColumnId;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

}
