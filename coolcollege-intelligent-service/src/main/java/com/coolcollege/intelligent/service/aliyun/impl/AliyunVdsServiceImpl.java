package com.coolcollege.intelligent.service.aliyun.impl;

import com.coolcollege.intelligent.common.enums.Aliyun.AliyunAgeEnum;
import com.coolcollege.intelligent.common.util.ListPageInfo;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonGroupMappingMapper;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonMapper;
import com.coolcollege.intelligent.dao.aliyun.PersonNotifyRecordMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonDO;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupMappingDO;
import com.coolcollege.intelligent.model.aliyun.PersonNotifyRecordDO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunPersonDTO;
import com.coolcollege.intelligent.model.aliyun.request.WebHookMessage;
import com.coolcollege.intelligent.model.aliyun.request.WebHookRequest;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunPersonTraceVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonHistoryVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonVO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.setting.vo.EnterpriseNoticeRoleVO;
import com.coolcollege.intelligent.model.setting.vo.EnterpriseNoticeSettingVO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreUserDTO;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.aliyun.AliyunVdsService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.setting.EnterpriseNoticeSettingService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.DateFormatUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.model.aliyun.response.VdsPersonInfo;
import com.coolcollege.intelligent.model.aliyun.response.VdsPersonResultResponse;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/14
 */
@Service
@Slf4j
public class AliyunVdsServiceImpl implements AliyunVdsService {
    @Autowired
    private AliyunService aliyunService;
    @Resource
    private StoreMapper storeMapper;
    @Autowired
    private RegionService regionService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private PersonNotifyRecordMapper personNotifyRecordMapper;

    @Resource
    private AliyunPersonMapper aliyunPersonMapper;

    @Autowired
    private RedisUtilPool redis;

    @Autowired
    private StoreService storeService;


    @Autowired
    private EnterpriseNoticeSettingService enterpriseNoticeSettingService;

    @Resource
    private AliyunPersonGroupMappingMapper aliyunPersonGroupMappingMapper;

    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public ListPageInfo<AliyunVdsPersonVO> listPerson(String eid,String storeId,Long startTime,Long endTime, Integer pageSize, Integer pageNum) {
        String vdsCorpId;
        ListPageInfo<AliyunVdsPersonVO> listPageInfo = new ListPageInfo<>();

        if(StringUtils.isBlank(storeId)){
            SettingVO setting = enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
            vdsCorpId=setting.getRootVdsCorpId();
        }else {
            StoreDTO store = storeMapper.getStoreByStoreId(eid, storeId);
            if(store==null){
                listPageInfo.setPageNum(pageNum);
                listPageInfo.setPageSize(pageSize);
                listPageInfo.setTotal(0);
                return listPageInfo;
            }
            vdsCorpId= store.getVdsCorpId();
            if(StringUtils.isBlank(vdsCorpId)){
                listPageInfo.setPageNum(pageNum);
                listPageInfo.setPageSize(pageSize);
                listPageInfo.setTotal(0);
                return listPageInfo;
            }
        }
        if(StringUtils.isBlank(vdsCorpId)){
            listPageInfo.setPageNum(pageNum);
            listPageInfo.setPageSize(pageSize);
            listPageInfo.setTotal(0);
            return listPageInfo;
        }
        VdsPersonResultResponse<VdsPersonInfo> resultResponse = aliyunService.listPersonResult(vdsCorpId, startTime,
                endTime, pageNum, pageSize);
        listPageInfo.setPageNum(pageNum);
        listPageInfo.setPageSize(pageSize);
        listPageInfo.setTotal(0);
        if(resultResponse!=null){
            listPageInfo.setTotal(resultResponse.getTotalCount());
            List<AliyunVdsPersonVO> aliyunVdsPersonVOList = ListUtils.emptyIfNull(resultResponse.getData())
                    .stream()
                    .map(this::mapAliyunVdsPersonVO)
                    .collect(Collectors.toList());
            listPageInfo.setList(aliyunVdsPersonVOList);
        }

        return listPageInfo;
    }

    @Override
    public void callBackWebhook(String eid, String customerId, WebHookRequest request) {
        DataSourceHelper.reset();
        String key = "webhook_"+eid+"_" + customerId;
        String value = redis.getString(key);
        if(StringUtils.isNotBlank(value)){
            return;
        }

        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String dbName = enterpriseConfigDO.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<WebHookMessage> webHookMessages = request.getWebHookMessages();
        if(CollectionUtils.isEmpty(webHookMessages)){
            return;
        }

        List<AliyunPersonDTO> aliyunPersonDTOList = aliyunPersonMapper.listAliyunPersonDTOByCustomerId(eid, Collections.singletonList(customerId));
        if(CollectionUtils.isEmpty(aliyunPersonDTOList)){
            return;
        }

        WebHookMessage webHookMessage = webHookMessages.get(0);

        Date shotTime = DateFormatUtil.parse(webHookMessage.getShotTime(), "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        PersonNotifyRecordDO personNotifyRecordDO = new PersonNotifyRecordDO();
        personNotifyRecordDO.setShotTime(shotTime.getTime());
        personNotifyRecordDO.setSourcePicUrl(webHookMessage.getPicUrl());
        personNotifyRecordDO.setTargetPicUrl(webHookMessage.getTargetPicUrl());
        personNotifyRecordDO.setCustomerId(customerId);
        personNotifyRecordDO.setDeviceId(webHookMessage.getGbId());
        personNotifyRecordDO.setCreateTime(System.currentTimeMillis());
        personNotifyRecordDO.setScore(webHookMessage.getScore().toString());
        AliyunPersonDO aliyunPersonDO =new AliyunPersonDO();
        String storeId=null;
        if(StringUtils.isNotBlank(webHookMessage.getGbId())){
            DeviceDO deviceDO = deviceMapper.getDeviceByDeviceId(eid,webHookMessage.getGbId());
            if(deviceDO==null){
                return;
            }
            aliyunPersonDO.setLastAppearStoreId(deviceDO.getBindStoreId());
            personNotifyRecordDO.setStoreId(storeId);
        }
        //更新人员的冗余信息
        aliyunPersonDO.setCustomerId(customerId);
        aliyunPersonDO.setLastAppearPic(webHookMessage.getPicUrl());
        aliyunPersonDO.setLastAppearTargetPic(webHookMessage.getTargetPicUrl());
        aliyunPersonDO.setLastAppearTime(shotTime.getTime());
        aliyunPersonMapper.updateAliyunPersonWebHook(eid,aliyunPersonDO);
        //回调记录的冗余分组信息
        AliyunPersonDTO aliyunPersonDTO = aliyunPersonDTOList.get(0);
        personNotifyRecordDO.setPersonGroupName(aliyunPersonDTO.getGroupName());
        boolean b = personNotifyRecordMapper.insertPersonNotifyRecord(eid, personNotifyRecordDO) == 1;
        if(b){
            redis.setString(key,eid,3600);
        }
        //发送布控通知 没有门店Id则不发送通知
        if(StringUtils.isBlank(storeId)){
           return;
        }
        StoreDTO store = storeMapper.getStoreByStoreId(eid, storeId);
        List<AliyunVdsPersonHistoryVO> customerList = aliyunPersonMapper.getAliyunPersonByCustomerList(eid,
                Collections.singletonList(customerId),Boolean.FALSE);
        if(CollectionUtils.isEmpty(customerList)){
            return;
        }
        DataSourceHelper.reset();
        List<EnterpriseNoticeSettingVO> enterpriseNoticeSettingVOList = enterpriseNoticeSettingService.listEnterpriseNotice(eid);
        if(CollectionUtils.isEmpty(enterpriseNoticeSettingVOList)){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        AliyunPersonGroupMappingDO aliyunPersonMappingByCustomer = aliyunPersonGroupMappingMapper.getAliyunPersonMappingByCustomer(eid, customerId);
        List<EnterpriseNoticeSettingVO> noticeSettingVOList = enterpriseNoticeSettingVOList.stream()
                .filter(data -> StringUtils.equals(data.getPersonGroupId(), aliyunPersonMappingByCustomer.getPersonGroupId()))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(noticeSettingVOList)){
            return;
        }
        List<Long> roleIdList = noticeSettingVOList.stream()
                .map(EnterpriseNoticeSettingVO::getRoleVOList)
                .flatMap(Collection::stream)
                .map(EnterpriseNoticeRoleVO::getRoleId)
                .collect(Collectors.toList());
        List<StoreUserDTO> storeUserPositionList = storeService.getStoreUserPositionList(eid, storeId, null, null, null, null);
        List<String> sendUserIdList = ListUtils.emptyIfNull(storeUserPositionList)
                .stream()
                .filter(data -> roleIdList.contains(data.getPositionId()))
                .map(StoreUserDTO::getUserId)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(sendUserIdList)){
            return;
        }
        //到店通知下线
//        AliyunVdsPersonHistoryVO aliyunVdsPersonHistoryVO = customerList.get(0);
//        jmsSendWebhookMessageService.sendWebhookNotify(enterpriseConfigDO.getDingCorpId(),sendUserIdList,customerId,store.getStoreName(),
//                aliyunVdsPersonHistoryVO.getName(),shotTime,aliyunPersonDTO.getGroupName(),
//                aliyunVdsPersonHistoryVO.getPicUrl(),aliyunVdsPersonHistoryVO.getGender(),aliyunVdsPersonHistoryVO.getAge(),
//                webHookMessage.getPicUrl(),webHookMessage.getTargetPicUrl());

    }

    @Override
    public List<AliyunVdsPersonHistoryVO> listPersonHistory(String eid, String storeId, Long startTime, Long endTime,
                                                            Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<AliyunVdsPersonHistoryVO> historyVOList = personNotifyRecordMapper.listPersonHistory(eid, storeId, startTime, endTime);
       if(CollectionUtils.isEmpty(historyVOList)){
           return null;
       }
        List<String> customerIdList = historyVOList
                .stream()
                .map(AliyunVdsPersonHistoryVO::getCustomerId)
                .collect(Collectors.toList());
        List<AliyunVdsPersonHistoryVO> aliyunPersonList = aliyunPersonMapper.getAliyunPersonByCustomerList(eid, customerIdList,null);
        List<String> lastStoreIdList = ListUtils.emptyIfNull(aliyunPersonList)
                .stream()
                .filter(data->StringUtils.isNotBlank(data.getLastAppearStoreId()))
                .map(AliyunVdsPersonHistoryVO::getLastAppearStoreId)
                .collect(Collectors.toList());
        List<AliyunPersonDTO> aliyunPersonGroup = aliyunPersonMapper.listAliyunPersonDTOByCustomerIdAndGroup(eid, customerIdList);
        Map<String, AliyunPersonDTO> groupMap = ListUtils.emptyIfNull(aliyunPersonGroup)
                .stream()
                .collect(Collectors.toMap(AliyunPersonDTO::getCustomerId, data -> data, (a, b) -> a));
        //填充分组信息
        historyVOList.stream()
                .forEach(vo->{
                    if(MapUtils.isNotEmpty(groupMap)) {
                        AliyunPersonDTO aliyunPersonDTO = groupMap.get(vo.getCustomerId());
                        if (aliyunPersonDTO != null) {
                            vo.setGroupName(aliyunPersonDTO.getGroupName());
                        }
                    }
                });
        //填充基本信息
        Map<String, AliyunVdsPersonHistoryVO> aliyunPersonMap = ListUtils.emptyIfNull(aliyunPersonList)
                .stream()
                .collect(Collectors.toMap(AliyunVdsPersonHistoryVO::getCustomerId, data -> data, (a, b) -> a));
        historyVOList.stream()
                .forEach(vo->{
                    if(MapUtils.isNotEmpty(aliyunPersonMap)){
                        AliyunVdsPersonHistoryVO aliyunVdsPersonHistoryVO = aliyunPersonMap.get(vo.getCustomerId());
                        if(aliyunVdsPersonHistoryVO!=null){
                            vo.setTargetPersonUrl(aliyunVdsPersonHistoryVO.getTargetPersonUrl());
                            vo.setSourcePersonUrl(aliyunVdsPersonHistoryVO.getSourcePersonUrl());
                            vo.setGender(aliyunVdsPersonHistoryVO.getGender());
                            vo.setPicUrl(aliyunVdsPersonHistoryVO.getPicUrl());
                            vo.setName(aliyunVdsPersonHistoryVO.getName());
                            vo.setAge(aliyunVdsPersonHistoryVO.getAge());
                            vo.setPersonalTag(aliyunVdsPersonHistoryVO.getPersonalTag());
                            vo.setLastAppearStoreId(aliyunVdsPersonHistoryVO.getLastAppearStoreId());
                        }
                    }
                });

        //填充门店名称
        if(CollectionUtils.isNotEmpty(lastStoreIdList)){
            List<StoreDO> storeDOList = storeMapper.getByStoreIds(eid, lastStoreIdList);
            Map<String, String> storeNameMap = ListUtils.emptyIfNull(storeDOList)
                    .stream()
                    .filter(a->a.getStoreId()!=null&&a.getStoreName()!=null)
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
            historyVOList.stream()
                    .forEach(data->{
                        if(MapUtils.isNotEmpty(storeNameMap)&&StringUtils.isNotBlank(storeNameMap.get(data.getLastAppearStoreId()))){
                            data.setStoreName(storeNameMap.get(data.getLastAppearStoreId()));
                        }
                    });
        }
        return historyVOList;
    }

    @Override
    public Integer personHistoryCount(String eid, String storeId, Long startTime, Long endTime) {

        return personNotifyRecordMapper.countPersonHistory(eid, storeId, startTime, endTime);
    }

    @Override
    public List<AliyunPersonTraceVO> listPersonTrace(String eid, String customerId,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<AliyunPersonTraceVO> personTraceVOList = personNotifyRecordMapper.listPersonTrace(eid, customerId);
        if(CollectionUtils.isEmpty(personTraceVOList)){
            return null;
        }
        List<String> storeIdList = personTraceVOList.stream()
                .map(AliyunPersonTraceVO::getStoreId)
                .collect(Collectors.toList());
        List<StoreDO> effectiveStoreByStoreIds = storeMapper.getEffectiveStoreByStoreIds(eid, storeIdList, null);
        Map<String, String> storeNameMap = ListUtils.emptyIfNull(effectiveStoreByStoreIds)
                .stream()
                .filter(a->a.getStoreId()!=null&&a.getStoreName()!=null)
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        personTraceVOList.forEach(data->{
            if(MapUtils.isNotEmpty(storeNameMap)){
                String storeName = storeNameMap.get(data.getStoreId());
                data.setStoreName(storeName);
            }
        });
        return personTraceVOList;

    }

    private AliyunVdsPersonVO mapAliyunVdsPersonVO(VdsPersonInfo vdsPersonInfo){
        AliyunVdsPersonVO vo=new AliyunVdsPersonVO();
        vo.setPersonId(vdsPersonInfo.getPersonId());
        vo.setShotTime(vdsPersonInfo.getUpdateTime().getTime());
        vo.setSourcePersonUrl(vdsPersonInfo.getSourceUrl());
        vo.setTargetPersonUrl(vdsPersonInfo.getTargetUrl());
        if(StringUtils.isNotBlank(vdsPersonInfo.getGender())){
            vo.setGender(Integer.valueOf(vdsPersonInfo.getGender()));
        }
        if(vdsPersonInfo.getAge()!=null){
            vo.setAge(AliyunAgeEnum.getByCode(vdsPersonInfo.getAge().toString()).getMsg());
        }
        return vo;
    }

}
