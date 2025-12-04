package com.coolcollege.intelligent.service.yunda;

import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;

/**
 * @author wxp
 */
public interface YundaService {

    void sendServiceWindowMsg(CoolCollegeMsgDTO dto, String messageUrl);

}
