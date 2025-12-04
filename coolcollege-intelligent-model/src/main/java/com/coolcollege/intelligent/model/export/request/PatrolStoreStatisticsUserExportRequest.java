package com.coolcollege.intelligent.model.export.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/7 16:40
 */
@Data
public class PatrolStoreStatisticsUserExportRequest extends FileExportBaseRequest{
    /**
     * 人员id
     */
    private List<String> userIdList;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 开始时间
     */
    private Date beginDate;
}
