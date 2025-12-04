package com.coolcollege.intelligent.service.requestBody.region;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/3/7 14:25
 * @Version 1.0
 */
@Data
public class RegionAddPersonalRequest {

    private String regionId;

    private List<String> userIds;
}
