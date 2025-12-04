package com.coolcollege.intelligent.service.enterprise.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseOpenLeaveInfoMapper;
import com.coolcollege.intelligent.dto.AuthPersonLeaveInfoDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOpenLeaveInfoDO;
import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOpenLeaveInfoService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AppTypeEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.coolstore.base.enums.AppTypeEnum.FEI_SHU;
import static com.coolstore.base.enums.AppTypeEnum.WX_APP2;

/**
 * @author wxp
 * @since 2022/8/17
 */
@Service
@Slf4j
public class EnterpriseOpenLeaveInfoServiceImpl implements EnterpriseOpenLeaveInfoService {

    @Resource
    EnterpriseOpenLeaveInfoMapper enterpriseOpenLeaveInfoMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Autowired
    public EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Override
    public EnterpriseOpenLeaveInfoDO saveEnterpriseOpenLeaveInfo(String enterpriseId, String authCode, CurrentUser user) {
        log.info("用户授权留资入参:{}", enterpriseId);
        EnterpriseOpenLeaveInfoDO checkDumpObj = enterpriseOpenLeaveInfoMapper.selectByEnterpriseId(enterpriseId);
        if(checkDumpObj != null){
            log.info("留资信息已存在:{}", enterpriseId);
            return checkDumpObj;
        }
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);

        EnterpriseOpenLeaveInfoDO enterpriseOpenLeaveInfoDO = new EnterpriseOpenLeaveInfoDO();
        AuthPersonLeaveInfoDTO authPersonLeaveInfo = null;
        String userId = (user != null) ? user.getUserId() : null;
        try {
            authPersonLeaveInfo = enterpriseInitConfigApiService.getAuthPersonLeaveInfo(enterpriseConfig.getDingCorpId(), userId, enterpriseDO.getAppType(), authCode);
        } catch (ApiException e) {
            throw new ServiceException(ErrorCodeEnum.DING_USERINFO_BYTOKEN_EXCEPTION);
        }
        enterpriseOpenLeaveInfoDO.setName(authPersonLeaveInfo.getNick());
        enterpriseOpenLeaveInfoDO.setMobile(authPersonLeaveInfo.getMobile());
        enterpriseOpenLeaveInfoDO.setCreateTime(new Date());
        enterpriseOpenLeaveInfoDO.setCorpId(enterpriseConfig.getDingCorpId());
        enterpriseOpenLeaveInfoDO.setCorpName(enterpriseDO.getName());
        enterpriseOpenLeaveInfoDO.setAppType(enterpriseDO.getAppType());
        enterpriseOpenLeaveInfoDO.setEnterpriseId(enterpriseDO.getId());
        enterpriseOpenLeaveInfoMapper.insertSelective(enterpriseOpenLeaveInfoDO);
        enterpriseService.updateEnterpriseIsLeaveInfo(enterpriseDO.getId());
        // 留资后清楚缓存
        redisUtilPool.hashDel(RedisConstant.LEAVE_ENTERPRISE, enterpriseDO.getId());
        return enterpriseOpenLeaveInfoDO;
    }

    @Override
    public EnterpriseOpenLeaveInfoDO saveEnterpriseOpenLeaveInfoByFsAndQw(String enterpriseId, String phoneNum, CurrentUser user,String smsCode) {
        log.info("用户授权留资入参,enterpriseId:{},phoneNum:{},user:{},smsCode:{}", enterpriseId,phoneNum, JSONObject.toJSONString(user),smsCode);
        String smsCodeKey = SmsCodeTypeEnum.LOGIN + ":"+ phoneNum;
        String code = redisUtilPool.getString(smsCodeKey);
        if(StringUtils.isBlank(code)){
            throw new ServiceException(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if(!smsCode.equals(code)){
            throw new ServiceException(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        EnterpriseOpenLeaveInfoDO checkDumpObj = enterpriseOpenLeaveInfoMapper.selectByEnterpriseId(enterpriseId);
        if(checkDumpObj != null){
            log.info("留资信息已存在:{}", enterpriseId);
            return checkDumpObj;
        }
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        EnterpriseOpenLeaveInfoDO enterpriseOpenLeaveInfoDO = new EnterpriseOpenLeaveInfoDO();
        enterpriseOpenLeaveInfoDO.setName(user.getName());
        enterpriseOpenLeaveInfoDO.setMobile(phoneNum);
        enterpriseOpenLeaveInfoDO.setCreateTime(new Date());
        enterpriseOpenLeaveInfoDO.setCorpId(enterpriseConfig.getDingCorpId());
        enterpriseOpenLeaveInfoDO.setCorpName(enterpriseDO.getName());
        enterpriseOpenLeaveInfoDO.setAppType(enterpriseDO.getAppType());
        enterpriseOpenLeaveInfoDO.setEnterpriseId(enterpriseDO.getId());
        enterpriseOpenLeaveInfoMapper.insertSelective(enterpriseOpenLeaveInfoDO);
        enterpriseService.updateEnterpriseIsLeaveInfo(enterpriseDO.getId());
        // 留资后清楚缓存
        redisUtilPool.hashDel(RedisConstant.LEAVE_ENTERPRISE, enterpriseDO.getId());
        return enterpriseOpenLeaveInfoDO;
    }

    /**
     * 检查用户是否需要弹框授权留资
     * @param enterpriseId
     * @param user
     * @param enterpriseDO
     * @return
     */
    @Override
    public boolean checkUserLeaveInfo(String enterpriseId, CurrentUser user, EnterpriseDO enterpriseDO, String appType) {
        log.info("进入checkUserLeaveInfo...........");
        // 留资开关
        String leaveOpenValue = redisUtilPool.getString(RedisConstant.LEAVE_OPEN);
        // 是否需要留资企业
        String leaveEnterpriseValue = redisUtilPool.hashGet(RedisConstant.LEAVE_ENTERPRISE, enterpriseId);
        log.info("checkUserLeaveInfo leaveOpenValue：{},leaveEnterpriseValue:{}", JSONObject.toJSONString(leaveOpenValue),JSONObject.toJSONString(leaveEnterpriseValue));
        if(StringUtils.isNotBlank(leaveOpenValue) && StringUtils.isNotBlank(leaveEnterpriseValue)){
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)){
                log.info("检查门店通企业是否需要弹框授权留资,leaveOpenValue:{},leaveEnterpriseValue:{}",leaveOpenValue, leaveEnterpriseValue);
                return true;
            }else if(AppTypeEnum.DING_DING2.getValue().equals(appType)){
                // 管理员 或 开通人
                boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
                boolean isOpenUser = enterpriseDO != null && user.getUserId().equals(enterpriseDO.getAuthUserId());
                log.info("检查用户是否需要弹框授权留资,leaveOpenValue:{},leaveEnterpriseValue:{},isAdmin:{},isOpenUser:{}",leaveOpenValue, leaveEnterpriseValue, isAdmin, isOpenUser);
                if(isAdmin || isOpenUser){
                    return true;
                }
            }else if (AppTypeEnum.ONE_PARTY_APP2.getValue().equals(appType)){
                log.info("检查门店通企业是否需要弹框授权留资,leaveOpenValue:{},leaveEnterpriseValue:{}",leaveOpenValue, leaveEnterpriseValue);
                return true;
            }
        }
        if (WX_APP2.getValue().equals(appType)){
            String qwStatus = redisUtilPool.getString("fsAndQwRetainCapital_qw2_" + enterpriseId);
            log.info("checkUserLeaveInfo fsStatus：{}",JSONObject.toJSONString(qwStatus));
            if (!"firstLogin".equals(qwStatus)){
                String fsAndQwRetainCapital_qw2 = redisUtilPool.getString("fsAndQwRetainCapital_qw2");
                if (fsAndQwRetainCapital_qw2.equals("true")){
                    boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
                    boolean isOpenUser = enterpriseDO != null && user.getUserId().equals(enterpriseDO.getAuthUserId());
                    //第一次进入时载入缓存
                    redisUtilPool.setString("fsAndQwRetainCapital_qw2_"+enterpriseId,"firstLogin");
                    if(isAdmin || isOpenUser){
                        return true;
                    }
            }
            }else {
                log.info("该企业不是第一次登录状态，enterpriseId：{}",enterpriseId);
                return false;
            }
        }else if (FEI_SHU.getValue().equals(appType)){
            String fsStatus = redisUtilPool.getString("fsAndQwRetainCapital_fei_shu" + enterpriseId);
            log.info("checkUserLeaveInfo fsStatus:{}",JSONObject.toJSONString(fsStatus));
            if (!"firstLogin".equals(fsStatus)){
                String fsAndQwRetainCapital_fei_shu = redisUtilPool.getString("fsAndQwRetainCapital_fei_shu");
                if (fsAndQwRetainCapital_fei_shu.equals("true")){
                    boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
                    boolean isOpenUser = enterpriseDO != null && user.getUserId().equals(enterpriseDO.getAuthUserId());
                    redisUtilPool.setString("fsAndQwRetainCapital_fei_shu"+enterpriseId,"firstLogin");
                    if(isAdmin || isOpenUser){
                        return true;
                    }
            }
            }else {
                return false;
            }
        }
        return false;
    }

    @Override
    public List<EnterpriseOpenLeaveInfoDO> listByEnterpriseIds(List<String> enterpriseIds) {
        if (CollectionUtils.isEmpty(enterpriseIds)) {
            return Collections.emptyList();
        }
        return enterpriseOpenLeaveInfoMapper.listByEnterpriseIds(enterpriseIds);
    }

}
