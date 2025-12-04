package com.coolcollege.intelligent.model.aliyun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/26
 */
@Data
public class AliyunPersonGroupUpdateRequest {

    private String groupId;

    private String groupName;

    private String remark;

}
