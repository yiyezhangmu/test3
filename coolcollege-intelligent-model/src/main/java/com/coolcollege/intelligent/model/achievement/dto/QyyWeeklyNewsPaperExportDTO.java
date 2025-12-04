package com.coolcollege.intelligent.model.achievement.dto;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import lombok.Data;

@Data
public class QyyWeeklyNewsPaperExportDTO extends ExportBaseRequest {
    private String enterpriseId;
}
