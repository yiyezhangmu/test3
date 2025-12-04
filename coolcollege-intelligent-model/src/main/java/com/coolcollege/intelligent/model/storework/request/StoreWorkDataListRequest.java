package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.common.enums.storework.SortFieldEnum;
import com.coolcollege.intelligent.common.enums.storework.SortTypeEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 店务记录门店统计查询请求参数
 * @author wxp
 * @date 2022-9-21 19:13
 */
@ApiModel
@Data
public class StoreWorkDataListRequest extends PageRequest {

    /**
     * 区域id列表  选择区域树查询时使用
     */
    @ApiModelProperty("区域id列表")
    private List<String> regionIdList;

    @ApiModelProperty("店务id")
    @NotNull(message = "店务id不能为空")
    private Long storeWorkId;

    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    @NotBlank(message = "店务日期不能为空")
    private String storeWorkDate;

    @ApiModelProperty("检查表映射表id")
    private Long tableMappingId;

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    private Integer completeStatus;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    private Integer commentStatus;

    @ApiModelProperty("排序类型")
    private SortTypeEnum sortType;

    @ApiModelProperty("排序字段")
    private SortFieldEnum sortField;

    @ApiModelProperty(value = "是否是查询当前区域的子区域数据")
    private Boolean childRegion = false;

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

    @ApiModelProperty(value = "门店id")
    private String storeId;

    @ApiModelProperty(value = "是否导出")
    private Boolean export;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;
    @ApiModelProperty(hidden = true)
    private ExportServiceEnum exportServiceEnum;
    @ApiModelProperty(hidden = true)
    private String userId;

    @ApiModelProperty(hidden = true)
    private List<String> regionPathList;

    @ApiModelProperty(hidden = true)
    private Boolean summary;

    @ApiModelProperty(hidden = true)
    private String regionPath;

    @ApiModelProperty(hidden = true)
    private CurrentUser currentUser;

    @ApiModelProperty(value = "门店ids")
    private List<String> storeIds;
}
