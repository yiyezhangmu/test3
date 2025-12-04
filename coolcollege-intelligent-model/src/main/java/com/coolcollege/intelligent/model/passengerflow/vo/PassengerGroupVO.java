package com.coolcollege.intelligent.model.passengerflow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 客群统计
 */
@ApiModel
@Data
public class PassengerGroupVO {

    @ApiModelProperty("客群总人数")
    private Integer personCount;

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
        @ApiModelProperty("壮年")
        private Integer prime;
        @ApiModelProperty("中年")
        private Integer middle;
        @ApiModelProperty("青年")
        private Integer young;
        @ApiModelProperty("老年")
        private Integer old;
        @ApiModelProperty("未知年龄段")
        private Integer unknownAge;
        @ApiModelProperty("少年")
        private Integer child;
        @ApiModelProperty("青少年")
        private Integer teenager;
        @ApiModelProperty("中老年")
        private Integer middleAged;

        public Age(Integer prime, Integer middle, Integer young, Integer old, Integer unknownAge, Integer child, Integer teenager, Integer middleAged) {
            this.prime = prime;
            this.middle = middle;
            this.young = young;
            this.old = old;
            this.unknownAge = unknownAge;
            this.child = child;
            this.teenager = teenager;
            this.middleAged = middleAged;
        }
    }


}
