package com.coolcollege.intelligent.model.achievement.request;

import com.coolcollege.intelligent.model.achievement.dto.AchievementDetailDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 业绩请求参数
 *
 * @author chenyupeng
 * @since 2021/10/25
 */
@Data
public class AchievementRequest {
    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 门店id
     */
    private List<String> storeIds;

    /**
     * 页面size
     */
    @NotNull(message = "分页显示条数不能为空")
    private Integer pageSize;
    /**
     * 页面num
     */
    @NotNull(message = "当前页数不能为空")
    @Min(value = 1, message = "当前页数不能小于1")
    private Integer pageNum;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 是否展示当前区域
     */
    private Boolean showCurrent;

    /**
     * 状态
     */
    private List<Integer> statusList;

    /**
     * 时间类型
     */
    private String timeType;

    /**
     * 业绩产生时间
     */
    private Date produceTime;

    /**
     * 业绩产生人id
     */
    private String produceUserId;

    /**
     * 业绩产生人名称
     */
    private String produceUserName;

    /**
     * 模板id
     */
    private Long achievementFormworkId;

    /**
     * 模板类型
     */
    private String achievementFormworkType;

    /**
     * 业绩详情
     */
    private List<AchievementDetailDTO> achievementDetailList;

    @ApiModelProperty("商品型号")
    private String goodsType;

    @ApiModelProperty("拓展字段")
    private String extendParam;

    @ApiModelProperty("商品数量")
    private String goodsNum;


}
