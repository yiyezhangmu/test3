package com.coolcollege.intelligent.service.setting;

import com.coolcollege.intelligent.common.enums.device.DeviceSyncStatusEnum;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/01
 */
public interface EnterpriseVideoSettingService {
    /**
     * 添加视频配置信息
     * @param enterpriseVideoSettingDTO
     */
    Boolean addEnterpriseVideoSetting(EnterpriseVideoSettingDTO enterpriseVideoSettingDTO);

    /**
     * 获得视频配置信息
     * @param eid
     * @return
     */
    List<EnterpriseVideoSettingDTO> getEnterpriseVideoSetting(String eid);

    EnterpriseVideoSettingDTO getEnterpriseVideoSettingByYunType(String eid,String yunType, String accountType);


    /**
     * 修改视频配置信息
     * @param enterpriseVideoSettingDTOList
     */
    Boolean saveEnterpriseVideoSetting(List<EnterpriseVideoSettingDTO> enterpriseVideoSettingDTOList);


    EnterpriseVideoSettingDTO getVideoSettingByYunTypeAndAccountTYpe(String enterpriseId, YunTypeEnum yunTypeEnum, AccountTypeEnum accountTypeEnum);

    void updateVdsCorpId(Long id, String rootVdsCorpId);

    SettingVO getSetting(String eid, YunTypeEnum yunType, AccountTypeEnum accountType);

    List<SettingVO> getSettingAll(String eid);


    SettingVO getSettingIncludeNull(String eid, YunTypeEnum yunType, AccountTypeEnum accountType);

    void updateLastSyncLastTime(String enterpriseId, YunTypeEnum yunTypeEnum, DeviceSyncStatusEnum statusEnum);



}
