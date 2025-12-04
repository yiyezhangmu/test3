package com.coolcollege.intelligent.rpc.config;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dto.*;
import com.coolcollege.intelligent.rpc.api.EnterpriseConfigServiceApi;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;


/**
 * @author: byd
 * @date: 2022-03-29 15:25
 */
@Slf4j
@Service
public class EnterpriseConfigApiService {
    @SofaReference(uniqueId = ConfigConstants.ENTERPRISE_CONFIG_API_FACADE_UNIQUE_ID,
            interfaceType = EnterpriseConfigServiceApi.class,
            binding = @SofaReferenceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE, timeout = 120000))
    private EnterpriseConfigServiceApi enterpriseConfigServiceApi;

    @Resource
    private RedisUtilPool redisUtilPool;

    public EnterpriseConfigExtendInfoDTO getServerDomain(String corpId, String appType) throws ApiException {
        log.info("rpc getServerDomain param : corpId: {}, appType:{}", corpId, appType);
        BaseResultDTO<EnterpriseConfigExtendInfoDTO> extendInfoDTOBaseResult = enterpriseConfigServiceApi.getServerDomain(corpId, appType);
        log.info("rpc getServerDomain response : {}", JSONObject.toJSONString(extendInfoDTOBaseResult));
        if (extendInfoDTOBaseResult.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(extendInfoDTOBaseResult.getResultCode()),extendInfoDTOBaseResult.getMessage());
        }
        return extendInfoDTOBaseResult.getData();
    }

    public EnterpriseConfigDTO getEnterpriseConfig(String enterpriseId) throws ApiException {
        log.info("rpc getServerDomain param : enterpriseId: {}", enterpriseId);
        String cacheKey = MessageFormat.format(RedisConstant.ENTERPRISE_CONFIG_KEY, enterpriseId);
        String value = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(value)){
            return JSONObject.parseObject(value, EnterpriseConfigDTO.class);
        }
        BaseResultDTO<EnterpriseConfigDTO> extendInfoDTOBaseResult = enterpriseConfigServiceApi.getEnterpriseConfig(enterpriseId);
        log.info("rpc getServerDomain response : {}", JSONObject.toJSONString(extendInfoDTOBaseResult));
        if (extendInfoDTOBaseResult.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(extendInfoDTOBaseResult.getResultCode()),extendInfoDTOBaseResult.getMessage());
        }
        EnterpriseConfigDTO result = extendInfoDTOBaseResult.getData();
        if(Objects.isNull(result)){
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        redisUtilPool.setString(cacheKey, JSONObject.toJSONString(result), RedisConstant.SEVEN_DAY);
        return result;
    }


    public EnterpriseDTO getEnterprise(String enterpriseId) throws ApiException {
        String cacheKey = MessageFormat.format(RedisConstant.ENTERPRISE_KEY, enterpriseId);
        String value = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(value)){
            return JSONObject.parseObject(value, EnterpriseDTO.class);
        }
        log.info("rpc getEnterprise param : enterpriseId: {}", enterpriseId);
        BaseResultDTO<EnterpriseDTO> extendInfoDTOBaseResult = enterpriseConfigServiceApi.getEnterprise(enterpriseId);
        log.info("rpc getEnterprise response : {}", JSONObject.toJSONString(extendInfoDTOBaseResult));
        if (extendInfoDTOBaseResult.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            throw new ApiException(String.valueOf(extendInfoDTOBaseResult.getResultCode()),extendInfoDTOBaseResult.getMessage());
        }
        EnterpriseDTO result = extendInfoDTOBaseResult.getData();
        redisUtilPool.setString(cacheKey, JSONObject.toJSONString(result), RedisConstant.ONE_DAY_SECONDS);
        return result;
    }


    public String getEnterpriseName(String enterpriseId) {
        String cacheKey = MessageFormat.format(RedisConstant.ENTERPRISE_NAME_KEY, enterpriseId);
        String value = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(value)){
            return value;
        }
        BaseResultDTO<EnterpriseDTO> extendInfoDTOBaseResult = enterpriseConfigServiceApi.getEnterprise(enterpriseId);
        if (extendInfoDTOBaseResult.getResultCode() != ResultCodeDTO.SUCCESS.getCode()) {
            return "";
        }
        EnterpriseDTO result = extendInfoDTOBaseResult.getData();
        if(Objects.nonNull(result)){
            redisUtilPool.setString(cacheKey, result.getName(), RedisConstant.ONE_DAY_SECONDS);
            return result.getName();
        }
        return "";
    }

    /**
     *
     * @param enterpriseId
     * @return
     * @throws ApiException
     */
    public Integer getEnterpriseLimitStoreCount(String enterpriseId) {
        EnterpriseDTO enterprise = null;
        try {
            enterprise = getEnterprise(enterpriseId);
        } catch (ApiException e) {
            log.info("远程获取失败");
        }
        return Optional.ofNullable(enterprise).map(o->o.getLimitStoreCount()).orElse(Constants.TEN);
    }

    /**
     * 获取企业dbName
     * @param enterpriseId
     * @return
     */
    public String getEnterpriseDbName(String enterpriseId) {
        String cacheKey = MessageFormat.format(RedisConstant.ENTERPRISE_DB_SERVER, enterpriseId);
        String value = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(value)){
            return value;
        }
        try {
            EnterpriseConfigDTO enterpriseConfig = getEnterpriseConfig(enterpriseId);
            if(Objects.nonNull(enterpriseConfig)){
                //缓存永不过期
                redisUtilPool.setString(cacheKey, enterpriseConfig.getDbName());
                return enterpriseConfig.getDbName();
            }
        } catch (ApiException e) {
           throw new ServiceException(ErrorCodeEnum.SERVER_ERROR);
        }
        return null;
    }
}
