package com.coolcollege.intelligent.facade.qyy;

import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.BigOrderBoardDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.taobao.api.ApiException;

public interface CardDemoFacade {

    ResultDTO sendUserOrderTop(String eid) throws ApiException;
}
