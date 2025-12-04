package com.coolcollege.intelligent.model.device.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EditDeviceLicense {

    @NotBlank(message = "名字不能为空")
    private String name;

    private String remark;

    @NotBlank(message = "licenseId不能为空")
    private String id;
}
