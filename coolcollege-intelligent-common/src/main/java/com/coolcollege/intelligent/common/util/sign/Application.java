package com.coolcollege.intelligent.common.util.sign;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author JiangDongZhao
 */
@Slf4j
public class Application {


    public static void main(String[] args) {
        handle();
    }

    private static int handle() {
        AppData selected = new AppData();
        selected.setAppId("gravity-coolstore");
        selected.setAppSecret("28C5C98C-5CF0-4F81-BB89-58C458DD1BBF");
        String nonce = String.valueOf(RandomUtils.nextInt());
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        String encryptText = SignUtils.getEncryptText(selected.getAppId(), selected.getAppSecret(), nonce, String.valueOf(timestamp));
        String signature = SignUtils.getSignatureBySHA256(encryptText);

        String sign = String.format("appId=%s&timestamp=%s&nonce=%s&sign=%s", selected.getAppId(), timestamp, nonce, signature);
        System.out.printf("[%s|%s] ClipBoard update sign!:\n%s\n", selected.getAppId(), selected.getAppSecret(), sign);
        JSONObject params = new JSONObject();
        params.put("bu", "10003");
        params.put("deptId", "2000028");
        String result = HttpUtil.post("https://gravity-cloud-uat.ctf.com.cn/open_api/v1/dc-gravity-ps/staff/get_dept_struct?" + sign, params.toJSONString());
        System.out.println(result);

        // do not thing
        return 0;
    }

}
