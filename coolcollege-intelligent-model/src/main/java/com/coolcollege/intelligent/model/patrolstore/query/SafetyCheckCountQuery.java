package com.coolcollege.intelligent.model.patrolstore.query;

import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author byd
 */
@Data
public class SafetyCheckCountQuery {


    @ApiModelProperty("用户id 列表")
    private List<String> userIdList;

    /**
     * 时间
     */
    @ApiModelProperty("开始时间")
    private Long beginTime;
    /**
     * 时间
     */
    @ApiModelProperty("结束时间")
    private Long endTime;
    /**
     *
     */
    @ApiModelProperty("页数")
    private Integer pageNum = 1;
    /**
     *
     */
    @ApiModelProperty("分页大小")
    private Integer pageSize = 10;

    @ApiModelProperty(hidden = true)
    ExportServiceEnum exportServiceEnum;
}
