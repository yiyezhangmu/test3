package com.coolcollege.intelligent.facade.songxia;

import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.taobao.api.ApiException;

public interface SongXiaFacade {

    /**
     * 发送撤样提醒
     */
    ResultDTO<Integer> sendRemindMsg(String enterpriseId) throws ApiException;
}
