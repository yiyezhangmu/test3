package com.coolcollege.intelligent.service.device.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceAuthAppEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceAuthStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.dao.DeviceDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.mapper.device.EnterpriseAuthDeviceDAO;
import com.coolcollege.intelligent.mapper.device.EnterpriseDeviceFetchStreamLogDAO;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import com.coolcollege.intelligent.model.device.EnterpriseDeviceFetchStreamLogDO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthPageDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceCancelAuthDTO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.device.request.ElemeStoreOpenRequest;
import com.coolcollege.intelligent.model.device.request.OpenDevicePageRequest;
import com.coolcollege.intelligent.model.device.vo.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.device.DeviceAuthService;
import com.coolcollege.intelligent.service.device.auth.OpenAuthStrategyFactory;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeviceAuthServiceImpl implements DeviceAuthService {

    @Resource
    private EnterpriseAuthDeviceDAO enterpriseAuthDeviceDAO;
    @Resource
    private DeviceDao deviceDao;
    @Autowired
    private OpenAuthStrategyFactory openAuthStrategyFactory;
    @Resource
    private VideoServiceApi videoServiceApi;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private StoreDao storeDao;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Resource
    private EnterpriseDeviceFetchStreamLogDAO enterpriseDeviceFetchStreamLogDAO;


    @Override
    public DeviceAuthDetailVO getDeviceAuthDetail(String enterpriseId, String deviceId, String channelNo) {
        DataSourceHelper.reset();
        List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getAuthDeviceList(enterpriseId, deviceId, channelNo, null);
        return new DeviceAuthDetailVO(DeviceAuthAppVO.convertList(authDeviceList), DeviceAuthRecordVO.convertList(authDeviceList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deviceAuth(String enterpriseId, DeviceAuthDTO param, DeviceDO device) {
        String deviceId = param.getDeviceId(), channelNo = param.getChannelNo();
        List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getAuthDeviceList(enterpriseId, deviceId, channelNo, param.getAppId());
        if(CollectionUtils.isEmpty(authDeviceList)){
            EnterpriseAuthDeviceDO authDevice = DeviceAuthDTO.convertAuthToDO(enterpriseId, device.getBindStoreId(), param, device);
            enterpriseAuthDeviceDAO.insert(authDevice);
            return openAuthStrategyFactory.authDevice(authDevice, param.getAppId());
        }
        List<EnterpriseAuthDeviceDO> authList = authDeviceList.stream().filter(authDevice -> DeviceAuthStatusEnum.AUTH.getCode().equals(authDevice.getAuthStatus()))
                .filter(authDevice -> authDevice.getAuthEndTime().after(new Date())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(authList)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_AUTHED);
        }
        List<EnterpriseAuthDeviceDO> cancelAuthList = authDeviceList.stream().filter(authDevice -> DeviceAuthStatusEnum.AUTH.getCode().equals(authDevice.getAuthStatus()))
                .filter(authDevice -> authDevice.getAuthEndTime().before(new Date())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(cancelAuthList)){
            //更新成取消授权
            cancelAuthList.forEach(authDevice -> {
                authDevice.setAuthStatus(DeviceAuthStatusEnum.CANCEL_AUTH.getCode());
                authDevice.setCancelAuthTime(new Date());
            });
            enterpriseAuthDeviceDAO.cancelAuth(cancelAuthList);
            //调用平台接口取消授权
            openAuthStrategyFactory.cancelDevice(cancelAuthList, param.getAppId());
        }
        EnterpriseAuthDeviceDO authDevice = DeviceAuthDTO.convertAuthToDO(enterpriseId, device.getBindStoreId(), param, device);
        enterpriseAuthDeviceDAO.insert(authDevice);
        //调用平台接口授权
        return openAuthStrategyFactory.authDevice(authDevice, param.getAppId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelDeviceAuth(String enterpriseId, DeviceCancelAuthDTO param) {
        String deviceId = param.getDeviceId(), channelNo = StringUtils.isBlank(param.getChannelNo()) ? Constants.ZERO_STR : param.getChannelNo();
        List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getAuthDeviceList(enterpriseId, deviceId, channelNo, param.getAppId());
        if(CollectionUtils.isEmpty(authDeviceList)){
            throw new ServiceException(ErrorCodeEnum.NOT_AUTH_RECORD);
        }
        List<EnterpriseAuthDeviceDO> cancelAuth = authDeviceList.stream().filter(authDevice -> DeviceAuthStatusEnum.AUTH.getCode().equals(authDevice.getAuthStatus())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(cancelAuth)){
            return true;
        }
        for (EnterpriseAuthDeviceDO enterpriseAuthDeviceDO : cancelAuth) {
            enterpriseAuthDeviceDO.setAuthStatus(DeviceAuthStatusEnum.CANCEL_AUTH.getCode());
            enterpriseAuthDeviceDO.setCancelAuthTime(new Date());
        }
        enterpriseAuthDeviceDAO.cancelAuth(cancelAuth);
        //调用平台接口取消授权
        return openAuthStrategyFactory.cancelDevice(cancelAuth, param.getAppId());
    }

    @Override
    public OpenVideoUrlVO getLiveUrl(DeviceAuthAppEnum appEnum, VideoDTO param) {
        DataSourceHelper.reset();
        List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getAuthDeviceByAppId(param.getDeviceId(), param.getChannelNo(), appEnum.getAppId());
        if(CollectionUtils.isEmpty(authDeviceList)){
            throw new ServiceException(ErrorCodeEnum.NOT_AUTH_RECORD);
        }
        List<EnterpriseAuthDeviceDO> authList = authDeviceList.stream().filter(authDevice -> DeviceAuthStatusEnum.AUTH.getCode().equals(authDevice.getAuthStatus()))
                .filter(authDevice -> authDevice.getAuthEndTime().after(new Date())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(authList)){
            throw new ServiceException(ErrorCodeEnum.AUTH_EXPIRE);
        }
        EnterpriseAuthDeviceDO enterpriseAuthDeviceDO = authList.get(0);
        String enterpriseId = enterpriseAuthDeviceDO.getEnterpriseId();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        LiveVideoVO result = videoServiceApi.getLiveUrl(enterpriseId, param);
        if(Objects.isNull(result)){
            throw new ServiceException(ErrorCodeEnum.GET_LIVE_URL_ERROR);
        }
        DataSourceHelper.reset();
        //插入调用记录
        enterpriseDeviceFetchStreamLogDAO.insertLog(EnterpriseDeviceFetchStreamLogDO.convert(enterpriseAuthDeviceDO));
        return new OpenVideoUrlVO(result.getUrl(), Objects.isNull(result.getExpireTime()) ? null : String.valueOf(result.getExpireTime()));
    }

    @Override
    public boolean storeOpenPushAuthDevice(DeviceAuthAppEnum appEnum, ElemeStoreOpenRequest request) {
        DataSourceHelper.reset();
        List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getAuthDeviceByThirdStoreId(request.getThirdStoreId(), appEnum.getAppId());
        if(CollectionUtils.isEmpty(authDeviceList)){
            return false;
        }
        EnterpriseAuthDeviceDO enterpriseAuthDevice = authDeviceList.get(0);
        String enterpriseId = enterpriseAuthDevice.getEnterpriseId();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<String> deviceIdList = authDeviceList.stream().map(EnterpriseAuthDeviceDO::getDeviceId).distinct().collect(Collectors.toList());
        List<DeviceDO> deviceList = deviceDao.getDeviceByDeviceIdList(enterpriseId, deviceIdList);
        Map<String, String> deviceStatusMap = ListUtils.emptyIfNull(deviceList).stream().collect(Collectors.toMap(k -> k.getDeviceId() + Constants.MOSAICS + Constants.ZERO_STR, DeviceDO::getDeviceStatus));
        List<String> parentDeviceIdList = authDeviceList.stream().filter(o->!Constants.ZERO_STR.equals(o.getChannelNo())).map(EnterpriseAuthDeviceDO::getDeviceId).distinct().collect(Collectors.toList());
        List<DeviceChannelDO> channelList = null;
        if(CollectionUtils.isNotEmpty(parentDeviceIdList)){
            channelList = deviceChannelMapper.getByParentDeviceIds(enterpriseId, parentDeviceIdList, null);
        }
        Map<String, String> channelStatusMap = ListUtils.emptyIfNull(channelList).stream().collect(Collectors.toMap(k -> k.getDeviceId() + Constants.MOSAICS + k.getChannelNo(), DeviceChannelDO::getStatus));
        for (EnterpriseAuthDeviceDO authDevice : authDeviceList) {
            if(Constants.ZERO_STR.equals(authDevice.getChannelNo())){
                authDevice.setDeviceStatus(deviceStatusMap.get(authDevice.getDeviceId() + Constants.MOSAICS + Constants.ZERO_STR));
            }else{
                authDevice.setDeviceStatus(channelStatusMap.get(authDevice.getDeviceId() + Constants.MOSAICS + authDevice.getChannelNo()));
            }
        }
        return openAuthStrategyFactory.authDevice(authDeviceList, appEnum.getAppId());
    }

    @Override
    public void devicePush(String enterpriseId) {
        for (DeviceAuthAppEnum appEnum : DeviceAuthAppEnum.values()) {
            if(appEnum.isHidden()){
                continue;
            }
            boolean isContinue = true;
            int pageNum = Constants.ONE, pageSize = Constants.PAGE_SIZE;
            while (isContinue){
                PageHelper.startPage(pageNum, pageSize);
                List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getAuthDeviceByEnterpriseId(enterpriseId, appEnum.getAppId());
                if(CollectionUtils.isEmpty(authDeviceList)){
                    break;
                }
                if(authDeviceList.size() < pageSize){
                    isContinue = false;
                }
                openAuthStrategyFactory.authDevice(authDeviceList, appEnum.getAppId());
                pageNum++;
            }
        }

    }

    @Override
    public PageInfo getDeviceAuthPage(String enterpriseId, DeviceAuthPageDTO param, CurrentUser currentUser) {
        DataSourceHelper.reset();
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getDeviceAuthPage(enterpriseId, param);
        List<String> deviceIds = authDeviceList.stream().map(EnterpriseAuthDeviceDO::getDeviceId).distinct().collect(Collectors.toList());
        DataSourceHelper.changeToMy();
        List<DeviceDO> deviceList = deviceDao.getDeviceByDeviceIdList(enterpriseId, deviceIds);
        Map<String, DeviceDO> deviceMap = deviceList.stream().collect(Collectors.toMap(DeviceDO::getDeviceId, x -> x));
        List<String> storeIds = ListUtils.emptyIfNull(deviceList).stream().map(DeviceDO::getBindStoreId).collect(Collectors.toList());
        List<StoreDO> storeList = storeDao.getByStoreIdList(enterpriseId, storeIds);
        Map<String, StoreDO> storeMap = storeList.stream().collect(Collectors.toMap(StoreDO::getStoreId, x -> x));
        PageInfo page = new PageInfo<>(authDeviceList);
        List<DeviceAuthRecordVO> resultList = DeviceAuthRecordVO.convertList(authDeviceList);
        resultList.forEach(record -> {
            DeviceDO device = deviceMap.get(record.getDeviceId());
            if(Objects.nonNull(device)){
                StoreDO storeDO = storeMap.get(device.getBindStoreId());
                if(Objects.nonNull(storeDO)){
                    record.setStoreId(storeDO.getStoreId());
                    record.setStoreName(storeDO.getStoreName());
                }
            }
        });
        page.setList(resultList);
        return page;
    }

    @Override
    public PageInfo<OpenDevicePageVO> getDevicePage(OpenDevicePageRequest request) {
        DataSourceHelper.reset();
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<OpenDevicePageVO> resultList = enterpriseAuthDeviceDAO.getDevicePage(request);
        if(CollectionUtils.isNotEmpty(resultList)){
            for (OpenDevicePageVO device : resultList) {
                String dbName = enterpriseConfigApiService.getEnterpriseDbName(device.getEnterpriseId());
                DataSourceHelper.changeToSpecificDataSource(dbName);
                DeviceDO deviceInfo = deviceDao.getDeviceByDeviceId(device.getEnterpriseId(), device.getDeviceId());
                if(Objects.nonNull(deviceInfo)){
                    device.setDeviceName(deviceInfo.getDeviceName());
                }
            }
        }
        return new PageInfo<>(resultList);
    }
}
