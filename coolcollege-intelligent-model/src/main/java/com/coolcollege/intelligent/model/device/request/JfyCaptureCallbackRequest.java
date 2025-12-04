package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class JfyCaptureCallbackRequest {

    @ApiModelProperty("任务ID")
    private String taskId;

    @ApiModelProperty("数据")
    private List<CaptureCallback> data;

    @Data
    public static class CaptureCallback {

        @ApiModelProperty("错误码")
        private String errCode;

        @ApiModelProperty("错误信息")
        private String errMsg;

        @ApiModelProperty("状态")
        private Integer status;

        @ApiModelProperty("时间点")
        private String timePoint;

        @ApiModelProperty("图片地址")
        private String url;
    }
}
