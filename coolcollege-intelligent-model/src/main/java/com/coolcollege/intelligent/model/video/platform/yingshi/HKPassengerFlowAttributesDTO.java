package com.coolcollege.intelligent.model.video.platform.yingshi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 海康门店客流属性统计
 */
@Data
public class HKPassengerFlowAttributesDTO {

    @ApiModelProperty(value = "性别")
    private Gender gender;
    @ApiModelProperty(value = "年龄段")
    private AgeGroup ageGroup;
    @ApiModelProperty("门店id")
    private String storeId;
    @ApiModelProperty("门店名称")
    private String storeName;
    @ApiModelProperty("日期 2024-09-24")
    private String dateTime;

    @Data
    public static class Gender {
        @ApiModelProperty("总数")
        private AttributeNumber total;
        @ApiModelProperty("男")
        private AttributeNumber male;
        @ApiModelProperty("女")
        private AttributeNumber female;
        @ApiModelProperty("未知")
        private AttributeNumber unknown;
    }

    @Data
    public static class AgeGroup {
        @ApiModelProperty("总数")
        private AttributeNumber total;
        @ApiModelProperty("儿童")
        private AttributeNumber kid;
        @ApiModelProperty("少年")
        private AttributeNumber child;
        @ApiModelProperty("青少年")
        private AttributeNumber teenager;
        @ApiModelProperty("青年")
        private AttributeNumber young;
        @ApiModelProperty("壮年")
        private AttributeNumber prime;
        @ApiModelProperty("中年")
        private AttributeNumber middle;
        @ApiModelProperty("中老年")
        private AttributeNumber middleAged;
        @ApiModelProperty("老年")
        private AttributeNumber old;
        @ApiModelProperty("未知")
        private AttributeNumber unknown;
    }

    @Data
    public static class AttributeNumber {
        int value;
    }

}
