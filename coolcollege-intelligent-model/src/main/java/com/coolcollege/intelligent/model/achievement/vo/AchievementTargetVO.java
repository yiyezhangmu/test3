package com.coolcollege.intelligent.model.achievement.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 业绩门店目标请求VO
 * @Author: mao
 * @CreateDate: 2021/5/21 10:54
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTargetVO {
    /**
     * id
     */
    private Long id;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 目标年份
     */
    private Integer achievementYear;
    /**
     * 详细目标集合
     */
    private List<AchievementTargetTimeReqVO> targetDetail;
    /**
     * 年目标总金额
     */
    private BigDecimal yearAchievementTarget;
    /**
     * 页面size
     */
    @NotNull(message = "分页显示条数不能为空")
    @Max(value = 100, message = "分页显示条数不能大于100")
    private Integer pageSize;
    /**
     * 页面num
     */
    @NotNull(message = "当前页数不能为空")
    @Min(value = 1, message = "当前页数不能小于1")
    private Integer pageNum;
    /**
     * 门店数组集合
     */
    private List<String> storeIds;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 是否展示当前区域
     */
    private Boolean showCurrent;

}
