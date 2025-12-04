package com.coolcollege.intelligent.service.songxia;

import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.SongXiaDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSalesInfoVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSampleInfoVO;
import com.coolcollege.intelligent.facade.request.PageRequest;

public interface SongXiaService {
    PageDTO<SongXiaSalesInfoVO> getSalesInfo(SongXiaDTO songXiaDTO);

    PageDTO<SongXiaSampleInfoVO> getSampleInfo(PageRequest request);

    PageDTO<SongXiaSampleInfoVO> getStockInfo(SongXiaDTO request);
}
