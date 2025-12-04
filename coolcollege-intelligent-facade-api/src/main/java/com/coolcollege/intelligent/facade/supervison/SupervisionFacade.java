package com.coolcollege.intelligent.facade.supervison;

import com.coolcollege.intelligent.facade.request.supervison.SupervisionRemindRequest;
import com.coolstore.base.dto.ResultDTO;

/**
 * 督导助手
 * @author byd
 * @date 2023-04-13 10:41
 */
public interface SupervisionFacade {

    /**
     * 督导通知
     * @param supervisionRemindRequest
     * @return
     */
    ResultDTO<Boolean> supervisionRemind(SupervisionRemindRequest supervisionRemindRequest);


    ResultDTO<Boolean> supervisionData(SupervisionRemindRequest supervisionRemindRequest);

}
