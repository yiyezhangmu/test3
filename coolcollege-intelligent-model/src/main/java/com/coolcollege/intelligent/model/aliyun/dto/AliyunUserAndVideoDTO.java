package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/11/7 14:51
 */
@Data
public class AliyunUserAndVideoDTO {

    private Integer pageNum;

    private Integer pageSize;

    private String corpId;

    private String eid;

    private String videoId;

    private Long startTime;

    private Long endTime;
}
