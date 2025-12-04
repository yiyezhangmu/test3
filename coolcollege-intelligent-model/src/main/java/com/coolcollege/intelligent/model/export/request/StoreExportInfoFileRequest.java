package com.coolcollege.intelligent.model.export.request;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/07/06
 */
@Data
public class StoreExportInfoFileRequest extends FileExportBaseRequest {
    private Boolean isAdmin;
//    private List<String> storeIdList;
//    private List<String> fullRegionIdList;
    private String enterpriseId;

    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 门店编号
     */
    private String storeNum;
    /**
     * 管辖区域或门店
     */
    private List<String> regionIdList;
    /**
     * 门店状态
     */
    private String storeStatus;
}
