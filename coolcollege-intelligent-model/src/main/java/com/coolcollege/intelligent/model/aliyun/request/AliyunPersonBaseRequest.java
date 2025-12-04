package com.coolcollege.intelligent.model.aliyun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/25
 */
@Data
public class AliyunPersonBaseRequest {
    private String customerId;

}
