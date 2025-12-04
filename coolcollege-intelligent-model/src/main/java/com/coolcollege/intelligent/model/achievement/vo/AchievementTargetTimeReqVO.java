package com.coolcollege.intelligent.model.achievement.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 门店月目标请求vo
 * @Author: mao
 * @CreateDate: 2021/5/24 10:43
 */
@Data
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTargetTimeReqVO {
    /**
     * id
     */
    private Long id;
    /**
     * 月目标金额
     */
    private BigDecimal achievementTarget;
    /**
     * 日期
     */
    private Date beginDate;
}
