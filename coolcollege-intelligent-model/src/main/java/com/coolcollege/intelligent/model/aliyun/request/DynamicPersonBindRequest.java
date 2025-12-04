package com.coolcollege.intelligent.model.aliyun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/29
 */
@Data
public class DynamicPersonBindRequest {

    @JsonProperty("ali_pic_url")
    private String aliPicUrl;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("face_id")
    private String faceId;

}
