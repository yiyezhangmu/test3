package com.coolcollege.intelligent.service.aliyun.impl;

import com.aliyuncs.vcs.model.v20200515.GetPersonListResponse;
import com.aliyuncs.vcs.model.v20200515.ListPersonVisitCountResponse;
import com.coolcollege.intelligent.common.enums.Aliyun.PersonGroupTypeEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonGroupMapper;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonGroupMappingMapper;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonDO;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupDO;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupMappingDO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunPersonDTO;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonUpdateRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunStaticPersonAddRequest;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunPersonVO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.aliyun.AliyunPersonGroupService;
import com.coolcollege.intelligent.service.aliyun.AliyunPersonService;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.aliyun.AliyunVdsMonitorService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/25
 */
@Service
@Slf4j
public class AliyunPersonServiceImpl implements AliyunPersonService {

    @Resource
    private AliyunPersonMapper aliyunPersonMapper;
    @Resource
    private AliyunPersonGroupMapper aliyunPersonGroupMapper;
    @Resource
    private AliyunPersonGroupMappingMapper aliyunPersonGroupMappingMapper;
    @Autowired
    private AliyunService aliyunService;
    @Resource
    private StoreDao storeDao;
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private AliyunPersonGroupService aliyunPersonGroupService;
    @Autowired
    private RedisUtilPool redis;
    @Autowired
    private AliyunVdsMonitorService aliyunVdsMonitorService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;


    @Override
    public Boolean addStaticAliyunPerson(String enterpriseId, AliyunStaticPersonAddRequest request) {

        AliyunPersonGroupDO aliyunPersonGroup = aliyunPersonGroupMapper.getAliyunPersonGroupById(enterpriseId, request.getGroupId());
        if (aliyunPersonGroup == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "分组不存在！");
        }
        if(StringUtils.isNotBlank(request.getPicUrl())){
            AliyunPersonDO aliyunPersonByPic = aliyunPersonMapper.getAliyunPersonByPic(enterpriseId, request.getPicUrl());
            if (aliyunPersonByPic != null) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "已经存在该人员！");
            }
        }
        //验证逻辑
        validAliyunPerson(aliyunPersonGroup,request.getName(),request.getPhone());
        CurrentUser user = UserHolder.getUser();
        String userId = user.getUserId();
        AliyunPersonDO aliyunPersonDO = new AliyunPersonDO();
        aliyunPersonDO.setName(request.getName());
        aliyunPersonDO.setPicUrl(request.getPicUrl());
        aliyunPersonDO.setBirthday(request.getBirthday());
        aliyunPersonDO.setPersonalTag(request.getPersonalTag());
        String customerId = UUIDUtils.get32UUID();
        aliyunPersonDO.setCustomerId(customerId);
        aliyunPersonDO.setAge(request.getAge());
        aliyunPersonDO.setPhone(request.getPhone());
        aliyunPersonDO.setRemark(request.getRemark());
        aliyunPersonDO.setGender(request.getGender());
        aliyunPersonDO.setWechat(request.getWechat());
        aliyunPersonDO.setEmail(request.getEmail());
        aliyunPersonDO.setCreateTime(System.currentTimeMillis());
        aliyunPersonDO.setUpdateTime(System.currentTimeMillis());
        aliyunPersonDO.setCreateId(userId);

        AliyunPersonGroupMappingDO groupMappingDO = new AliyunPersonGroupMappingDO();
        groupMappingDO.setCreateId(userId);
        groupMappingDO.setCreateTime(System.currentTimeMillis());
        groupMappingDO.setCustomerId(customerId);
        groupMappingDO.setPersonGroupId(request.getGroupId());
        //添加布控
        SettingVO setting = enterpriseVideoSettingService.getSettingIncludeNull(enterpriseId, YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
        if(setting!=null&&setting.getOpenWebHook()&&StringUtils.isNotBlank(setting.getRootVdsCorpId())){
            //人员布控功能暂时不使用
            addMonitor(enterpriseId, request.getPicUrl(), aliyunPersonDO, customerId, setting.getRootVdsCorpId());
        }
        aliyunPersonMapper.insertAliyunPerson(enterpriseId, aliyunPersonDO);
        aliyunPersonGroupMappingMapper.insertAliyunPersonMappingGroup(enterpriseId, groupMappingDO);
        return true;
    }

    private void addMonitor(String enterpriseId, String picUrl, AliyunPersonDO aliyunPersonDO, String customerId,String rootVdsCorpId) {
        String taskId= aliyunVdsMonitorService.addCdrsMonitor(rootVdsCorpId);
        aliyunVdsMonitorService.updateCdrsMonitor(enterpriseId,rootVdsCorpId,customerId,
                taskId,"ADD", Collections.singletonList(picUrl));
        //todo 关闭布控通知功能注释
        aliyunPersonDO.setTaskId(taskId);
    }

    private void validAliyunPerson(AliyunPersonGroupDO groupDO, String name, String phone){

        String personGroupType = groupDO.getPersonGroupType();
        PersonGroupTypeEnum groupTypeEnum = PersonGroupTypeEnum.getByCode(personGroupType);
        switch (groupTypeEnum){
            case NOT_REQUIRED:
                break;
            case BASE_REQUIRED:
                if(StringUtils.isBlank(name)){
                    throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "名字不能为空！");
                }
                if(StringUtils.isBlank(phone)){
                    throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "手机号码不能为空！");
                }
                break;
                default:
                    break;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAliyunPerson(String enterpriseId, AliyunPersonUpdateRequest request) {

        CurrentUser user = UserHolder.getUser();
        String userId = user.getUserId();
        AliyunPersonGroupDO aliyunPersonGroup = aliyunPersonGroupMapper.getAliyunPersonGroupById(enterpriseId, request.getGroupId());
        if (aliyunPersonGroup == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "分组不存在！");
        }
        validAliyunPerson(aliyunPersonGroup,request.getName(),request.getPhone());
        if(StringUtils.isBlank(request.getCustomerId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "顾客Id不存在！");
        }
        //如果不存在customerId 插入数据并绑定

        AliyunPersonDO aliyunPerson = aliyunPersonMapper.getAliyunPersonByCustomer(enterpriseId, request.getCustomerId());
        if (aliyunPerson == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "顾客不存在！");
        }
        //更新任务图片
        if(StringUtils.isNotBlank(request.getPicUrl())&&StringUtils.isNotBlank(aliyunPerson.getTaskId())&&
                !StringUtils.equals(request.getPicUrl(),aliyunPerson.getPicUrl())){
            SettingVO settingVO = enterpriseVideoSettingService.getSettingIncludeNull(enterpriseId, YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
            if(settingVO!=null&& settingVO.getOpenWebHook()&&StringUtils.isNotBlank(settingVO.getRootVdsCorpId())){
                aliyunVdsMonitorService.updateCdrsMonitor(enterpriseId,settingVO.getRootVdsCorpId(),aliyunPerson.getCustomerId(),aliyunPerson.getTaskId(),
                        "REPLACE", Collections.singletonList(request.getPicUrl()));
            }
        }
        //新增任务图片
        if(StringUtils.isNotBlank(request.getPicUrl())&&StringUtils.isBlank(aliyunPerson.getTaskId())){

            SettingVO settingVO = enterpriseVideoSettingService.getSettingIncludeNull(enterpriseId,YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
            if(settingVO!=null&& settingVO.getOpenWebHook()&&StringUtils.isNotBlank(settingVO.getRootVdsCorpId())){
                addMonitor(enterpriseId, request.getPicUrl(), aliyunPerson, request.getCustomerId(), settingVO.getRootVdsCorpId());
                aliyunPersonMapper.updateAliyunPersonByTaskId(enterpriseId,aliyunPerson.getTaskId(),request.getCustomerId());
            }
        }
        AliyunPersonGroupMappingDO mapping = aliyunPersonGroupMappingMapper.getAliyunPersonMappingByCustomer(enterpriseId, request.getCustomerId());
        //一个人对应一个分组的情况
        if (mapping != null) {
            AliyunPersonGroupMappingDO groupMappingDO = new AliyunPersonGroupMappingDO();
            groupMappingDO.setUpdateId(userId);
            groupMappingDO.setUpdateTime(System.currentTimeMillis());
            groupMappingDO.setCustomerId(request.getCustomerId());
            groupMappingDO.setPersonGroupId(request.getGroupId());
            aliyunPersonGroupMappingMapper.updateAliyunPersonMappingGroup(enterpriseId, groupMappingDO);
        } else {
            aliyunPersonGroupMappingMapper.deleteAliyunPerosnMappingGroupByGroup(enterpriseId, request.getGroupId());
            AliyunPersonGroupMappingDO groupMappingDO = new AliyunPersonGroupMappingDO();
            groupMappingDO.setCreateId(userId);
            groupMappingDO.setCreateTime(System.currentTimeMillis());
            groupMappingDO.setCustomerId(request.getCustomerId());
            groupMappingDO.setPersonGroupId(request.getGroupId());
            aliyunPersonGroupMappingMapper.insertAliyunPersonMappingGroup(enterpriseId, groupMappingDO);
        }
        AliyunPersonDO aliyunPersonDO = new AliyunPersonDO();
        aliyunPersonDO.setName(request.getName());
        aliyunPersonDO.setPicUrl(request.getPicUrl());
        aliyunPersonDO.setBirthday(request.getBirthday());
        aliyunPersonDO.setPersonalTag(request.getPersonalTag());
        aliyunPersonDO.setAge(request.getAge());
        aliyunPersonDO.setPhone(request.getPhone());
        aliyunPersonDO.setRemark(request.getRemark());
        aliyunPersonDO.setCustomerId(request.getCustomerId());
        aliyunPersonDO.setPhone(request.getPhone());
        aliyunPersonDO.setRemark(request.getRemark());
        aliyunPersonDO.setCustomerId(request.getCustomerId());
        aliyunPersonDO.setGender(request.getGender());
        aliyunPersonDO.setWechat(request.getWechat());
        aliyunPersonDO.setEmail(request.getEmail());
        aliyunPersonDO.setUpdateTime(System.currentTimeMillis());
        aliyunPersonMapper.updateAliyunPerson(enterpriseId, aliyunPersonDO);

        return true;
    }

    @Override
    public Boolean deleteAliyunPerson(String enterpriseId, String customerId) {

        AliyunPersonDO aliyunPerson = aliyunPersonMapper.getAliyunPersonByCustomer(enterpriseId, customerId);
        if (aliyunPerson == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "顾客不存在！");
        }
        SettingVO settingVO = enterpriseVideoSettingService.getSettingIncludeNull(enterpriseId,YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
        if(StringUtils.isNotBlank(aliyunPerson.getTaskId())&&settingVO!=null&& StringUtils.isNotBlank(settingVO.getRootVdsCorpId())){
            aliyunVdsMonitorService.stopCdrsMonitor(aliyunPerson.getTaskId(),settingVO.getRootVdsCorpId());
        }
        aliyunPersonMapper.deleteAliyunPerson(enterpriseId, Collections.singletonList(customerId));
        aliyunPersonGroupMappingMapper.deleteAliyunPerosnMappingGroupByCustomer(enterpriseId, customerId);
        return true;
    }

    @Override
    public AliyunPersonVO getAliyunPerson(String enterpriseId, String customerId) {
        AliyunPersonDO aliyunPerson=null;
        if(StringUtils.isNotBlank(customerId)){
            aliyunPerson= aliyunPersonMapper.getAliyunPersonByCustomer(enterpriseId, customerId);
        }

        if(aliyunPerson==null){
            return null;
        }
        AliyunPersonGroupMappingDO mapping = aliyunPersonGroupMappingMapper.getAliyunPersonMappingByCustomer(enterpriseId, aliyunPerson.getCustomerId());
        AliyunPersonGroupDO group = aliyunPersonGroupMapper.getAliyunPersonGroupById(enterpriseId, mapping.getPersonGroupId());
        AliyunPersonVO vo = new AliyunPersonVO();
        vo.setGroupName(group.getPersonGroupName());
        vo.setGroupId(group.getPersonGroupId());
        vo.setGroupId(group.getPersonGroupId());
        vo.setStoreId(aliyunPerson.getStoreId());
        vo.setAge(aliyunPerson.getAge());
        vo.setAliPicUrl(aliyunPerson.getAliPicUrl());
        vo.setBirthday(aliyunPerson.getBirthday());
        vo.setCreateId(aliyunPerson.getCreateId());
        vo.setCreateTime(aliyunPerson.getCreateTime());
        vo.setCustomerId(aliyunPerson.getCustomerId());
        vo.setFaceId(aliyunPerson.getFaceId());
        vo.setFirstAppearTime(aliyunPerson.getFirstAppearTime());
        vo.setName(aliyunPerson.getName());
        vo.setPhone(aliyunPerson.getPhone());
        vo.setPicUrl(aliyunPerson.getPicUrl());
        vo.setRemark(aliyunPerson.getRemark());
        vo.setUpdateId(aliyunPerson.getUpdateId());
        vo.setUpdateTime(aliyunPerson.getUpdateTime());
        vo.setPersonalTag(aliyunPerson.getPersonalTag());

        vo.setLastAppearTime(aliyunPerson.getLastAppearTime());
        vo.setLastAppearPic(aliyunPerson.getLastAppearPic());
        vo.setLastAppearTargetPic(aliyunPerson.getLastAppearTargetPic());
        vo.setLastAppearStoreId(aliyunPerson.getLastAppearStoreId());
        vo.setGender(aliyunPerson.getGender());
        vo.setEmail(aliyunPerson.getEmail());
        vo.setWechat(aliyunPerson.getWechat());

        return vo;
    }

    @Override
    public Object listAliyunPerson(String enterpriseId, String groupId, Integer pageSize, Integer pageNumber, String keywords) {

        PageHelper.startPage(pageNumber, pageSize);
        List<AliyunPersonDTO> aliyunPersonDTOList = aliyunPersonMapper.listAliyunPersonDTO(enterpriseId, groupId, keywords);
        List<AliyunPersonVO> aliyunPersonVOList = ListUtils.emptyIfNull(aliyunPersonDTOList)
                .stream()
                .map(this::mapAliyunPersonVO)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(aliyunPersonDTOList)) {
            Map<String, Object> pageInfo = PageHelperUtil.getPageInfo(new PageInfo<>(aliyunPersonDTOList));
            pageInfo.put("list", aliyunPersonVOList);
            return pageInfo;
        }
        return PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>()));
    }




    private AliyunPersonVO mapAliyunPersonVO(ListPersonVisitCountResponse.Datas data,
                                             Map<String, GetPersonListResponse.Data.RecordsItem> recordsItemMap,
                                             Map<String, AliyunPersonDTO> personMap,
                                             Map<String, AliyunPersonDTO> matchCustomerDTOMap
    ) {
        AliyunPersonVO vo = new AliyunPersonVO();
        vo.setFaceId(data.getPersonId());
        vo.setStoreVisitCount(Long.valueOf(data.getTagMetrics()));
        if (MapUtils.isNotEmpty(recordsItemMap) && recordsItemMap.get(data.getPersonId()) != null) {
            GetPersonListResponse.Data.RecordsItem recordsItem = recordsItemMap.get(data.getPersonId());
            vo.setAliPicUrl(recordsItem.getFaceUrl());
            vo.setFirstAppearTime(recordsItem.getFirstShotTime());
            vo.setFaceId(data.getPersonId());
            vo.setAliPicUrl(recordsItem.getFaceUrl());
            vo.setRecentAppearTime(recordsItem.getLastShotTime());
        }
        if (MapUtils.isNotEmpty(matchCustomerDTOMap) && matchCustomerDTOMap.get(data.getPersonId()) != null) {
            AliyunPersonDTO aliyunPersonDTO = matchCustomerDTOMap.get(data.getPersonId());
            vo.setGroupName(aliyunPersonDTO.getGroupName());
            vo.setStoreId(aliyunPersonDTO.getStoreId());
            vo.setAge(aliyunPersonDTO.getAge());
            vo.setBirthday(aliyunPersonDTO.getBirthday());
            vo.setCreateId(aliyunPersonDTO.getCreateId());
            vo.setCreateTime(aliyunPersonDTO.getCreateTime());
            vo.setCustomerId(aliyunPersonDTO.getCustomerId());
            vo.setFaceId(aliyunPersonDTO.getFaceId());
            vo.setFirstAppearTime(aliyunPersonDTO.getFirstAppearTime());
            vo.setName(aliyunPersonDTO.getName());
            vo.setPhone(aliyunPersonDTO.getPhone());
            vo.setPicUrl(aliyunPersonDTO.getPicUrl());
            vo.setRemark(aliyunPersonDTO.getRemark());
            vo.setUpdateId(aliyunPersonDTO.getUpdateId());
            vo.setUpdateTime(aliyunPersonDTO.getUpdateTime());
            vo.setPersonalTag(aliyunPersonDTO.getPersonalTag());
        }
        if (MapUtils.isNotEmpty(personMap) && personMap.get(data.getPersonId()) != null) {
            AliyunPersonDTO aliyunPersonDTO = personMap.get(data.getPersonId());
            vo.setGroupId(aliyunPersonDTO.getGroupId());
            vo.setGroupName(aliyunPersonDTO.getGroupName());
            vo.setStoreId(aliyunPersonDTO.getStoreId());
            vo.setAge(aliyunPersonDTO.getAge());
            vo.setBirthday(aliyunPersonDTO.getBirthday());
            vo.setCreateId(aliyunPersonDTO.getCreateId());
            vo.setCreateTime(aliyunPersonDTO.getCreateTime());
            vo.setCustomerId(aliyunPersonDTO.getCustomerId());
            vo.setFaceId(aliyunPersonDTO.getFaceId());
            vo.setFirstAppearTime(aliyunPersonDTO.getFirstAppearTime());
            vo.setName(aliyunPersonDTO.getName());
            vo.setPhone(aliyunPersonDTO.getPhone());
            vo.setPicUrl(aliyunPersonDTO.getPicUrl());
            vo.setRemark(aliyunPersonDTO.getRemark());
            vo.setUpdateId(aliyunPersonDTO.getUpdateId());
            vo.setUpdateTime(aliyunPersonDTO.getUpdateTime());
            vo.setPersonalTag(aliyunPersonDTO.getPersonalTag());
        }
        return vo;
    }

    @Override
    public Object bindAliyunDynamicPersonList(String enterpriseId, Integer pageSize, Integer pageNumber, String keywords) {

        PageHelper.startPage(pageNumber, pageSize);
        List<AliyunPersonDTO> aliyunPersonDTOList = aliyunPersonMapper.listAliyunPersonDTOByVipAndUnbind(enterpriseId, keywords);
        List<AliyunPersonVO> aliyunPersonVOList = ListUtils.emptyIfNull(aliyunPersonDTOList)
                .stream()
                .map(this::mapAliyunPersonVO)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(aliyunPersonDTOList)) {
            Map<String, Object> pageInfo = PageHelperUtil.getPageInfo(new PageInfo<>(aliyunPersonDTOList));
            pageInfo.put("list", aliyunPersonVOList);
            return pageInfo;
        }
        return PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>()));

    }

    @Override
    public Object searchStatic(String enterpriseId, Integer pageSize, Integer pageNumber) {
        return null;
    }


    private AliyunPersonVO mapAliyunPersonVO(AliyunPersonDTO aliyunPersonDTO) {

        AliyunPersonVO vo = new AliyunPersonVO();
        vo.setGroupName(aliyunPersonDTO.getGroupName());
        vo.setStoreId(aliyunPersonDTO.getStoreId());
        vo.setAge(aliyunPersonDTO.getAge());
        vo.setGroupId(aliyunPersonDTO.getGroupId());
        vo.setAliPicUrl(aliyunPersonDTO.getAliPicUrl());
        vo.setBirthday(aliyunPersonDTO.getBirthday());
        vo.setCreateId(aliyunPersonDTO.getCreateId());
        vo.setCreateTime(aliyunPersonDTO.getCreateTime());
        vo.setCustomerId(aliyunPersonDTO.getCustomerId());
        vo.setFaceId(aliyunPersonDTO.getFaceId());
        vo.setFirstAppearTime(aliyunPersonDTO.getFirstAppearTime());
        vo.setName(aliyunPersonDTO.getName());
        vo.setPhone(aliyunPersonDTO.getPhone());
        vo.setPicUrl(aliyunPersonDTO.getPicUrl());
        vo.setRemark(aliyunPersonDTO.getRemark());
        vo.setUpdateId(aliyunPersonDTO.getUpdateId());
        vo.setUpdateTime(aliyunPersonDTO.getUpdateTime());
        vo.setPersonalTag(aliyunPersonDTO.getPersonalTag());
        vo.setGender(aliyunPersonDTO.getGender());
        vo.setEmail(aliyunPersonDTO.getEmail());
        vo.setWechat(aliyunPersonDTO.getWechat());
        return vo;
    }
}
