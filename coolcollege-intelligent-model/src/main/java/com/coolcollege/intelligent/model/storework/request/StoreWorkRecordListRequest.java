package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.common.enums.storework.SortFieldEnum;
import com.coolcollege.intelligent.common.enums.storework.SortTypeEnum;
import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 店务记录列表查询请求参数
 * @author wxp
 * @date 2022-10-09 17:13
 */
@ApiModel(value = "店务记录列表查询请求参数")
@Data
public class StoreWorkRecordListRequest extends PageRequest {

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

    @ApiModelProperty("日清类型")
    private String workCycle;

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    private Integer completeStatus;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    private Integer commentStatus;

    @ApiModelProperty("排序类型")
    private SortTypeEnum sortType;

    @ApiModelProperty("排序字段")
    private SortFieldEnum sortField;
    /**
     * 店务开始时间
     */
    @ApiModelProperty(value = "店务开始时间(默认近7天的数据)")
    private Long beginStoreWorkTime;

    /**
     * 店务结束时间
     */
    @ApiModelProperty(value = "店务结束时间(默认近7天的数据)")
    private Long endStoreWorkTime;

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

    @ApiModelProperty(value = "是否导出")
    private Boolean export;

    @ApiModelProperty(value = "业务ID")
    private String businessId;

    @ApiModelProperty(value = "数据表ID")
    private Long dataTableId;

    @ApiModelProperty(hidden = true)
    private List<String> regionPathList;

}
