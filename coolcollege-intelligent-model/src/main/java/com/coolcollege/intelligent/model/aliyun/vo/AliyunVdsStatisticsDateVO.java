package com.coolcollege.intelligent.model.aliyun.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author 邵凌志
 * @date 2021/1/13 20:10
 */
@Data
public class AliyunVdsStatisticsDateVO extends AliyunVdsBaseStatisticsVO {

//    @JsonProperty("yyyy-MM-dd HH:mm:dd")
    @NotNull(message = "开始时间不能为空")
    private Long beginDateTime;

//    @JsonProperty("yyyy-MM-dd HH:mm:dd")
    @NotNull(message = "结束时间不能为空")
    private Long endDateTime;

    private String beginTime;

    private String endTime;

    private String faceId;

    private boolean all = false;

    /**
     * 是否包含昨天
     */
    private boolean containsYesterday;
}
