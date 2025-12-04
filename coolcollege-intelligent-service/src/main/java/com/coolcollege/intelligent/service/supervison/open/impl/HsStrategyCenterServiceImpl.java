package com.coolcollege.intelligent.service.supervison.open.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.supervison.TaskGroupEnum;
import com.coolcollege.intelligent.common.enums.supervison.TaskLabelsEnum;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.sign.HuShangSignUtils;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.supervision.dto.*;
import com.coolcollege.intelligent.model.supervision.request.HsSignRequest;
import com.coolcollege.intelligent.model.supervision.request.HsUserStoreRequest;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.supervison.open.HsStrategyCenterService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2023/3/1 19:22
 * @Version 1.0
 */
@Service
@Slf4j
public class HsStrategyCenterServiceImpl implements HsStrategyCenterService {

    private static String hsStrategyCenterUrl = "https://vgateway.hsay.com";
    private static String signKey = "T3Usaf8b2eCiX9Ljq";

    private static String USERNAME = "cool";
    private static String PASSWORD = "C9!2cMzkLP";
    @Value("${spring.profiles.active}")
    private String active;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisUtilPool redis;
    @Autowired
    private RedisUtilPool redisUtilPool;



    public void callHsStrategyCenter(String enterpriseId, Long supervisionTaskId, String checkCode) {
        log.info("callHsStrategyCenter enterpriseId:{}, supervisionTaskId:{}, checkCode:{}", enterpriseId, supervisionTaskId, checkCode);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if (Objects.isNull(enterpriseConfigDO)) {
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        //封装调用参数
        SupervisionTaskDTO supervisionTaskDTO = new SupervisionTaskDTO();
        supervisionTaskDTO.setTaskId(supervisionTaskId);
        supervisionTaskDTO.setRuleCode(checkCode);
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        // 1~6位随机数，12345
        String nonce = String.valueOf(RandomUtils.nextInt(1,999999));
        String createdSign = HuShangSignUtils.generateSign(timeStamp, nonce, "",signKey,false);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("TIMESTAMP", String.valueOf(timeStamp));
        httpHeaders.set("NONCE", nonce);
        httpHeaders.set("SIGN", createdSign);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("callHsStrategyCenter request is {}", JSONObject.toJSONString(supervisionTaskDTO));
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(hsStrategyCenterUrl, new HttpEntity<>(JSONObject.toJSONString(supervisionTaskDTO), httpHeaders), JSONObject.class);
        log.info("callHsStrategyCenter resp {}", JSONObject.toJSONString(responseEntity));
        JSONObject response = responseEntity.getBody();
        if (Objects.nonNull(response) && (SyncConfig.STATUS_200.equals(response.getString("code")))) {
            // 返回成功 变更任务状态
            CheckResultDTO checkResultDTO = JSONObject.parseObject(response.getString("data"), CheckResultDTO.class);
            // 0:待定 1:通过 2:不通过
            if(checkResultDTO != null && checkResultDTO.getResult() == 1){
//                updateCompleteStatusCancelUpcoming(enterpriseId, supervisionTaskId);
            }
        } else {
            log.info("调用沪上策略中心异常 {}", JSONObject.toJSONString(response));
        }
    }

    @Override
    public String getToken(String timestamp,String username,String password) {
        log.info("getToken timestamp:{}, userName:{}, password:{}", timestamp, username, password);
        String url = "/auth/api/auth/open/login";
        String tokenKey = String.format("HS%s%s", username, password);
        String token = redis.getString(tokenKey);
        if (StringUtils.isNotEmpty(token)){
            return token;
        }
        String format = String.format("%s%s%s", username, timestamp, password);
        String sign = md5(format);
        HsSignRequest hsSignRequest = new HsSignRequest();
        hsSignRequest.setUsername(username);
        hsSignRequest.setPassword(password);
        hsSignRequest.setTimestamp(timestamp);
        hsSignRequest.setSign(sign);
        HttpEntity<Object> req = new HttpEntity<>(hsSignRequest);
        String finalUrl = hsStrategyCenterUrl + url;
        ResponseEntity<JSONObject>  exchange = restTemplate.exchange(finalUrl, HttpMethod.POST, req, JSONObject.class);
        log.info(JSONObject.toJSONString(exchange));
        String resultToken = (String) ((HashMap) exchange.getBody().get("data")).get("token");
        if (StringUtils.isNotEmpty(resultToken)){
            redis.setString(tokenKey,resultToken,6000);
        }
        return resultToken;
    }


    @Override
    public List<HsUserStoreDTO> getSupervisorStores(String eid,List<String> dingDingUserIds) {
        log.info("getSupervisorStores dingDingUserIds:{}", JSONObject.toJSONString(dingDingUserIds));
        //如果不是沪上企业，使用模拟数据
        if (!"993bc9ea70cb4d798b740b41ac0c8a3d".equals(eid)){
            String testData = redisUtilPool.getString("testData");
            List<HsUserStoreDTO> hsStoreDTOS = JSONObject.parseArray(testData, HsUserStoreDTO.class);
            log.info("沪上模拟人店关系：{}",testData);
            return  hsStoreDTOS;
        }
        String url = "/supervisor/api/supers/getSupervisorStores";
        long timeMillis = System.currentTimeMillis();
        String token = getToken(String.valueOf(timeMillis), USERNAME, PASSWORD);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("API-TOKEN-V1", token);
        HsUserStoreRequest hsUserStoreRequest = new HsUserStoreRequest();
        hsUserStoreRequest.setDingDingUserIds(dingDingUserIds);
        HttpEntity<Object> req = new HttpEntity<>(hsUserStoreRequest,headers);
        String finalUrl = hsStrategyCenterUrl + url;
        ResponseEntity<JSONObject>  exchange = restTemplate.exchange(finalUrl, HttpMethod.POST, req, JSONObject.class);
        log.info(JSONObject.toJSONString(exchange));
        List<HsUserStoreDTO> result = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), HsUserStoreDTO.class);
        log.info("沪上真实人店关系：{}",JSONObject.toJSONString(result));
        return result;
    }

    @Override
    public List<TaskGroupDTO> getTaskGroups() {
        /**String url = "/supervisor/api/supers/getTaskGroups";
        long timeMillis = System.currentTimeMillis();
        String token = getToken(String.valueOf(timeMillis), USERNAME, PASSWORD);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("API-TOKEN-V1", token);
        HttpEntity<Object> req = new HttpEntity<>(headers);
        String finalUrl = hsStrategyCenterUrl + url;
//        String finalUrl = "http://58.33.58.162:10020/mock/14/supervisor/api/supers/getTaskGroups";
        ResponseEntity<JSONObject>  exchange = restTemplate.exchange(finalUrl, HttpMethod.GET, req, JSONObject.class);
        log.info("getTaskGroups" + JSONObject.toJSONString(exchange));
        JSONObject resultBody = exchange.getBody();
        if(resultBody == null){
            return new ArrayList<>();
        }**/
        List<TaskGroupEnum> allTaskGroupEnum = TaskGroupEnum.getAllTaskGroupEnum();
        List<TaskGroupDTO> list = allTaskGroupEnum.stream()
                .map(x -> new TaskGroupDTO(x.getGroupCode(), x.getGroupName()))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public List<TaskLabelDTO> getTaskLabels() {
        /**String url = "/supervisor/api/supers/getTaskLabels";
        long timeMillis = System.currentTimeMillis();
        String token = getToken(String.valueOf(timeMillis), USERNAME, PASSWORD);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("API-TOKEN-V1", token);
        HttpEntity<Object> req = new HttpEntity<>(headers);
        String finalUrl = hsStrategyCenterUrl + url;
//        String finalUrl = "http://58.33.58.162:10020/mock/14/supervisor/api/supers/getTaskLabels";
        ResponseEntity<JSONObject>  exchange = restTemplate.exchange(finalUrl, HttpMethod.GET, req, JSONObject.class);
        log.info("getTaskGroups" + JSONObject.toJSONString(exchange));
        JSONObject resultBody = exchange.getBody();
        if(resultBody == null){
            return new ArrayList<>();
        }**/
        List<TaskLabelsEnum> allTaskLabelEnum = TaskLabelsEnum.getAllTaskLabelEnum();
        List<TaskLabelDTO> list = allTaskLabelEnum.stream()
                .map(x -> new TaskLabelDTO(x.getLabelCode(), x.getLabelName()))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public List<RelatedBusinessDTO> getRelatedBusiness() {
        String url = "/supervisor/api/supers/getRelatedBusiness";
        long timeMillis = System.currentTimeMillis();
        String token = getToken(String.valueOf(timeMillis), USERNAME, PASSWORD);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("API-TOKEN-V1", token);
        HttpEntity<Object> req = new HttpEntity<>(headers);
        String finalUrl = hsStrategyCenterUrl + url;
//        String finalUrl = "http://58.33.58.162:10020/mock/14/supervisor/api/supers/getRelatedBusiness";
        ResponseEntity<JSONObject>  exchange = restTemplate.exchange(finalUrl, HttpMethod.GET, req, JSONObject.class);
        log.info("getTaskGroups" + JSONObject.toJSONString(exchange));
        JSONObject resultBody = exchange.getBody();
        if(resultBody == null){
            return new ArrayList<>();
        }
        return JSONObject.parseArray(JSONObject.toJSONString(resultBody.get("data")), RelatedBusinessDTO.class);
    }

    @Override
    public List<CheckRuleDTO> getCheckRules() {
        String url = "/supervisor/api/supers/getCheckRules";
        long timeMillis = System.currentTimeMillis();
        String token = getToken(String.valueOf(timeMillis), USERNAME, PASSWORD);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("API-TOKEN-V1", token);
        HttpEntity<Object> req = new HttpEntity<>(headers);
        String finalUrl = hsStrategyCenterUrl + url;
//        String finalUrl = "http://58.33.58.162:10020/mock/14/supervisor/api/supers/getCheckRules";
        ResponseEntity<JSONObject>  exchange = restTemplate.exchange(finalUrl, HttpMethod.GET, req, JSONObject.class);
        log.info("getTaskGroups" + JSONObject.toJSONString(exchange));
        JSONObject resultBody = exchange.getBody();
        if(resultBody == null){
            return new ArrayList<>();
        }
        return JSONObject.parseArray(JSONObject.toJSONString(resultBody.get("data")), CheckRuleDTO.class);
    }

    /**
     * 验签
     * @param inputString
     * @return
     */
    public static String md5(String inputString){
        return DigestUtils.md5DigestAsHex(inputString.getBytes());
    }
}
