package com.coolcollege.intelligent.model.aliyun.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/12
 */
@Data
public class WebHookMessage {

    @JsonProperty("score")
    private Double score;

    private String  shotTime;

    @JsonProperty("picUrl")
    private String picUrl;

    @JsonProperty("targetPicUrl")
    private String targetPicUrl;

    @JsonProperty("taskId")
    private String taskId;

    private String gbId;



}
