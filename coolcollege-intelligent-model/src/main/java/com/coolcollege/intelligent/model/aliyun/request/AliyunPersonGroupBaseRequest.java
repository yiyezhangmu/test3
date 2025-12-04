package com.coolcollege.intelligent.model.aliyun.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/27
 */
@Data
public class AliyunPersonGroupBaseRequest {
    @NotBlank(message = "分组Id不能为空")
    private String groupId;

}
