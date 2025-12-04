package com.coolcollege.intelligent.model.achievement.request;

import com.coolcollege.intelligent.model.export.request.ExportBaseRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shuchang.wei
 * @date 2021/5/25 20:33
 */
@Data
public class AchievementDetailListExport extends ExportBaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private AchievementDetailListRequest request;
}
