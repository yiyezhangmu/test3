package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2021/7/8 16:21
 */
@Data
public class ColumnDetailListRequest extends ColumnStatisticsRequest{
    private Integer pageSize;

    private Integer pageNum;

    /**
     * 检查状态
     */
    private String checkStatus;

    /**
     * 检查项id
     */
    private Long metaColumnId;
    /**
     * FINISH：完成， HANDLE：待处理 RECHECK：待审批
     */
    private String status;
    /**
     * 是否获取区域直连门店数据
     */
    private Boolean getDirectStore = false;
}
