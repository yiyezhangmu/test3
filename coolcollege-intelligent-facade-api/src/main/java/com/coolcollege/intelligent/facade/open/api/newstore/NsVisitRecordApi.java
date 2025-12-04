package com.coolcollege.intelligent.facade.open.api.newstore;

import com.coolcollege.intelligent.facade.dto.openApi.NsVisitRecordDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
* @Author: hu hu
* @Date: 2025/1/15 10:09
* @Description: 
*/
public interface NsVisitRecordApi {

    OpenApiResponseVO getVisitRecordList(NsVisitRecordDTO param);

}
