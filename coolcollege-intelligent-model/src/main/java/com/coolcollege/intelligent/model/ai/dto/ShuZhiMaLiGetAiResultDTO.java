package com.coolcollege.intelligent.model.ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ShuZhiMaLiGetAiResultDTO {

    @ApiModelProperty("来源系统任务唯⼀标识")
    private String outBizNo;

    @ApiModelProperty("透传业务参数")
    private String outBizParam;

    @ApiModelProperty("检测结果")
    private List<InspectResult> inspectResult;

    @Data
    public static class InspectResult {
        @ApiModelProperty("质检素材链接")
        private String filePath;

        @ApiModelProperty("质检素材id")
        private String fileId;

        @ApiModelProperty("质检项")
        private String monitorItem;

        @ApiModelProperty("质检素材检测结果代码")
        private String bizCode;

        @ApiModelProperty("质检素材检测结果描述")
        private String bizMsg;

        @ApiModelProperty("质检素材检测结果")
        private Object result;

        @ApiModelProperty("质检素材检测结果")
        private String checkResult;

        public String getCheckResult() {
            if (this.checkResult != null) {
                return this.checkResult;
            }
            if (this.bizCode == null) {
                return null;
            }
            switch (this.bizCode) {
                case "30001":
                    return "合格";
                case "30002":
                    return "不合格";
                case "30003":
                    return "违规";
                case "10005":
                    return "检测失败";
                case "202":
                    return "任务执⾏中";
                default:
                    return null;
            }
        }
        
        public void setCheckResult(String checkResult) {
            this.checkResult = checkResult;
        }
    }


}
