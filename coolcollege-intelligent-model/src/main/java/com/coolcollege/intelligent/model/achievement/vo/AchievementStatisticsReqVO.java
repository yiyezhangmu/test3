package com.coolcollege.intelligent.model.achievement.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Description: 业绩类型请求VO
 * @Author: mao
 * @CreateDate: 2021/5/21 10:54
 */
@Data
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AchievementStatisticsReqVO {
    /**
     * 区域集合
     */
    private List<Long> regionIds;
    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private Date beginDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 门店id （无效查询字段）
     */
    private String storeId;

    /**
     * 业绩类型Id
     */
    private Long achievementTypeId;
    /**
     * 模板Id
     */
    private Long achievementFormworkId;


    /**
     * 是否是当前区域
     */
    private Boolean showCurrent;

}
