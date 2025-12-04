package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: BillboardDTO
 * @Description:开单播报
 * @date 2023-03-30 15:55
 */
@Data
public class BillboardDTO {

    /**
     * 部门id
     */
    private String dingDeptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 已开单门店数
     */
    private Integer salesStoreNum;

    /**
     * 未开单门店数
     */
    private Integer noSalesStoreNum;

}
