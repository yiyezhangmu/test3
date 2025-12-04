package com.coolcollege.intelligent.facade.consumer.listener;

import com.aliyun.openservices.ons.api.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;

/**
 * @author ：xugangkun
 * @date ：2022/2/21 10:38
 */
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class EnterpriseScriptListenerTest {

    @InjectMocks
    private EnterpriseScriptListener enterpriseScriptListener;

    @Test
    public void consume() {
        String text = "{\"appType\":\"qw2\",\"authUserId\":\"woayJeDAAA5aMMGbxo3hZ8eiOkx3qDtg\",\"corpId\":\"wpayJeDAAAJBJKBRgxLIiqMO6jVvRHtw\",\"dbName\":\"coolcollege_intelligent_2\",\"eid\":\"361e95a9c2be463db9192c1ed28b28cc\"}";
        Message message = new Message();
        message.setBody(text.getBytes(StandardCharsets.UTF_8));
        message.setTag("enterprise_open_enterprise_run_script");
//        enterpriseScriptListener.consume(message, new ConsumeContext());
    }

}
