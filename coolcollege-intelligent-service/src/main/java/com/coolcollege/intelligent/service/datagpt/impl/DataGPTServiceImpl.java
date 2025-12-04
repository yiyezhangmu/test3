package com.coolcollege.intelligent.service.datagpt.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.http.HttpHelper;
import com.coolcollege.intelligent.service.datagpt.DataGPTService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * @ClassName DataGPTServiceImpl
 * @Description 用一句话描述什么
 */
@Service
@Slf4j
public class DataGPTServiceImpl implements DataGPTService {

    @Resource
    private RedisUtilPool redisUtilPool;

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public String getDataGptToken() {
        String appSecretKey = "db119bd3_1022_44&&6e7d9634-b3e8-40c2-8b74-0aaa4dcdb253";
        try {
            String url = MessageFormat.format("http://cn-shanghai-alicloud.api.clickzetta.com/clickzetta-campaign-data/open/api/v1/appSecretKey/generateAuthToken?appSecretKey={0}", URLEncoder.encode(appSecretKey, "UTF-8"));
            JSONObject jsonObject = HttpHelper.get(url);
            log.info("getDataGptToken response:{}", JSONObject.toJSONString(jsonObject));
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                String accessToken = data.getString("token");
                String expireMillisecond = data.getString("expireMillisecond");
                return accessToken;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String getDataGptTokenPlus(String enterpriseId) {
        enterpriseId = (Constants.ONLINE_ENV.equals(env) || Constants.HD_ENV.equals(env)) ? enterpriseId : "140e9bf7acf445a08864d1afcc1814fa";
        String appSecretKey = redisUtilPool.hashGet("data_gpt_enterprise_key", enterpriseId);
        log.info("getDataGptTokenPlus，enterpriseId:{},appSecretKey:{}", enterpriseId, appSecretKey);
        if(StringUtils.isBlank(appSecretKey)){
            log.info("企业未在云器后台添加账号，并生成AppSecretKey,enterpriseId:{}", enterpriseId);
            return null;
        }
        // String appSecretKey = "db119bd3_1022_44&&6e7d9634-b3e8-40c2-8b74-0aaa4dcdb253";
        String url = "https://cn-shanghai-alicloud.api.clickzetta.com/clickzetta-campaign-data/open/api/v1/appSecretKey/generateAuthTokenPlus";
        try {
            // 构造请求体
            JSONObject body = new JSONObject();
            body.put("appSecretKey", appSecretKey);

            JSONObject rowLevelRule = new JSONObject();
            rowLevelRule.put("mode", "SIMPLE");

            JSONArray ruleItems = new JSONArray();
            JSONObject ruleItem = new JSONObject();
            ruleItem.put("name", "租户过滤");
            ruleItem.put("value", enterpriseId);
            ruleItems.add(ruleItem);
            rowLevelRule.put("ruleItems", ruleItems);
            body.put("rowLevelRule", rowLevelRule);

            // 发送 POST 请求
            JSONObject jsonObject = HttpHelper.post(url, body);

            log.info("getDataGptTokenPlus response: {}", JSONObject.toJSONString(jsonObject));

            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (data != null) {
                    String accessToken = data.getString("token");
                    String expireMillisecond = data.getString("expireMillisecond");
                    log.info("Token expires at: {}", expireMillisecond);
                    return accessToken;
                }
            }
        } catch (Exception e) {
            log.error("Error when getting token plus", e);
            throw new RuntimeException(e);
        }
        return null;
    }

}
