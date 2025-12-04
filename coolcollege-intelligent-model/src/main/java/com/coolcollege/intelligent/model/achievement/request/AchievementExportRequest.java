package com.coolcollege.intelligent.model.achievement.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/10/28
 */
@Data
public class AchievementExportRequest extends FileExportBaseRequest {
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
     * 门店名称
     */
    private String storeName;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 暂时没有用
     */
    private Long achievementTypeId;

    /**
     * 是否是当前区域
     */
    private Boolean showCurrent;

    private CurrentUser user;

    /**
     * 门店ID数组字符串
     */
    private String storeIdStr;

    /**
     * 业绩模板Id
     */
    private Long achievementFormworkId;

    /**
     * 业绩类型id(逗号分隔)
     */
    private String achievementTypeIdStr;

    /**
     * 产生人
     */
    private String produceUserIdStr;

    /**
     * 是否查询空业绩产生人
     */
    private Boolean isNullProduceUser;
}
