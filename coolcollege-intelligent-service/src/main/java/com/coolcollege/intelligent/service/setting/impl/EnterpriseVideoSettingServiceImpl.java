package com.coolcollege.intelligent.service.setting.impl;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceSyncStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.StringUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.setting.EnterpriseVideoSettingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.setting.EnterpriseVideoSettingDO;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/01
 */
@Service
@Slf4j
public class EnterpriseVideoSettingServiceImpl implements EnterpriseVideoSettingService {

    @Resource
    private EnterpriseVideoSettingMapper enterpriseVideoSettingMapper;
    @Autowired
    private RedisUtilPool redisUtil;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Value("${aliyun.vcs.corp.id}")
    private String vcsCorpId;

    @Override
    public Boolean addEnterpriseVideoSetting(EnterpriseVideoSettingDTO enterpriseVideoSettingDTO) {
        EnterpriseVideoSettingDO enterpriseVideoSettingDO = mapEnterpriseVideoDO(enterpriseVideoSettingDTO);
        //获取用户名
        BossLoginUserDTO user = BossUserHolder.getUser();
        String userName = user == null ? Constants.SYSTEM : UserHolder.getUser().getUserId() == null
                ? Constants.SYSTEM : UserHolder.getUser().getUserId();
        enterpriseVideoSettingDO.setCreateName(userName);
        enterpriseVideoSettingDO.setCreateTime(new Date());
        enterpriseVideoSettingDO.setUpdateName(userName);
        enterpriseVideoSettingDO.setUpdateTime(new Date());
        //默认false
        enterpriseVideoSettingDO.setOpenWebHook(false);
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(enterpriseVideoSettingDTO.getAccountType());
        if(Objects.isNull(accountType)){
            //默认平台
            accountType = AccountTypeEnum.PLATFORM;
        }
        enterpriseVideoSettingDO.setAccountType(accountType.getCode());
        enterpriseVideoSettingMapper.insertEnterpriseVideoSetting(enterpriseVideoSettingDO);
        return true;
    }

    private EnterpriseVideoSettingDO mapEnterpriseVideoDO(EnterpriseVideoSettingDTO enterpriseVideoSettingDTO) {
        EnterpriseVideoSettingDO enterpriseVideoSettingDO = new EnterpriseVideoSettingDO();
        enterpriseVideoSettingDO.setId(enterpriseVideoSettingDTO.getId());
        enterpriseVideoSettingDO.setEnterpriseId(enterpriseVideoSettingDTO.getEnterpriseId());
        enterpriseVideoSettingDO.setAccessKeyId(enterpriseVideoSettingDTO.getAccessKeyId());
        enterpriseVideoSettingDO.setSecret(enterpriseVideoSettingDTO.getSecret());
        enterpriseVideoSettingDO.setYunType(enterpriseVideoSettingDTO.getYunType());
        enterpriseVideoSettingDO.setAliyunCorpId(enterpriseVideoSettingDTO.getAliyunCorpId());
        enterpriseVideoSettingDO.setOpenVideoStreaming(enterpriseVideoSettingDTO.getOpenVideoStreaming());
        enterpriseVideoSettingDO.setRootVdsCorpId(enterpriseVideoSettingDTO.getRootVdsCorpId());
        enterpriseVideoSettingDO.setOpenDataAnalysis(enterpriseVideoSettingDTO.getOpenDataAnalysis());
        enterpriseVideoSettingDO.setOpenAlarmEvent(enterpriseVideoSettingDTO.getOpenAlarmEvent());
        enterpriseVideoSettingDO.setOpenYunControl(enterpriseVideoSettingDTO.getOpenYunControl());
        enterpriseVideoSettingDO.setVideoPlaybackType(enterpriseVideoSettingDTO.getVideoPlaybackType());
        enterpriseVideoSettingDO.setOpenWebHook(enterpriseVideoSettingDTO.getOpenWebHook());
        enterpriseVideoSettingDO.setHasOpen(enterpriseVideoSettingDTO.getHasOpen());
        enterpriseVideoSettingDO.setSyncDeviceFlag(enterpriseVideoSettingDTO.getSyncDeviceFlag());
        String accountType = StringUtils.isBlank(enterpriseVideoSettingDO.getAccountType()) ? AccountTypeEnum.PRIVATE.getCode() : enterpriseVideoSettingDTO.getAccountType();
        enterpriseVideoSettingDO.setAccountType(accountType);
        return enterpriseVideoSettingDO;
    }

    @Override
    public List<EnterpriseVideoSettingDTO> getEnterpriseVideoSetting(String eid) {
        List<EnterpriseVideoSettingDO> enterpriseVideoSettingDOList = enterpriseVideoSettingMapper.listEnterpriseVideoSettingByEid(eid);
        return ListUtils.emptyIfNull(enterpriseVideoSettingDOList)
                .stream()
                .map(this::mapEnterpriseSettingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnterpriseVideoSettingDTO getEnterpriseVideoSettingByYunType(String eid, String yunType, String accountType) {
        DataSourceHelper.reset();
        EnterpriseVideoSettingDO enterpriseVideoSettingDO = enterpriseVideoSettingMapper.selectEnterpriseVideoSettingByYunType(eid, yunType, accountType);
        return mapEnterpriseSettingDTO(enterpriseVideoSettingDO);
    }

    private EnterpriseVideoSettingDTO mapEnterpriseSettingDTO(EnterpriseVideoSettingDO enterpriseVideoSettingDO) {
        EnterpriseVideoSettingDTO enterpriseVideoSettingDTO = new EnterpriseVideoSettingDTO();
        if(enterpriseVideoSettingDO==null){
            return null;
        }
        enterpriseVideoSettingDTO.setId(enterpriseVideoSettingDO.getId());
        enterpriseVideoSettingDTO.setEnterpriseId(enterpriseVideoSettingDO.getEnterpriseId());
        enterpriseVideoSettingDTO.setAccessKeyId(enterpriseVideoSettingDO.getAccessKeyId());
        enterpriseVideoSettingDTO.setSecret(enterpriseVideoSettingDO.getSecret());
        enterpriseVideoSettingDTO.setYunType(enterpriseVideoSettingDO.getYunType());
        enterpriseVideoSettingDTO.setAliyunCorpId(enterpriseVideoSettingDO.getAliyunCorpId());
        enterpriseVideoSettingDTO.setOpenVideoStreaming(enterpriseVideoSettingDO.getOpenVideoStreaming());
        enterpriseVideoSettingDTO.setRootVdsCorpId(enterpriseVideoSettingDO.getRootVdsCorpId());
        enterpriseVideoSettingDTO.setOpenDataAnalysis(enterpriseVideoSettingDO.getOpenDataAnalysis());
        enterpriseVideoSettingDTO.setOpenAlarmEvent(enterpriseVideoSettingDO.getOpenAlarmEvent());
        enterpriseVideoSettingDTO.setOpenYunControl(enterpriseVideoSettingDO.getOpenYunControl());
        enterpriseVideoSettingDTO.setVideoPlaybackType(enterpriseVideoSettingDO.getVideoPlaybackType());
        enterpriseVideoSettingDTO.setOpenWebHook(enterpriseVideoSettingDO.getOpenWebHook());
        enterpriseVideoSettingDTO.setHasOpen(enterpriseVideoSettingDO.getHasOpen());
        enterpriseVideoSettingDTO.setAccountType(enterpriseVideoSettingDO.getAccountType());
        enterpriseVideoSettingDTO.setLastSyncTime(enterpriseVideoSettingDO.getLastSyncTime());
        enterpriseVideoSettingDTO.setSyncStatus(enterpriseVideoSettingDO.getSyncStatus());
        enterpriseVideoSettingDTO.setSyncDeviceFlag(enterpriseVideoSettingDO.getSyncDeviceFlag());
        enterpriseVideoSettingDTO.setExtendInfo(enterpriseVideoSettingDO.getExtendInfo());
        return enterpriseVideoSettingDTO;
    }

    @Override
    public Boolean saveEnterpriseVideoSetting(List<EnterpriseVideoSettingDTO> enterpriseVideoSettingDTOList) {
        if(CollectionUtils.isEmpty(enterpriseVideoSettingDTOList)){
            return true;
        }
        String enterpriseId = enterpriseVideoSettingDTOList.get(0).getEnterpriseId();

        //判断云眸是否是共用的私用账户(如果共用私有账户需要使用云眸平台的一级菜单来区分云眸平台中的账户是属于哪个企业)
        List<EnterpriseVideoSettingDTO> HikCloudList = ListUtils.emptyIfNull(enterpriseVideoSettingDTOList)
                .stream()
                .filter(data -> YunTypeEnum.HIKCLOUD.getCode().equals(data.getYunType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(HikCloudList)){
            //云眸配置
            EnterpriseVideoSettingDTO enterpriseVideoSettingDTO = HikCloudList.get(0);
            //为空的时候不需要校验
            if (StringUtils.isNotEmpty(enterpriseVideoSettingDTO.getAccessKeyId())){
                //查询非该企业 是否有使用此appkey的企业 如果有说明至少两个企业共用了云眸的私有账户  需要校验这两个企业是否配置了一级菜单
                EnterpriseVideoSettingDO enterpriseVideoSettingDO = enterpriseVideoSettingMapper.selectEnterpriseVideoSettingByAppkey(enterpriseId, enterpriseVideoSettingDTO.getYunType(), enterpriseVideoSettingDTO.getAccountType(), enterpriseVideoSettingDTO.getAccessKeyId());
                if (enterpriseVideoSettingDO!=null){
                    //两种情况下 之前配置的没有配置一级菜单标识 还有就是新增的企业使用没有配置一级菜单标识
                    if (StringUtils.isEmpty(enterpriseVideoSettingDO.getSyncDeviceFlag())||StringUtils.isEmpty(enterpriseVideoSettingDTO.getSyncDeviceFlag())){
                        //配置
                        throw new ServiceException(ErrorCodeEnum.ENTERPRISE_VIDEO_SETTING_FIRST_NODE);
                    }
                }
            }
        }
        //插入或者更新平台配置，先切分那些是插入那些是更新
        List<EnterpriseVideoSettingDTO> updateEnterpriseVideoSettingDTOList = ListUtils.emptyIfNull(enterpriseVideoSettingDTOList)
                .stream()
                .filter(data -> data.getId() != null)
                .collect(Collectors.toList());

        List<EnterpriseVideoSettingDTO> insertEnterpriseVideoSettingDTOList = ListUtils.emptyIfNull(enterpriseVideoSettingDTOList)
                .stream()
                .filter(data -> data.getId() == null)
                .collect(Collectors.toList());
        ListUtils.emptyIfNull(insertEnterpriseVideoSettingDTOList)
                .forEach(this::addEnterpriseVideoSetting);

        //该循环最多循环三次
        ListUtils.emptyIfNull(updateEnterpriseVideoSettingDTOList)
                .forEach(data->{
                    EnterpriseVideoSettingDO enterpriseVideoSettingDO = mapEnterpriseVideoDO(data);
                    //获取用户名
                    BossLoginUserDTO user = BossUserHolder.getUser();
                    String userName = user == null ? Constants.SYSTEM : UserHolder.getUser().getUserId() == null
                            ? Constants.SYSTEM : UserHolder.getUser().getUserId();
                    enterpriseVideoSettingDO.setUpdateName(userName);
                    enterpriseVideoSettingDO.setUpdateTime(new Date());
                    enterpriseVideoSettingDO.setHasOpen(data.getHasOpen()==null?false:data.getHasOpen());
                    enterpriseVideoSettingDO.setAccountType(data.getAccountType());
                    enterpriseVideoSettingMapper.updateEnterpriseVideoSetting(enterpriseVideoSettingDO);
                    if(data.getYunType().equals(YunTypeEnum.YUSHIYUN.getCode())){
                        redisUtil.delKey(Constants.YUSHI_TOKEN +enterpriseId);
                    }
                });
        return true;
    }

    @Override
    public EnterpriseVideoSettingDTO getVideoSettingByYunTypeAndAccountTYpe(String enterpriseId, YunTypeEnum yunTypeEnum, AccountTypeEnum accountTypeEnum) {
        EnterpriseVideoSettingDO enterpriseVideoSettingDO = enterpriseVideoSettingMapper.selectEnterpriseVideoSettingByYunType(enterpriseId, yunTypeEnum.getCode(), accountTypeEnum.getCode());
        return mapEnterpriseSettingDTO(enterpriseVideoSettingDO);
    }

    @Override
    public void updateVdsCorpId(Long id, String rootVdsCorpId) {
        enterpriseVideoSettingMapper.updateVdsCorpId(id, rootVdsCorpId);
    }

    @Override
    public SettingVO getSetting(String eid, YunTypeEnum yunType, AccountTypeEnum accountType) {
        SettingVO vo = getSettingIncludeNull(eid, yunType, accountType);
        if (vo == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "使用"+yunType.getMsg()+"视频服务，请联系销售人员配置！");
        }
        return vo;
    }

    @Override
    public List<SettingVO> getSettingAll(String eid) {
        List<EnterpriseVideoSettingDTO> enterpriseVideoSettingDTOList = getEnterpriseVideoSetting(eid);
        List<SettingVO> settingVOList = ListUtils.emptyIfNull(enterpriseVideoSettingDTOList)
                .stream()
                .filter(Objects::nonNull)
                .filter(EnterpriseVideoSettingDTO::getHasOpen)
                .map(this::mapSettingVO)
                .collect(Collectors.toList());
        return settingVOList;
    }

    @Override
    public SettingVO getSettingIncludeNull(String eid, YunTypeEnum yunType, AccountTypeEnum accountType) {
        if(yunType == null || accountType == null){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "暂时不支持该云平台类型");
        }
        DataSourceHelper.reset();
        EnterpriseVideoSettingDTO enterpriseVideoSettingDTO = getEnterpriseVideoSettingByYunType(eid, yunType.getCode(), accountType.getCode());
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        if (Objects.isNull(enterpriseConfigDO)){
            log.info("getSettingIncludeNull enterpriseConfigDO IS NULL");
            return null;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if (Objects.isNull(enterpriseVideoSettingDTO)) {
            return null;
        }
        SettingVO vo = mapSettingVO(enterpriseVideoSettingDTO);
        return vo;
    }

    @Override
    public void updateLastSyncLastTime(String enterpriseId, YunTypeEnum yunTypeEnum, DeviceSyncStatusEnum syncStatusEnum) {
        enterpriseVideoSettingMapper.updateLastSyncLastTime(enterpriseId, yunTypeEnum.getCode(), syncStatusEnum.getStatus());
    }

    private SettingVO mapSettingVO(EnterpriseVideoSettingDTO enterpriseVideoSettingDTO) {
        SettingVO vo = new SettingVO();
        vo.setId(enterpriseVideoSettingDTO.getId());
        vo.setEid(enterpriseVideoSettingDTO.getEnterpriseId());
        vo.setAccessKeyId(enterpriseVideoSettingDTO.getAccessKeyId());
        vo.setSecret(enterpriseVideoSettingDTO.getSecret());
        vo.setYunType(enterpriseVideoSettingDTO.getYunType());
        vo.setAliyunCorpId(enterpriseVideoSettingDTO.getAliyunCorpId());
        vo.setOpenVideoStreaming(enterpriseVideoSettingDTO.getOpenVideoStreaming());
        vo.setRootVdsCorpId(enterpriseVideoSettingDTO.getRootVdsCorpId());
        vo.setOpenDataAnalysis(enterpriseVideoSettingDTO.getOpenDataAnalysis());
        vo.setOpenAlarmEvent(enterpriseVideoSettingDTO.getOpenAlarmEvent());
        vo.setOpenYunControl(enterpriseVideoSettingDTO.getOpenYunControl());
        vo.setVideoPlaybackType(enterpriseVideoSettingDTO.getVideoPlaybackType());
        vo.setHasOpen(enterpriseVideoSettingDTO.getHasOpen());
        vo.setOpenWebHook(enterpriseVideoSettingDTO.getOpenWebHook());
        vo.setHikCloudFristNodeId(enterpriseVideoSettingDTO.getSyncDeviceFlag());
        return vo;
    }

}
