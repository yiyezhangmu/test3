package com.coolcollege.intelligent.service.coolrelation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.EnterpriseApiErrorEnum;
import com.coolcollege.intelligent.common.enums.PlatFormApiErrorEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.coolrelation.dto.AdvertDTO;
import com.coolcollege.intelligent.model.coolrelation.dto.CoolInfoDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserToken;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.coolrelation.CoolCollegeRelationService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.proxy.PlatformApiProxy;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2021/5/10 11:11
 */
@Service
@Slf4j
public class CoolCollegeRelationServiceImpl implements CoolCollegeRelationService {

    @Autowired
    private RedisUtilPool redis;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private PlatformApiProxy platformApiProxy;

    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;

    @Override
    public Boolean getAdvertSetting(String eid, String platFormType, String type) {
        String setting = redis.getString(splicingKey(eid, platFormType));
        if (StringUtils.isBlank(type)) {
            return setting == null;
        }
        if (RedisConstant.ADVERT_SETTING_VALUE.equals(setting) || StringUtils.isBlank(setting)) {
            return true;
        } else {
            JSONObject json = JSONObject.parseObject(setting);
            return StringUtils.isBlank(json.getString(type));
        }
    }

    @Override
    public void setAdvertSetting(String eid, AdvertDTO advertDTO) {
        String setting = redis.getString(splicingKey(eid, advertDTO.getPlatFormType()));
        JSONObject json;
        if (RedisConstant.ADVERT_SETTING_VALUE.equals(setting) || StringUtils.isBlank(setting)) {
            json = new JSONObject();
        } else {
            json = JSONObject.parseObject(setting);
        }
        if (StringUtils.isBlank(advertDTO.getType())) {
            redis.setString(splicingKey(eid, advertDTO.getPlatFormType()), RedisConstant.ADVERT_SETTING_VALUE);
            return;
        }
        json.put(advertDTO.getType(), RedisConstant.ADVERT_SETTING_VALUE);
        redis.setString(splicingKey(eid, advertDTO.getPlatFormType()), json.toJSONString());

    }

    @Override
    public Pair<CoolInfoDTO, PlatFormApiErrorEnum> getCoolToken(String enterpriseId, String corpId, String userId, String appType) {
        String key = RedisConstant.COOL_TOKEN_PREFIX + corpId + "_" + userId;
        String token = redis.getString(key);
        if (StringUtils.isNoneEmpty(token)) {
            return Pair.of(JSON.parseObject(token, CoolInfoDTO.class), null);
        }
        Pair<UserToken, PlatFormApiErrorEnum> pari = Pair.of(null, null);
        if (AppTypeEnum.isCoolCollege(appType)) {
            DataSourceHelper.changeToMy();
            EnterpriseUserDO user = enterpriseUserDao.selectByUserId(enterpriseId, userId);
            DataSourceHelper.reset();
            //存在平台库中用户的userId和业务库中用户的userId不一样的情况，需要查询两次
            if (user == null) {
                user = enterpriseUserService.selectConfigUserByUserId(userId);
            }
            if (user == null) {
                throw new ServiceException("找不到该用户");
            }
            pari = platformApiProxy.createToken(user.getUnionid(), corpId);
        } else {
            String tempCode = coolCollegeIntegrationApiService.getLoginCoolCollegeTicket(userId, enterpriseId);
            pari = platformApiProxy.createThirdOaToken(tempCode);
        }

        if (pari != null && pari.getKey() != null) {
            CurrentUser coolUser = pari.getKey().getUser();
            CoolInfoDTO coolInfoDTO = new CoolInfoDTO(pari.getKey().getActionToken(), coolUser.getId(), coolUser.getEnterpriseId());
            redis.setString(key, JSON.toJSONString(coolInfoDTO), pari.getKey().getExpire());
            return Pair.of(coolInfoDTO, pari.getValue());
        }
        if (pari == null) {
            return null;
        }
        return Pair.of(null, pari.getValue() != null ? pari.getValue() : null);
    }

    @Override
    public String checkAndRefreshToken(EnterpriseApiErrorEnum enterpriseApiErrorEnum, CurrentUser user){
        //是否失效
        String newToken = null;
        if(enterpriseApiErrorEnum != null && (EnterpriseApiErrorEnum.INVALID_TOKEN_NEW.getCode().equals(enterpriseApiErrorEnum.getCode()) ||
                EnterpriseApiErrorEnum.INVALID_TOKEN.getCode().equals(enterpriseApiErrorEnum.getCode()))){
            String key = RedisConstant.COOL_TOKEN_PREFIX + user.getDingCorpId() + "_" + user.getUserId();
            //门店token没过期,酷学院token失效的情况
            if(redis.getString(key) == null){
                return null;
            }
            Pair<UserToken, PlatFormApiErrorEnum> pari = platformApiProxy.createToken(user.getUnionid(),user.getDingCorpId());
            if (pari != null && pari.getKey() != null) {
                CurrentUser coolUser = pari.getKey().getUser();
                CoolInfoDTO coolInfoDTO = new CoolInfoDTO(pari.getKey().getActionToken(), coolUser.getId(), coolUser.getEnterpriseId());
                newToken = coolInfoDTO.getActionToken();
                redis.setString(key, JSON.toJSONString(coolInfoDTO), pari.getKey().getExpire());
            }
        }
        return newToken;
    }

    private String splicingKey(String eid, String platFormType) {
        return RedisConstant.ADVERT_SETTING_PREFIX + platFormType + "_" + eid;
    }

    private String splicingKey(String prefix, String ... keys) {
        StringBuilder sb = new StringBuilder(prefix);
        for (String key : keys) {
            sb.append("_")
                    .append(keys);
        }
        return sb.toString();
    }
}
