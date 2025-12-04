package com.coolcollege.intelligent.facade.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author xuanfeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreUserRequest {

    /**
     * 企业id 不能为空
     */
    private String enterpriseId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 分页页码
     */
    private Integer pageNum;

    /**
     * 分页条数
     */
    private Integer pageSize;
}
