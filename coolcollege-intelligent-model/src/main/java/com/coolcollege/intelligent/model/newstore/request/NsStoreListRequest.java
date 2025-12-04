package com.coolcollege.intelligent.model.newstore.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 新店列表request
 * @author wxp
 * @date 2022-03-08 13:32
 */
@Data
public class NsStoreListRequest extends PageBaseRequest {

    @ApiModelProperty("开始创建时间")
    private Long createTimeStart;

    @ApiModelProperty("结束创建时间")
    private Long createTimeEnd;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("门店名称")
    private String name;

    @ApiModelProperty("门店类型")
    private List<String> typeList;

    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private List<String> statusList;

    @ApiModelProperty("负责人")
    private String directUserId;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("经度")
    private String longitude;
    /**
     * 当前人定位维度
     */
    @ApiModelProperty("纬度")
    private String latitude;

}
