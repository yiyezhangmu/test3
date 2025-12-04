package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author 邵凌志
 * @date 2020/7/1 10:46
 */
@Data
public class AliyunMetricsDTO {

    public static final String NUM = "num";
    public static final String SUMMARY = "summary";
    public static final String STOREMAP = "storeMap";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 租户id
     */
    private String corp_id;

    /**
     * 分组id
     */
    private String group_id;

    /**
     * 人脸id
     */
    private String face_id;

    /**
     * 时间周期
     */
//    @NotBlank(message = "时间周期不能为空")
    private String period;

    /**
     * 时间周期
     * num: 获取基础数据
     * summary： 获取浓缩视频
     * storeMap：获取轨迹地图
     */
//    @NotBlank(message = "时间周期不能为空")
    private String type;

    /**
     * 开始时间
     */
    private String start_time;

    /**
     * 结束时间
     */
    private String end_time;

    /**
     * 时间戳开始时间
     */
    private String startTime;

    /**
     * 时间戳结束时间
     */
    private String endTime;

    /**
     * 页码
     */
    private String page_num;

    /**
     * 页数
     */
    private String page_size;

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }
}
