package com.coolcollege.intelligent.service.sync;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.service.sync.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddressBookTask implements Runnable {
    private BaseEvent baseEvent;

    private String logId;

    public AddressBookTask(BaseEvent baseEvent, String logId) {
        this.baseEvent = baseEvent;
        this.logId = logId;
    }

    @Override
    public void run() {
        try {
            baseEvent.doEvent();
        } catch (Exception e) {
            log.error("doEvent err, event={}", JSON.toJSONString(baseEvent), e);
        }
    }
}
