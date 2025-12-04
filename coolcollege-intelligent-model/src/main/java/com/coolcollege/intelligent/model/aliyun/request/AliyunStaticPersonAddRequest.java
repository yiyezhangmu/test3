package com.coolcollege.intelligent.model.aliyun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/25
 */
@Data
public class AliyunStaticPersonAddRequest {


    private String storeId;

    private String picUrl;

    private String name;

    private Integer age;

    private String phone;

    private String birthday;

    private String remark;

    private String groupId;

    private String personalTag;

    private String email;

    private Integer gender;

    private String wechat;

}
