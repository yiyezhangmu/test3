package com.coolcollege.intelligent.model.enterprise.request;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2021/11/24
 */
@Data
public class EnterpriseFollowRecordsRequest {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /**
     * 线索id
     */
    private long cluesId;
}
