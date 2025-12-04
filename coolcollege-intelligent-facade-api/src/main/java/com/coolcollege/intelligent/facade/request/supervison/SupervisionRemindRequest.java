package com.coolcollege.intelligent.facade.request.supervison;

import lombok.Data;

/**
 * @author byd
 * @date 2023-04-13 11:18
 */
@Data
public class SupervisionRemindRequest {

    /**
     * 企业id 不能为空
     */
    private String enterpriseId;
}
