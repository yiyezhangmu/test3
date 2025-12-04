package com.coolcollege.intelligent.model.patrolstore.query;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.export.request.ExportBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
public class PatrolStoreCheckQuery extends ExportBaseRequest {

    @ApiModelProperty("区域id")
    private List<String> regionIdList;

    private Long regionId;

    @ApiModelProperty("门店id")
    private List<String> storeIdList;


    @ApiModelProperty("区域路径")
    private List<String> regionPathList;

    private String regionPath;

    @ApiModelProperty("检查表提交开始时间")
    private Date beginDate;

    @ApiModelProperty("检查表提交结束时间")
    private Date endDate;

    @ApiModelProperty("任务截止开始时间")
    private Date taskBeginDate;

    @ApiModelProperty("任务截止结束时间")
    private Date taskEndDate;

    @ApiModelProperty("检查表id")
    private Long metaTableId;

    @ApiModelProperty("任务类型")
    private String patrolType;

    @ApiModelProperty("巡店人id")
    private List<String> supervisorId;

    @ApiModelProperty("巡店人工号")
    private String supervisorJobNum;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("大区稽核状态 0: 待稽核 1:已稽核")
    private Integer bigRegionCheckStatus;

    @ApiModelProperty("战区稽核状态 0: 待稽核 1:已稽核")
    private Integer warZoneCheckStatus;

    @ApiModelProperty("大区稽核人id")
    private List<String> bigRegionUserId;

    @ApiModelProperty("大区稽核人工号")
    private String bigRegionUserJobNum;

    @ApiModelProperty("战区稽核人id")
    private List<String> warZoneUserId;

    @ApiModelProperty("战区稽核人工号")
    private String warZoneUserJobNum;

    @ApiModelProperty("稽核类型  1：大区稽核 2:战区稽核")
    private Integer checkType;

    @ApiModelProperty("0 稽核概览")
    private Integer type;

    private Long id;

    private Integer sort;
}
