package com.coolcollege.intelligent.service.passengerflow.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.passenger.FlowTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.passengerflow.PassengerFlowRecodeMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.jiefeng.PassengerFlowStatisticalThirdResp;
import com.coolcollege.intelligent.model.passengerflow.PassengerFlowRecordDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.SelectStoreDTO;
import com.coolcollege.intelligent.service.passengerflow.JieFengApiService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.PageHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * @author byd
 * @date 2025-09-09 15:01
 */
@Slf4j
@Service
public class JieFengApiServiceImpl implements JieFengApiService {

    @Value("${jiefeng.url}")
    private String jiefengUrl;
    @Value("${jiefeng.appKey}")
    private String jiefengAppKey;
    @Value("${jiefeng.secretKey}")
    private String jiefengSecretKey;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private PassengerFlowRecodeMapper passengerFlowRecodeMapper;

    @Autowired
    private RedisUtilPool redisUtilPool;


    /**
     * 获取API访问令牌
     *
     * @return 令牌字符串，失败时返回null
     */
    private String getToken() {
        String token = redisUtilPool.getString(RedisConstant.JIE_FENG_TOKEN);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }

        try {


            // 2. 构建请求URL（建议使用参数化方式）
            String url = String.format("%s/openApi/auth/gettoken?appKey=%s&secretKey=%s",
                    jiefengUrl,
                    jiefengAppKey,
                    jiefengSecretKey);

            // 3. 发送HTTP请求（添加超时和重试）
            String jsonStr = HttpUtil.createGet(url)
                    .timeout(5000) // 5秒超时
                    .execute()
                    .body();

            // 4. 解析响应
            ApiResponse response = JSONUtil.toBean(jsonStr, ApiResponse.class);
            if (response == null || !response.isSuccess()) {
                log.error("获取Token失败: 响应异常 - {}", jsonStr);
                return null;
            }
            log.info("获取Token成功: {}", JSONObject.toJSONString(response.getModel()));
            token = response.getModel().getString("tokenValue");
            redisUtilPool.setString(RedisConstant.JIE_FENG_TOKEN, token, response.getModel().getInteger("tokenTimeout") - 60);
            // 5. 安全获取Token
            return response.getModel().getString("tokenValue");

        } catch (Exception e) {
            log.error("获取Token异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String addStoreNode(String storeName, String storeAddress, String storeLongitude, String storeLatitude) {
// 1. 参数校验


        // 2. 构建JSON请求体（推荐使用JSONObject）
        String requestBody = JSONUtil.createObj()
                .set("storeName", storeName)
                .set("storeAddress", storeAddress)
                .set("longitude", storeLongitude)
                .set("latitude", storeLatitude)
                .toString();

        try {
            // 3. 创建请求
            HttpRequest request = HttpUtil.createPost(jiefengUrl + "/openApi/gather/cloudStore/store/create")
                    .header("Content-Type", "application/json")
                    .header("token", this.getToken())
                    .body(requestBody)
                    .timeout(5000); // 5秒超时
            log.info("请求参数:{}", requestBody);
            // 4. 执行请求
            HttpResponse response = request.execute();
            if (response.isOk()) {
                log.info("请求成功！响应: {}", response.body());
                ApiResponse apiResponse = JSONUtil.toBean(response.body(), ApiResponse.class);
                if (apiResponse != null && apiResponse.isSuccess()) {
                    return apiResponse.getModel().getString("id");
                }
                return null;
            } else {
                log.error("请求失败！状态码: {}, 响应: {}", response.getStatus(), response.body());

            }
        } catch (Exception e) {
            log.error("API请求异常:", e);
        }
        return null;
    }


    /**
     * 编辑云店铺信息
     *
     * @param id           店铺ID（必填）
     * @param storeName    店铺名称（必填）
     * @param storeAddress 店铺地址（必填）
     * @param longitude    经度（-180~180）
     * @param latitude     纬度（-90~90）
     * @return API响应字符串，失败时返回null
     */
    @Override
    public String editStore(String id, String storeName, String storeAddress,
                            String longitude, String latitude) {


        // 2. 构建JSON请求体
        String requestBody = JSONUtil.createObj()
                .set("id", id)
                .set("storeName", storeName)
                .set("storeAddress", storeAddress)
                .set("longitude", longitude)
                .set("latitude", latitude)
                .toString();

        try {
            // 3. 创建请求
            HttpRequest request = HttpUtil.createPost(jiefengUrl + "/openApi/gather/cloudStore/store/edit")
                    .header("Content-Type", "application/json")
                    .header("token", this.getToken())
                    .body(requestBody)
                    .timeout(5000); // 5秒超时


            // 4. 执行请求并处理响应
            HttpResponse response = request.execute();
            if (response.isOk()) {
                log.info("请求成功！响应: {}", response.body());
                ApiResponse apiResponse = JSONUtil.toBean(response.body(), ApiResponse.class);
                if (apiResponse != null && apiResponse.isSuccess()) {
                    return apiResponse.getModel().getString("id");
                }
                return null;
            } else {
                log.error("编辑店铺失败！状态码: {}, 响应: {}", response.getStatus(), response.body());
            }
        } catch (Exception e) {
            log.error("API请求异常: ", e);
        }
        return null;
    }


    /**
     * 删除店铺（Hutool实现，与editStore风格一致）
     *
     * @param nodeId 店铺ID（路径参数）
     * @return 成功返回响应体，失败返回null
     */
    @Override
    public String deleteStore(String nodeId) {
        // 1. 参数校验（与editStore相同风格）


        try {
            // 2. 构建URL
            String url = jiefengUrl + "/openApi/gather/cloudStore/store/delete/" + nodeId;

            // 3. 创建请求（保持与editStore相同的header和超时设置）
            HttpRequest request = HttpUtil.createGet(url)
                    .header("token", this.getToken())
                    .timeout(5000); // 5秒超时

            // 4. 执行请求
            HttpResponse response = request.execute();
            if (response.isOk()) {
                log.info("请求成功！响应: {}", response.body());
                ApiResponse apiResponse = JSONUtil.toBean(response.body(), ApiResponse.class);
                if (apiResponse != null && apiResponse.isSuccess()) {
                    return apiResponse.getModel().getString("id");
                }
                return null;
            } else {
                log.error("删除店铺失败！状态码: {}, 响应: {}",
                        response.getStatus(),
                        response.body()
                );
            }
        } catch (Exception e) {
            log.error("API请求异常: ", e);
        }
        return null;
    }

    /**
     * 查询客流统计（与editStore方法风格完全一致）
     *
     * @param deviceSn  设备序列号（必填）
     * @param storeId   店铺ID（必填）
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime   结束时间（必填）
     * @return API响应JSON字符串，失败返回null
     */
    @Override
    public String queryPassengerFlow(String deviceSn, String storeId,
                                     String startTime, String endTime) {

        // 2. 构建JSON请求体（与editStore相同的JSONUtil方式）
        String requestBody = JSONUtil.createObj()
                .set("deviceSn", deviceSn)
                .set("storeId", storeId)
                .set("startTime", startTime)
                .set("endTime", endTime)
                .toString();

        try {
            // 3. 创建请求（与editStore相同的header和超时设置）
            HttpRequest request = HttpUtil.createPost(jiefengUrl + "/openApi/gather/ai/passenger/flow/queryCount")
                    .header("Content-Type", "application/json")
                    .header("token", this.getToken())
                    .body(requestBody)
                    .timeout(5000); // 5秒超时
            log.info("请求体: {}", requestBody);
            // 4. 执行请求并处理响应（与editStore相同的逻辑）
            HttpResponse response = request.execute();
            if (response.isOk()) {
                log.info("请求成功！响应: {}", response.body());
                ApiResponse apiResponse = JSONUtil.toBean(response.body(), ApiResponse.class);
                if (apiResponse != null && apiResponse.isSuccess()) {
                    return apiResponse.getModel().toJSONString();
                }
                return null;
            } else {
                log.error("查询客流失败！状态码: {}, 响应: {}",
                        response.getStatus(),
                        response.body()
                );
            }
        } catch (Exception e) {
            log.error("API请求异常: ", e);
        }
        return null;
    }

    @Override
    public void addAllStoreNode(String eid) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            List<SelectStoreDTO> selectStoreDTOList = storeMapper.selectAllStoreListNodeId(eid);
            PageHelper.clearPage();
            hasNext = selectStoreDTOList.size() >= pageSize;
            if (CollectionUtils.isEmpty(selectStoreDTOList)) {
                break;
            }
            pageNum++;
            for (SelectStoreDTO storeDTO : selectStoreDTOList) {
                String nodeId = this.addStoreNode(storeDTO.getStoreName(), storeDTO.getLocationAddress(), null, null);
                if (StringUtils.isBlank(nodeId)) {
                    log.info("添加店铺失败：{}", storeDTO.getStoreName());
                    break;
                }
                StoreDO storeDO = new StoreDO();
                storeDO.setStoreId(storeDTO.getStoreId());
                storeDO.setNodeId(nodeId);
                storeMapper.updateStore(eid, storeDO);
            }
        }
    }

    @Override
    public void getAllPassengerFlow(String eid, String beginTime, String endTime) {
        boolean hasNext = true;
        int pageSize = 200;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            List<SelectStoreDTO> selectStoreDTOList = storeMapper.selectAllStoreList(eid);
            PageHelper.clearPage();
            hasNext = selectStoreDTOList.size() >= pageSize;
            if (CollectionUtils.isEmpty(selectStoreDTOList)) {
                break;
            }
            pageNum++;
            for (SelectStoreDTO storeDTO : selectStoreDTOList) {
                log.info("查询店铺：{}, storeId:{}, beginTime:{}, endTime:{}", storeDTO.getStoreName(), storeDTO.getNodeId(), beginTime, endTime);
                String result = this.queryPassengerFlow(null, storeDTO.getNodeId(), beginTime, endTime);
                if (StringUtils.isBlank(result)) {
                    log.info("查询店铺客流为空！storeId:{}", storeDTO.getStoreId());
                    break;
                }
                PassengerFlowStatisticalThirdResp list = JSONObject.parseObject(result, PassengerFlowStatisticalThirdResp.class);
                List<PassengerFlowRecordDO> passengerFlowRecordDOList = convertDOList(list, storeDTO, beginTime, endTime);
                passengerFlowRecodeMapper.batchInsertPassengerFlowRecordDO(eid, passengerFlowRecordDOList);
            }
        }
    }

    private List<PassengerFlowRecordDO> convertDOList(PassengerFlowStatisticalThirdResp passengerData, SelectStoreDTO storeDTO, String beginTime, String endTime) {
        List<PassengerFlowRecordDO> result = new ArrayList<>();
        PassengerFlowRecordDO passengerFlowRecordDO = null;
        //一天数据
        String dateTime = "";
        passengerFlowRecordDO = new PassengerFlowRecordDO();
        dateTime = beginTime;
        FlowTypeEnum flowType = FlowTypeEnum.HOUR;
        if (!isApproximatelyOneHour(beginTime, endTime, DateUtils.DATE_FORMAT_SEC)) {
            flowType = FlowTypeEnum.DAY;

        }
        convertPassengerFlowRecordDO(dateTime, storeDTO, passengerFlowRecordDO, flowType.getCode());
        passengerFlowRecordDO.setFlowIn(passengerData.getSumInboundCount());
        passengerFlowRecordDO.setFlowOut(passengerData.getSumOutboundCount());
        passengerFlowRecordDO.setFlowInOut(passengerData.getSumInboundCount() + passengerData.getSumOutboundCount());
        passengerFlowRecordDO.setFlowPass(passengerData.getSumPassCount() + passengerData.getSumInboundCount());

        JSONObject attributeCount = new JSONObject();

        // 设置enter字段（假设sumInboundCount对应enter）
        attributeCount.put("enter", passengerData.getSumInboundCount() != null ? passengerData.getSumInboundCount() : 0);

        // 设置gender对象
        JSONObject gender = new JSONObject();
        gender.put("female", passengerData.getSumWomanCount() != null ? passengerData.getSumWomanCount() : 0);
        gender.put("male", passengerData.getSumManCount() != null ? passengerData.getSumManCount() : 0);
        gender.put("unknownGender", passengerData.getSumGenderUnknownCount() != null ? passengerData.getSumGenderUnknownCount() : 0);
        attributeCount.put("gender", gender);

        // 设置age对象
        JSONObject age = new JSONObject();
        age.put("prime", 0); // 需要根据业务逻辑映射
        age.put("middle", passengerData.getSumMiddleCount() != null ? passengerData.getSumMiddleCount() : 0);
        age.put("young", passengerData.getSumYoungCount() != null ? passengerData.getSumYoungCount() : 0);
        age.put("old", passengerData.getSumOldCount() != null ? passengerData.getSumOldCount() : 0);
        age.put("unknownAge", passengerData.getSumAgeUnknownCount() != null ? passengerData.getSumAgeUnknownCount() : 0);
        age.put("child", passengerData.getSumChildrenCount() != null ? passengerData.getSumChildrenCount() : 0);
        age.put("teenager", 0); // 需要根据业务逻辑映射
        age.put("middleAged", 0); // 需要根据业务逻辑映射
        attributeCount.put("age", age);
        passengerFlowRecordDO.setAttributeCount(attributeCount.toJSONString());
        result.add(passengerFlowRecordDO);

        return result;
    }

    public static boolean isApproximatelyOneHour(String beginTime, String endTime, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime start = LocalDateTime.parse(beginTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);

            Duration duration = Duration.between(start, end);
            long seconds = duration.getSeconds();
            return seconds >= 3599 && seconds <= 3601; // 允许±1秒的误差
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间格式解析错误: " + e.getMessage());
        }
    }


    private PassengerFlowRecordDO convertPassengerFlowRecordDO(String dateTime, SelectStoreDTO storeDTO, PassengerFlowRecordDO passengerFlowRecordDO, String flowType) {
        LocalDateTime localDateTime = DateUtils.convertStringToDate(dateTime);
        passengerFlowRecordDO.setStoreId(storeDTO.getStoreId());
        passengerFlowRecordDO.setRegionPath(storeDTO.getRegionPath());
        passengerFlowRecordDO.setDeviceId("default");
        passengerFlowRecordDO.setHasChildDevice(Boolean.FALSE);
        //默认场景是店内客流
        passengerFlowRecordDO.setSceneId(3L);
        passengerFlowRecordDO.setSceneType("store_in_out");
        passengerFlowRecordDO.setFlowType(flowType);
        passengerFlowRecordDO.setFlowYear(localDateTime.getYear());
        passengerFlowRecordDO.setFlowMonth(localDateTime.getMonthValue());
        passengerFlowRecordDO.setFlowDay(DateUtil.parse(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.DATE_FORMAT_DAY));
        passengerFlowRecordDO.setFlowHour(localDateTime.getHour());
        passengerFlowRecordDO.setCreateTime(new Date());



        return passengerFlowRecordDO;
    }


    @Data
    public static class ApiResponse {
        private boolean success;       // 请求是否成功
        private String msg;            // 返回消息
        private String code;           // 状态码
        private JSONObject model;      // 核心数据
        private Object attributes;     // 扩展字段（可为null）
        private Map<?, ?> attributesJson; // 扩展JSON数据
        private Object exception;      // 异常信息
    }

}
