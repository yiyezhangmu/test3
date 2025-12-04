package com.coolcollege.intelligent.model.newstore.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 拜访记录列表request
 * @author zhangnan
 * @date 2022-03-08 9:32
 */
@Data
public class NsVisitRecordListRequest extends PageBaseRequest {

    @ApiModelProperty("新店id")
    private Long newStoreId;

    @ApiModelProperty("拜访状态：ongoing进行中，completed完成")
    private String status;

    @ApiModelProperty("新店名称")
    private String newStoreName;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("新店类型，多个")
    private List<String> newStoreTypes;

    @ApiModelProperty("拜访表id，多个")
    private List<Long> metaTableIds;

    @ApiModelProperty("拜访提交时间：开始")
    private Long completedBeginTime;

    @ApiModelProperty("拜访提交时间：结束")
    private Long completedEndTime;
}
