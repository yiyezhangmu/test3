package com.coolcollege.intelligent.facade.open.api.newbelle;

import com.coolcollege.intelligent.facade.dto.newbelle.TaskGoodsDetailDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

public interface NewBelleApi {

    /**
     * 发起货品反馈
     */
    OpenApiResponseVO sendProductFeedback(TaskGoodsDetailDTO param);

}
