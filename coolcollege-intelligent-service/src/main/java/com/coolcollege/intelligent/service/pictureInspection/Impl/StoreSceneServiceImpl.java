package com.coolcollege.intelligent.service.pictureInspection.Impl;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.device.SceneTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.pictureInspection.query.StoreSceneRequest;
import com.coolcollege.intelligent.service.pictureInspection.StoreSceneService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2021/8/26 16:20
 * @Version 1.0
 */
@Service
public class StoreSceneServiceImpl implements StoreSceneService {

    @Resource
    StoreSceneMapper storeSceneMapper;

    @Resource
    DeviceMapper deviceMapper;

    @Resource
    DeviceChannelMapper deviceChannelMapper;

    @Override
    public Boolean insert(String enterpriseId, StoreSceneRequest request) {
        //新增是校验是否有改名称的场景
        StoreSceneDo storeScene = storeSceneMapper.getStoreSceneByName(enterpriseId, request.getName());
        if (storeScene!=null){
             throw new ServiceException(ErrorCodeEnum.STORE_SCENE_ADD_NAME_EXISTENT);
        }
        SceneTypeEnum byCode = SceneTypeEnum.getByCode(request.getSceneType());
        StoreSceneDo newStoreSceneDo=new StoreSceneDo();

        if(byCode==null){
            newStoreSceneDo.setSceneType(SceneTypeEnum.NOTHING.getCode());
        }else {
            newStoreSceneDo.setSceneType(byCode.getCode());
        }
        newStoreSceneDo.setRemark(request.getRemark());
        newStoreSceneDo.setName(request.getName());
        storeSceneMapper.insert(enterpriseId,newStoreSceneDo);
        return true;
    }

    @Override
    public Boolean updateSceneById(String enterpriseId, StoreSceneRequest request) {
        if (request.getId()==null){
            throw new ServiceException(ErrorCodeEnum.STORE_SCENE_ADD_ID_NOTNULL);
        }
        //新增是校验是否有改名称的场景
        StoreSceneDo storeScene = storeSceneMapper.getStoreSceneByName(enterpriseId, request.getName());
        if (storeScene!=null&&!request.getId().equals(storeScene.getId())){
            throw new ServiceException(ErrorCodeEnum.STORE_SCENE_UPDATE_NAME_EXISTENT);
        }
        SceneTypeEnum byCode = SceneTypeEnum.getByCode(request.getSceneType());
        if(byCode==null){
            request.setSceneType(SceneTypeEnum.NOTHING.getCode());
        }
        storeSceneMapper.updateSceneById(enterpriseId, request.getId(),  request.getName(),request.getSceneType());
        return true;
    }

    @Override
    public Boolean deleteById(String enterpriseId, Long storeSceneId) {
        storeSceneMapper.deleteById(enterpriseId,storeSceneId);
        return true;
    }

    @Override
    public List<StoreSceneDo> getStoreSceneList(String enterpriseId) {
        List<DeviceDO> deviceDOS = deviceMapper.selectAllDevice(enterpriseId, DeviceTypeEnum.DEVICE_VIDEO.getCode());
        Map<Long, List<DeviceDO>> deviceMap = deviceDOS.stream().filter(data->data.getStoreSceneId()!=null).collect(Collectors.groupingBy(DeviceDO::getStoreSceneId));
        List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(enterpriseId);
        if(CollectionUtils.isEmpty(storeSceneList)){
            return null;
        }
        for (StoreSceneDo storeSceneDo:storeSceneList) {
            storeSceneDo.setSceneNum(deviceMap.get(storeSceneDo.getId())!=null?deviceMap.get(storeSceneDo.getId()).size():0);
        }
        if (CollectionUtils.isEmpty(deviceDOS)){
            return storeSceneList;
        }
        List<String> DeviceIdList = deviceDOS.stream().map(DeviceDO::getDeviceId).collect(Collectors.toList());
        List<DeviceChannelDO> deviceChannelDOS = deviceChannelMapper.listDeviceChannelByDeviceId(enterpriseId, DeviceIdList, null);
        Map<Long, List<DeviceChannelDO>> deviceChanneMap = deviceChannelDOS.stream().filter(data->data.getStoreSceneId()!=null).collect(Collectors.groupingBy(DeviceChannelDO::getStoreSceneId));
        for (StoreSceneDo storeSceneDo:storeSceneList) {
            storeSceneDo.setSceneNum(storeSceneDo.getSceneNum()+(deviceChanneMap.get(storeSceneDo.getId())!=null?deviceChanneMap.get(storeSceneDo.getId()).size():0));
        }
        return storeSceneList;
    }
}
