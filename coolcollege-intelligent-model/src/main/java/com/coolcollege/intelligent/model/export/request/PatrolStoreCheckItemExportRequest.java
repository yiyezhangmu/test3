package com.coolcollege.intelligent.model.export.request;

import lombok.Data;

import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/6/7 19:32
 */
@Data
public class PatrolStoreCheckItemExportRequest extends FileExportBaseRequest{
    private Long tableId;
    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;
}
