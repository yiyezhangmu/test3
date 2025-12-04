package com.coolcollege.intelligent.service.dingtalk;

import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;

/**
 * @author wxp
 */
public interface ServiceWindowService {

    void sendServiceWindowMsg(CoolCollegeMsgDTO dto, String messageUrl);

}
