package com.coolcollege.intelligent.service.oneparty.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.OnePartySetMealEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.device.dao.DeviceDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaTableDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.sop.dao.TaskSopDao;
import com.coolcollege.intelligent.dto.OpPackageDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.oneparty.dto.OnePartyBusinessRestrictionsDTO;
import com.coolcollege.intelligent.model.question.dto.TbQuestionRecordSearchDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.oneparty.OnePartyService;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Optional;

import static com.coolcollege.intelligent.common.constant.OnePartyConstants.*;

/**
 * 门店通工具类
 * @author zhangnan
 * @date 2022-06-08 13:58
 */
@Service
public class OnePartyServiceImpl implements OnePartyService {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TaskSopDao taskSopDao;
    @Resource
    private TbMetaTableDao metaTableDao;
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Override
    public OnePartyBusinessRestrictionsDTO getBusinessRestrictions(String eid, String businessCode) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigDao.getEnterpriseConfig(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        OnePartyBusinessRestrictionsDTO businessRestrictionsDTO = new OnePartyBusinessRestrictionsDTO();
        // 获取一方品套餐信息，付费套餐不做数量过滤
        OpPackageDTO setMealInfo = this.getSetMealInfo(enterpriseConfigDO.getDingCorpId());
        OnePartySetMealEnum onePartySetMealEnum = OnePartySetMealEnum.getByVersion(setMealInfo.getRightsCode());
        businessRestrictionsDTO.setIsAvailable(Boolean.TRUE);
        businessRestrictionsDTO.setSetMealVersion(onePartySetMealEnum.getVersion());
        switch (businessCode) {
            case SET_MEAL_SOP:
                businessRestrictionsDTO.setAll(onePartySetMealEnum.getSopQuantity());
                businessRestrictionsDTO.setUsed(taskSopDao.count(eid));
                break;
            case SET_MEAL_META_COLUMN_PROPERTIES:
                businessRestrictionsDTO.setAvailableValue(onePartySetMealEnum.getMetaColumnProperties());
                businessRestrictionsDTO.setAll(0);
                businessRestrictionsDTO.setUsed(0);
                break;
            case SET_MEAL_META_TABLE:
                businessRestrictionsDTO.setAll(onePartySetMealEnum.getMetaTableQuantity());
                businessRestrictionsDTO.setUsed(metaTableDao.count(eid, MetaTableConstant.TableTypeConstant.STANDARD, MetaColumnStatusEnum.NORMAL.getStatus()));
                break;
            case SET_MEAL_META_TABLE_PROPERTIES:
                businessRestrictionsDTO.setAvailableValue(onePartySetMealEnum.getMetaTableProperties());
                businessRestrictionsDTO.setAll(0);
                businessRestrictionsDTO.setUsed(0);
                break;
            case SET_MEAL_QUESTION_RECORD:
                businessRestrictionsDTO.setAll(onePartySetMealEnum.getQuestionRecordQuantity());
                TbQuestionRecordSearchDTO searchParam = new TbQuestionRecordSearchDTO();
                searchParam.setEnterpriseId(eid);
                businessRestrictionsDTO.setUsed(questionRecordDao.countQuestionRecords(searchParam).intValue());
                break;
            case SET_MEAL_PATROL_PICTURE:
                businessRestrictionsDTO.setAll(onePartySetMealEnum.getSelfCheckPictureQuantity());
                businessRestrictionsDTO.setUsed(Constants.ZERO);
                break;
            case SET_MEAL_STORE_DEVICE:
                // 门店设备数量限制：门店限制数*单个门店设备数
                Integer storeQuan = Optional.ofNullable(setMealInfo.getQuantity()).orElse(Constants.ZERO);
                businessRestrictionsDTO.setAll(onePartySetMealEnum.getSingleStoreDeviceQuantity() * storeQuan);
                businessRestrictionsDTO.setUsed(deviceDao.count(eid));
                break;
            default:
                break;
        }
        if(StringUtils.isNotBlank(businessRestrictionsDTO.getAvailableValue())) {
            return businessRestrictionsDTO;
        }
        businessRestrictionsDTO.setAvailable(businessRestrictionsDTO.getAll() - businessRestrictionsDTO.getUsed());
        businessRestrictionsDTO.setIsAvailable(businessRestrictionsDTO.getAvailable() > Constants.ZERO);
        return businessRestrictionsDTO;
    }


    @Override
    public void checkStoreQuantity(String eid, String corpId, String storeId) {
        /**
         * 校验暂时去掉， 后续考虑要不要加
         */
        /*// 获取一方品套餐信息，状态
        OpPackageDTO setMealInfo = this.getSetMealInfo(corpId);
        // 免费版没有门店数量限制
        if(setMealInfo.getRightsCode().equals(OnePartySetMealEnum.FREE.getVersion())) {
            return;
        }
        // 获取一方品套餐规格门店数量
        Integer orderInfoStoreQuan = setMealInfo.getQuantity();
        // 获取当月巡店数量
        String patrolStoreCacheKey = MessageFormat.format(CACHE_KEY_PATROL_STORE_SET, eid, LocalDate.now().getMonthValue());
        Long currentMonthPatrolStoreQuan = redisUtil.setSize(patrolStoreCacheKey);
        if(currentMonthPatrolStoreQuan >= orderInfoStoreQuan) {
            // 巡店数量超出套餐数量, 且当月没有巡店
            if(!redisUtil.setIsMember(patrolStoreCacheKey, storeId)) {
                throw new ServiceException(ErrorCodeEnum.OP_9020001,orderInfoStoreQuan);
            }
        }
        redisUtil.setAdd(patrolStoreCacheKey, storeId);
        // 清除上个月的巡店数
        if(currentMonthPatrolStoreQuan <= Constants.LONG_ONE) {
            patrolStoreCacheKey = MessageFormat.format(CACHE_KEY_PATROL_STORE_SET, eid,
                    LocalDate.now().minusMonths(Constants.LONG_ONE).getMonthValue());
            redisUtil.delete(patrolStoreCacheKey);
        }*/
    }

    /**
     * 获取套餐信息
     * @param corpId
     * @return
     */
    private OpPackageDTO getSetMealInfo(String corpId) {
        String setMealCacheKey = MessageFormat.format(CACHE_KEY_OP_SET_MEAL_INFO, corpId);
        String setMeal = redisUtilPool.getString(setMealCacheKey);
        if(StringUtils.isNotBlank(setMeal)) {
            OpPackageDTO setMealInfo = JSONObject.parseObject(setMeal, OpPackageDTO.class);
            // 免费版或者套餐未过期，直接返回redis中的套餐数据
            if(OnePartySetMealEnum.FREE.getVersion().equals(setMealInfo.getRightsCode())
                    || System.currentTimeMillis() < setMealInfo.getEndTime()) {
                return setMealInfo;
            }
        }
        // redis中没有缓存或者套餐过期，调用开放平台接口获取
        try {
            return enterpriseInitConfigApiService.getPackage(corpId, AppTypeEnum.ONE_PARTY_APP.getValue());
        } catch (ApiException e) {
            throw new ServiceException(ErrorCodeEnum.OP_9020002);
        }
    }
}
