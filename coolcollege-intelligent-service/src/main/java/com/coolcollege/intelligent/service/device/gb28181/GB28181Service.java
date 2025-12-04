package com.coolcollege.intelligent.service.device.gb28181;

import com.coolcollege.intelligent.model.device.dto.DeviceGBLicenseApplyDTO;
import com.coolcollege.intelligent.model.device.gb28181.Channel;
import com.coolstore.base.enums.AccountTypeEnum;

import java.util.List;

/**
 * <p>
 * GB28181协议接入
 * </p>
 *
 * @author wangff
 * @since 2025/8/11
 */
public interface GB28181Service {

    /**
     * 申请国标设备license
     * @param eid 企业id
     * @param accountType 账号类型
     * @param applyDTO 申请DTO
     * @return 国标序列号
     */
    String license(String eid, AccountTypeEnum accountType, DeviceGBLicenseApplyDTO applyDTO);


    /**
     * 申请国标通道license
     *
     * @param channels
     * @author twc
     * @date 2024/09/11
     */
    List<Channel> channel(String eid, AccountTypeEnum accountType, List<Channel> channels);

    /**
     * 设备国标状态
     *
     * @param eid
     * @param accountType
     * @param deviceSerial
     * @author twc
     * @date 2024/09/23
     */
    /**
     * 国标设备状态
     * @param eid 企业id
     * @param accountType 账号类型
     * @param deviceSerial 设备序列表
     * @param deviceType 1:IPC,2:NVR
     * @return 0-离线 1-在线
     */
    Integer deviceStatus(String eid, AccountTypeEnum accountType, String deviceSerial, Integer deviceType);


    /**
     * 删除设备国标
     *
     * @param eid
     * @param accountTypeEnum
     * @param deviceSerial
     * @author twc
     * @date 2024/09/23
     */
    boolean deleteGBDevice(String eid, AccountTypeEnum accountTypeEnum, String deviceSerial);


    /**
     * 设备国标通道状态
     *
     * @param enterpriseId
     * @param accountTypeEnum
     * @param deviceSerial
     * @author twc
     * @date 2024/09/23
     */
    List<Channel> channelStatus(String enterpriseId, AccountTypeEnum accountTypeEnum, String deviceSerial);
}
