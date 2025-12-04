package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * @Author wxp
 * @Date 2024/3/25 17:09
 * @Version 1.0
 */
@Data
public class XfsgTransferStoreDTO {

    /**
     * 门店编码
     */
    private String storeCode;
    /**
     * 原父节点ID（非必传）
     */
    private String oldParentDeptCode;
    /**
     * 新的父节点ID
     */
    private String newParentDeptCode;

}
