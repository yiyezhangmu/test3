package com.coolcollege.intelligent.model.video.platform.yingshi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * 多维客流设备 客流统计
 */
@Data
public class YingshiDeviceKitPeoplecountingDTO {

    @ApiModelProperty("进入人数")
    private int enter;

    @ApiModelProperty("离开人数")
    private int exit;

    @ApiModelProperty("通过人数")
    private int pass;

    @ApiModelProperty(value = "性别")
    private Gender gender;

    @ApiModelProperty(value = "年龄")
    private Age age;

    @Data
    public static class Gender {
        @ApiModelProperty("男")
        private Integer male;
        @ApiModelProperty("女")
        private Integer female;
        @ApiModelProperty("未知")
        private Integer unknownGender;

        public Gender(Integer male, Integer female, Integer unknownGender) {
            this.male = male;
            this.female = female;
            this.unknownGender = unknownGender;
        }
    }

    @Data
    public static class Age {
        @ApiModelProperty("少年")
        private Integer child;
        @ApiModelProperty("青年")
        private Integer young;
        @ApiModelProperty("中年")
        private Integer middle;
        @ApiModelProperty("老年")
        private Integer old;
        @ApiModelProperty("青少年")
        private Integer teenager;
        @ApiModelProperty("壮年")
        private Integer prime;
        @ApiModelProperty("中老年")
        private Integer middleAged;
        @ApiModelProperty("未知年龄段")
        private Integer unknownAge;

        public Age(Integer child, Integer young, Integer middle, Integer old, Integer teenager, Integer prime, Integer middleAged, Integer unknownAge) {
            this.child = child;
            this.young = young;
            this.middle = middle;
            this.old = old;
            this.teenager = teenager;
            this.prime = prime;
            this.middleAged = middleAged;
            this.unknownAge = unknownAge;
        }
    }

    public static YingshiDeviceKitPeoplecountingDTO convertDTO(HKPassengerFlowAttributesDTO attributesDTO) {
        if(Objects.isNull(attributesDTO)){
            return null;
        }
        YingshiDeviceKitPeoplecountingDTO result = new YingshiDeviceKitPeoplecountingDTO();
        result.setEnter(attributesDTO.getGender().getTotal().getValue());
        result.setGender(new Gender(attributesDTO.getGender().getMale().getValue(), attributesDTO.getGender().getFemale().getValue(), attributesDTO.getGender().getUnknown().getValue()));
        result.setAge(new Age(attributesDTO.getAgeGroup().getChild().getValue(),
                            attributesDTO.getAgeGroup().getYoung().getValue(),
                            attributesDTO.getAgeGroup().getMiddle().getValue(),
                            attributesDTO.getAgeGroup().getOld().getValue(),
                            attributesDTO.getAgeGroup().getTeenager().getValue(),
                            attributesDTO.getAgeGroup().getPrime().getValue(),
                            attributesDTO.getAgeGroup().getMiddleAged().getValue(),
                            attributesDTO.getAgeGroup().getUnknown().getValue()));
        return result;
    }


}
