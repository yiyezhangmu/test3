package com.coolcollege.intelligent.model.achievement.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("松下新增临时关系")
public class PanasonicAddRequest {

    private String storeId;
    private List<innerClass> data;

    @Data
    public static class innerClass {
        @ApiModelProperty("门店ID")
        private String storeId;
        @ApiModelProperty("品类")
        private String category;
        @ApiModelProperty("中类")
        private String middleClass;
        @ApiModelProperty("型号")
        private String type;
    }
}
