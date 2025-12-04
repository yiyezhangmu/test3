package com.coolcollege.intelligent.facade.open.api.songxia;

import com.coolcollege.intelligent.facade.dto.openApi.SongXiaDTO;
import com.coolcollege.intelligent.facade.request.PageRequest;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

public interface SongXiaApi {
    /**
     * 销量统计
     * @param songXiaDTO
     * @return
     */
    OpenApiResponseVO getSalesInfo(SongXiaDTO songXiaDTO);

    /**
     * 在样统计
     */
    OpenApiResponseVO getSampleInfo(PageRequest request);

    /**
     * 库存统计
     */
    OpenApiResponseVO getStockInfo(SongXiaDTO songXiaDTO);
}
