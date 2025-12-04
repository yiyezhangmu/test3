package com.coolcollege.intelligent.facade.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreSettingRequest {

    /**
     * 企业id 不能为空
     */
    private String enterpriseId;

}
