package com.coolcollege.intelligent.model.storework.dto;

import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import lombok.Data;

/**
 * @Author wxp
 * @Date 2023/12/7 14:45
 * @Version 1.0
 */
@Data
public class StoreWorkHandleCommentUpdateDTO {

    private String enterpriseId;

    private String tcBusinessId;

    private String storeId;

    private Long storeWorkId;

    private Boolean reissueFlag;
}
