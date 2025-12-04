package com.coolcollege.intelligent.model.export.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 门店报表导出请求
 * @author shuchang.wei
 * @date 2021/6/7 15:02
 */
@Data
public class StoreExportRequest extends FileExportBaseRequest{
    private List<String> storeIdList;

    private Long regionId;

    /**
     * 所属区域
     */
    private List<String> regionIdList;

    private Date beginDate;

    private Date endDate;

    /**
     * 门店状态
     */
    private String storeStatus;
}
