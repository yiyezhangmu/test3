package com.coolcollege.intelligent.model.storework.dto;

import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.request.StoreTaskResolveRequest;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/10/27 14:51
 * @Version 1.0
 */
@Data
public class StoreWorkResolveDTO {

    private StoreTaskResolveRequest storeTaskResolveRequest;

    private SwStoreWorkDO swStoreWorkDO;
}
