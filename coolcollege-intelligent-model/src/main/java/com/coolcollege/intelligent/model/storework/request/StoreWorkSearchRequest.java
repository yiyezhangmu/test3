package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店务列表查询请求参数
 * @author wxp
 * @date 2022-9-15 11:13
 */
@ApiModel(value = "店务管理记录查询请求参数")
@Data
public class StoreWorkSearchRequest extends PageRequest {

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;
    /**
     * 店务名称
     */
    @ApiModelProperty("店务名称")
    private String workName;

    /**
     * 创建时间开始日期
     */
    @ApiModelProperty(value = "创建时间开始日期")
    private String beginCreateDate;

    /**
     * 创建时间结束日期
     */
    @ApiModelProperty(value = "创建时间结束日期")
    private String endCreateDate;

    /**
     * 创建时间开始日期
     */
    @ApiModelProperty(value = "创建时间开始日期")
    private Long beginCreateTime;

    /**
     * 创建时间结束日期
     */
    @ApiModelProperty(value = "创建时间结束日期")
    private Long endCreateTime;
}
