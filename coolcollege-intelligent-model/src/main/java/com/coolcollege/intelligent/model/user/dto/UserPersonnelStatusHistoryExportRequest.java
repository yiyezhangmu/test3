package com.coolcollege.intelligent.model.user.dto;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import lombok.Data;

import java.util.List;

/**
 * 人事状态导出参数
 * @author ：xugangkun
 * @date ：2022/3/9 11:08
 */
@Data
public class UserPersonnelStatusHistoryExportRequest extends FileExportBaseRequest {
    /**
     * 所有用户
     */
    private List<String> allUserIdList;
    /**
     * 请求参数
     */
    private UserPersonnelStatusHistoryReportDTO query;
}
