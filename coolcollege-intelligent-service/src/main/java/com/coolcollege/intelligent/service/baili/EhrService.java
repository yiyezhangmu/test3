package com.coolcollege.intelligent.service.baili;

import com.coolcollege.intelligent.model.baili.request.BailiEmployeeRequest;
import com.coolcollege.intelligent.model.baili.request.BailiOrgRequest;
import com.coolcollege.intelligent.model.baili.request.BailiStoreRequest;
import com.coolcollege.intelligent.model.baili.response.BailiEmployeeResponse;
import com.coolcollege.intelligent.model.baili.response.BailiOrgResponse;
import com.coolcollege.intelligent.model.baili.response.BailiPageResponseBase;
import com.coolcollege.intelligent.model.baili.response.BailiStoreResponse;

/**
 * @author zhouyiping
 */
public interface EhrService {

    BailiPageResponseBase<BailiEmployeeResponse> listEmployeeBaseInfo(BailiEmployeeRequest request);

    BailiPageResponseBase<BailiOrgResponse> listOrg(BailiOrgRequest request);
    BailiPageResponseBase<BailiStoreResponse>liststoreInfo(BailiStoreRequest request);
}
