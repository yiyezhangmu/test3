package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

/**
 * <p>
 * 门店验收查询
 * </p>
 *
 * @author wangff
 * @since 2025/4/15
 */
@Data
public class StoreAcceptanceRequest {
    /**
     * 第三方业务id
     */
    private String thirdBusinessId;
}
