package com.coolcollege.intelligent.model.aliyun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/25
 */
@Data
public class AliyunPersonUpdateRequest extends AliyunPersonBaseRequest {





    private String picUrl;

    private String name;

    private Integer age;

    private String phone;

    private String birthday;

    private String remark;

    @NotBlank(message = "分组不能为空")
    private String groupId;

    private String personalTag;

    private String email;
    private String wechat;
    private Integer gender;

}
