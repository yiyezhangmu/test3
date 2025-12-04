package com.coolcollege.intelligent.service.requestBody.region;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/3/2 15:24
 * @Version 1.0
 */
@Data
public class RegionOrderNumRequest {
    private List<Long> regionIds;
}
