package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/26
 */
@Data
public class AliyunEventDTO {
    private String eventType;
    private Integer pageSize;
    private Integer pageNumber;
    private Long startTime;
    private Long endTime;
    private String dataSourceId;


}
