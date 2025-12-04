package com.coolcollege.intelligent.model.achievement.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import java.util.List;

/**
 * 下载模板
 *
 * @author chenyupeng
 * @since 2021/12/3
 */
@Data
public class AchievementTargetExportRequest extends FileExportBaseRequest {

    /**
     * 门店id
     */
    private List<String> storeIds;

    /**
     * 区域Id
     */
    private Long regionId;

    /**
     * 是否展示当前
     */
    private Boolean showCurrent;

    /**
     * 年份
     */
    private Integer achievementYear;

    private CurrentUser user;
}
