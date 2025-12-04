package com.coolcollege.intelligent.service.aliyun.impl;

import com.coolcollege.intelligent.common.enums.Aliyun.PersonGroupEnum;
import com.coolcollege.intelligent.common.enums.Aliyun.PersonGroupTypeEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonGroupMapper;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonGroupMappingMapper;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonMapper;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupDO;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupMappingDO;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonGroupAddRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonGroupUpdateRequest;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunGroupVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonHistoryVO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.aliyun.AliyunPersonGroupService;
import com.coolcollege.intelligent.service.aliyun.AliyunVdsMonitorService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/26
 */
@Service
public class AliyunPersonGroupServiceImpl implements AliyunPersonGroupService {

    @Resource
    private AliyunPersonGroupMapper aliyunPersonGroupMapper;
    @Resource
    private AliyunPersonMapper aliyunPersonMapper;
    @Autowired
    private AliyunVdsMonitorService aliyunVdsMonitorService;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private AliyunPersonGroupMappingMapper aliyunPersonGroupMappingMapper;

    @Override
    public List<AliyunGroupVO> listAliyunPersonGroup(String enterpriseId) {
        List<AliyunPersonGroupDO> aliyunPersonGroupDOList = aliyunPersonGroupMapper.listAliyunPersonGroup(enterpriseId);
        if(CollectionUtils.isEmpty(aliyunPersonGroupDOList)){
            initGroup(enterpriseId);
            aliyunPersonGroupDOList = aliyunPersonGroupMapper.listAliyunPersonGroup(enterpriseId);
        }
        return ListUtils.emptyIfNull(aliyunPersonGroupDOList)
                .stream()
                .map(this::mapAliyunGroupVO)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean addAliyunPersonGroup(String eid, AliyunPersonGroupAddRequest request) {
        DataSourceHelper.changeToMy();
        if(StringUtils.isBlank(request.getGroupName())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"分组名不能为空");
        }
        AliyunPersonGroupDO aliyunPersonGroupByName = aliyunPersonGroupMapper.getAliyunPersonGroupByName(eid, request.getGroupName());
        if(aliyunPersonGroupByName!=null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"分组名已经存在");
        }
        AliyunPersonGroupDO personGroupDO = new AliyunPersonGroupDO();
        personGroupDO.setPersonGroupName(request.getGroupName());
        personGroupDO.setRemark(request.getRemark());
        personGroupDO.setCreateId(UserHolder.getUser().getUserId());
        personGroupDO.setCreateTime(System.currentTimeMillis());
        String uuid = UUIDUtils.get32UUID();
        personGroupDO.setPersonGroupId(uuid);
        personGroupDO.setIsInternal(0);
        personGroupDO.setPersonGroupType(PersonGroupTypeEnum.BASE_REQUIRED.getCode());
        DataSourceHelper.changeToMy();
        return aliyunPersonGroupMapper.insertAliyunPersonGroup(eid,personGroupDO)==1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAliyunPersonGroup(String eid, AliyunPersonGroupUpdateRequest request) {

        if(StringUtils.isBlank(request.getGroupId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"分组ID不能为空！");
        }
        if(StringUtils.isBlank(request.getGroupName())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"分组不能为空！");
        }
        AliyunPersonGroupDO aliyunPersonGroupById = aliyunPersonGroupMapper.getAliyunPersonGroupById(eid, request.getGroupId());
        if(aliyunPersonGroupById==null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"分组不存在");
        }
        AliyunPersonGroupDO aliyunPersonGroupByName = aliyunPersonGroupMapper.getAliyunPersonGroupByName(eid, request.getGroupName());
        if(aliyunPersonGroupByName!=null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"分组名已经存在！");
        }
        PersonGroupEnum byCode = PersonGroupEnum.getByCode(request.getGroupId());
        if(byCode!=null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"基础分组不允许修改！");
        }
        AliyunPersonGroupDO groupDO = new AliyunPersonGroupDO();
        groupDO.setPersonGroupId(request.getGroupId());
        groupDO.setPersonGroupName(request.getGroupName());
        groupDO.setRemark(request.getRemark());
        groupDO.setUpdateId(UserHolder.getUser().getUserId());
        groupDO.setUpdateTime(System.currentTimeMillis());
        return aliyunPersonGroupMapper.updateAliyunPersonGroup(eid, groupDO) == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAliyunPersonGroup(String eid, String groupId) {

        if(StringUtils.isBlank(groupId)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"分组Id不能为空！");
        }
        PersonGroupEnum byCode = PersonGroupEnum.getByCode(groupId);
        if(byCode!=null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),"基础分组不允许删除！");
        }
        aliyunPersonGroupMapper.deleteAliyunPersonGroup(eid,groupId);
        List<AliyunPersonGroupMappingDO> aliyunPersonGroupMappingList= aliyunPersonGroupMappingMapper.listAliyunPersonMappingByGroup(eid, groupId);
        List<String> customerIdList = ListUtils.emptyIfNull(aliyunPersonGroupMappingList)
                .stream()
                .map(AliyunPersonGroupMappingDO::getCustomerId)
                .collect(Collectors.toList());
        //停掉布控回调
        if(CollectionUtils.isNotEmpty(customerIdList)){
            List<AliyunVdsPersonHistoryVO> customerList = aliyunPersonMapper.getAliyunPersonByCustomerList(eid, customerIdList,Boolean.FALSE);
            SettingVO setting = enterpriseVideoSettingService.getSettingIncludeNull(eid, YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
            if(setting!=null&&StringUtils.isNotBlank(setting.getRootVdsCorpId())){
                asyncStopMonitor(customerList, setting.getRootVdsCorpId());
            }
            aliyunPersonMapper.deleteAliyunPerson(eid,customerIdList);
        }
        aliyunPersonGroupMappingMapper.deleteAliyunPerosnMappingGroupByGroup(eid,groupId);
        return true;
    }

    @Async
    public void asyncStopMonitor(List<AliyunVdsPersonHistoryVO> customerList, String rootVdsCorpId) {
        customerList.forEach(data->{
            if(StringUtils.isNotBlank(data.getTaskId())&&StringUtils.isNotBlank(rootVdsCorpId)){
                aliyunVdsMonitorService.stopCdrsMonitor(data.getTaskId(),rootVdsCorpId);
            }
        });
    }


    @Override
    public Boolean initGroup(String enterpriseId) {
        List<AliyunPersonGroupDO> aliyunPersonGroupDOList = aliyunPersonGroupMapper.listAliyunPersonGroup(enterpriseId);
        if(CollectionUtils.isNotEmpty(aliyunPersonGroupDOList)){
            return true;
        }
        AliyunPersonGroupDO request1 = new AliyunPersonGroupDO();
        request1.setPersonGroupName("企业会员");
        request1.setRemark("系统创建");
        request1.setPersonGroupType(PersonGroupTypeEnum.BASE_REQUIRED.getCode());
        request1.setIsInternal(1);
        request1.setPersonGroupId("1000000");
        initGroupDO(enterpriseId,request1);

        AliyunPersonGroupDO request2 = new AliyunPersonGroupDO();
        request2.setPersonGroupName("企业店员");
        request2.setRemark("系统创建");
        request2.setPersonGroupType(PersonGroupTypeEnum.BASE_REQUIRED.getCode());
        request2.setIsInternal(1);
        request2.setPersonGroupId("2000000");
        initGroupDO(enterpriseId,request2);

        AliyunPersonGroupDO request3 = new AliyunPersonGroupDO();
        request3.setPersonGroupName("黑名单");
        request3.setRemark("系统创建");
        request3.setPersonGroupType(PersonGroupTypeEnum.NOT_REQUIRED.getCode());
        request3.setIsInternal(1);
        request3.setPersonGroupId("3000000");
        initGroupDO(enterpriseId,request3);

        AliyunPersonGroupDO request4 = new AliyunPersonGroupDO();
        request4.setPersonGroupName("黄牛");
        request4.setRemark("系统创建");
        request4.setPersonGroupType(PersonGroupTypeEnum.NOT_REQUIRED.getCode());
        request4.setIsInternal(1);
        request4.setPersonGroupId("4000000");
        initGroupDO(enterpriseId,request4);

        AliyunPersonGroupDO request5 = new AliyunPersonGroupDO();
        request5.setPersonGroupName("惯偷");
        request5.setRemark("系统创建");
        request5.setPersonGroupType(PersonGroupTypeEnum.NOT_REQUIRED.getCode());
        request5.setIsInternal(1);
        request5.setPersonGroupId("5000000");
        initGroupDO(enterpriseId,request5);
        return true;
    }
    private void initGroupDO(String eid, AliyunPersonGroupDO aliyunPersonGroupDO){
         aliyunPersonGroupMapper.insertAliyunPersonGroup(eid,aliyunPersonGroupDO);

    }

    private AliyunGroupVO mapAliyunGroupVO(AliyunPersonGroupDO aliyunPersonGroupDO) {

        AliyunGroupVO vo = new AliyunGroupVO();
        vo.setGroupId(aliyunPersonGroupDO.getPersonGroupId());
        vo.setGroupName(aliyunPersonGroupDO.getPersonGroupName());
        vo.setIsInternal(aliyunPersonGroupDO.getIsInternal());
        vo.setPersonGroupType(aliyunPersonGroupDO.getPersonGroupType());
        return vo;
    }
}
