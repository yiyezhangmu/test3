package com.coolcollege.intelligent.facade.fsGroup;

import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.taobao.api.ApiException;

public interface FsGroupTimerFacade {
    ResultDTO<Integer> sendFsGroupNotice(String enterpriseId)throws ApiException;

    ResultDTO<Integer> queryFsGroupNoticeReadNum(String enterpriseId)throws ApiException;
}
