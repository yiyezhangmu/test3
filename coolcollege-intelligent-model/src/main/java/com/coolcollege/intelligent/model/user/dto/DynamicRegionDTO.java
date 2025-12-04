package com.coolcollege.intelligent.model.user.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author wxp
 * @Date 2024/3/25 13:45
 * @Version 1.0
 * 动态区域参数
 */
@Data
public class DynamicRegionDTO {

    private String appId;

    private String name;

    private List<String> value;

}


