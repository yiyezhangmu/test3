package com.coolcollege.intelligent.facade.open.api.patrolstore;

import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.open.PatrolStoreDetailListDTO;
import com.coolcollege.intelligent.facade.dto.open.PatrolStoreRecordListDTO;
import com.coolcollege.intelligent.facade.request.patrolstore.PatrolStoreDetailListRequest;
import com.coolcollege.intelligent.facade.request.patrolstore.PatrolStoreListRequest;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * 开放平台巡店api
 *
 * @author byd
 * @date 2022-07-11 10:24
 */
public interface PatrolStoreApiService {

    /**
     * 巡店记录列表
     *
     * @param request
     * @return
     */
    OpenApiResponseVO<PageDTO<PatrolStoreRecordListDTO>> list(PatrolStoreListRequest request);

    /**
     * 巡店记录详情列表
     *
     * @param request
     * @return
     */
    OpenApiResponseVO<PageDTO<PatrolStoreDetailListDTO>> detailList(PatrolStoreDetailListRequest request);
}
