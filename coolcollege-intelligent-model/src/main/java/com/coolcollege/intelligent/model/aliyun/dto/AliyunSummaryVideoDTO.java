package com.coolcollege.intelligent.model.aliyun.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author 邵凌志
 * @date 2020/7/10 16:35
 */
@Data
public class AliyunSummaryVideoDTO implements Serializable {


    /**
     * 企业id
     */
    @JsonProperty(value = "enterprise_id")
    private String enterpriseId;

    /**
     * 租户id
     */
    @NotBlank(message = "租户id不能为空")
    @JsonProperty(value = "corp_id")
    private String corpId;

    /**
     * 摄像头id
     */
    @NotNull(message = "设备编号不能为空")
    @JsonProperty(value = "video_list")
    private List<String> videoList;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String start_time;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String end_time;
}
