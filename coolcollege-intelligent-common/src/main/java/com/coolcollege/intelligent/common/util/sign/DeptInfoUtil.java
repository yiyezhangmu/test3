package com.coolcollege.intelligent.common.util.sign;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 获取周大福部门信息
 *
 * @author byd
 * @date 2022-11-08 15:40
 */
@Slf4j
@Component
public class DeptInfoUtil {

    @Value("${zdf.appId}")
    private String appId;
    @Value("${zdf.appSecret}")
    private String appSecret;
    @Value("${zdf.url.domain}")
    private String url;

    private static final String OPEN_API_GET_DEPT_INFO = "/open_api/v1/dc-gravity-ps/staff/get_dept_struct";

    private static final String OPEN_API_DEPT_BU = "10003";

    private static final Integer SUC_CODE = 200;

    public DeptStructData getDeptInfo(String deptId) {
        String nonce = String.valueOf(RandomUtils.nextInt());
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        String encryptText = SignUtils.getEncryptText(appId, appSecret, nonce, String.valueOf(timestamp));
        String signature = SignUtils.getSignatureBySHA256(encryptText);
        String sign = String.format("appId=%s&timestamp=%s&nonce=%s&sign=%s", appId, timestamp, nonce, signature);
        JSONObject params = new JSONObject();
        params.put("bu", OPEN_API_DEPT_BU);
        params.put("deptId", deptId);
        log.info("zdf#getDeptInfo,dept:{},request:{},sign:{}", deptId, params.toJSONString(), sign);
        String result = CoolHttpClient.sendPostJsonRequest(url + OPEN_API_GET_DEPT_INFO + "?" + sign, params.toJSONString());
        log.info("zdf#getDeptInfo,dept:{},result:{}", deptId, result);
        if (StringUtils.isBlank(result)) {
            log.info("zdf#getDeptInfo,dept:{},返回值为空", deptId);
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer code = jsonObject.getInteger("code");
        if (!SUC_CODE.equals(code)) {
            log.info("zdf#getDeptInfo,dept:{},请求失败", deptId);
            return null;
        }
        String data = jsonObject.getString("data");
        if (StringUtils.isBlank(data)) {
            log.info("zdf#getDeptInfo,dept:{},返回值data为空", deptId);
            return null;
        }
        List<DeptStructData> resultList = JSONObject.parseArray(data, DeptStructData.class);
        return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
    }
}
