package com.coolcollege.intelligent.model.export.request;

import lombok.Data;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/16 11:47
 */
@Data
public class StoreInfoExportRequest extends FileExportBaseRequest{
    List<String> fieldList;
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
