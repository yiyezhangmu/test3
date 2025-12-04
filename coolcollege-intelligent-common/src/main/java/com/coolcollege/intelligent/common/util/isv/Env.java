package com.coolcollege.intelligent.common.util.isv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class Env {


    @Value("${env.reqPerSecond}")
    private int reqPerSecond;

    @Value("${env.corpReqPerSecond}")
    private int corpReqPerSecond;

    public int getReqPerSecond() {
        return reqPerSecond;
    }

    public int getCorpReqPerSecond() {
        return corpReqPerSecond;
    }

}
