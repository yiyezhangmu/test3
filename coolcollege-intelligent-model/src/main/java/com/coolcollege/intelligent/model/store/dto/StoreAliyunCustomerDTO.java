package com.coolcollege.intelligent.model.store.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
*
*  @author 邵凌志
*/
@Data
public class StoreAliyunCustomerDTO {

    /**
     * 门店id
     */
    @JsonProperty(value = "store_id")
    private String storeId;

    /**
    * 阿里云人脸图像id
    */
    @JsonProperty(value = "face_id")
    @NotBlank(message = "人脸id不能为空")
    private String faceId;

    /**
    * 阿里云人脸图像地址
    */
    @JsonProperty(value = "pic_url")
    @NotBlank(message = "人脸图像不能为空")
    private String picUrl;

    /**
    * 顾客姓名
    */
    private String name;

    /**
    * 是否vip，0：否，1：是
    */
    @JsonProperty(value = "is_vip")
    private Integer isVip;

    /**
    * 顾客年龄
    */
    private Integer age;

    /**
    * 顾客电话
    */
    private String phone;

    /**
    * 顾客生日
    */
    private String birthday;

    /**
     * 首次进店时间
     */
    @JsonProperty(value = "first_appear_time")
    private Long firstAppearTime;

    /**
    * 创建时间
    */
    private Long createTime;

    /**
    * 创建人
    */
    private String createName;

    /**
    * 修改时间
    * isNullAble:1
    */
    private Long updateTime;

    /**
    * 修改人
    * isNullAble:1
    */
    private String updateName;
}
