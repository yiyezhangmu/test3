package com.coolcollege.intelligent.model.achievement.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/26
 */
@Data
public class AchievementBaseRequest {

    private Long beginDate;

    private Long endDate;


}
