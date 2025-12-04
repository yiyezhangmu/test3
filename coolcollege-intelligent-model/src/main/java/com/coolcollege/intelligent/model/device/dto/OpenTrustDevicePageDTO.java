package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.device.EnterpriseDeviceInfoDO;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *  外部第三方设备列表
 */
@Data
public class OpenTrustDevicePageDTO {

    private String deviceSerial;
    /**
     * 设备id
     */
    private String deviceName;

    /**
     * 设备名称
     */
    private String cameraName;

    private String customName;

    private String  channelNo;

    private Integer status;

    public static List<EnterpriseDeviceInfoDO> convertList(List<OpenTrustDevicePageDTO> deviceList, YunTypeEnum yunType, AccountTypeEnum accountType, String enterpriseId){
        if(CollectionUtils.isEmpty(deviceList)){
            return null;
        }
        List<EnterpriseDeviceInfoDO> recordList = new ArrayList<>();
        for (OpenTrustDevicePageDTO device : deviceList) {
            EnterpriseDeviceInfoDO record = new EnterpriseDeviceInfoDO();
            if("0".equals(device.getChannelNo())){
                record.setDeviceId(device.getDeviceSerial());
                record.setDeviceType("ipc");
            }else{
                record.setDeviceId(device.getDeviceSerial() + Constants.UNDERLINE + device.getChannelNo());
                record.setParentDeviceId(device.getDeviceSerial());
                record.setDeviceType("nvr_ipc");
                record.setChannelNo(device.getChannelNo());
                EnterpriseDeviceInfoDO parentDevice = new EnterpriseDeviceInfoDO();
                parentDevice.setDeviceId(device.getDeviceSerial());
                parentDevice.setDeviceType("nvr");
                parentDevice.setYunType(yunType.getCode());
                parentDevice.setAccountType(accountType.getCode());
                parentDevice.setDeviceStatus(device.getStatus() == 1 ? "online" : "offline");
                parentDevice.setEnterpriseId(enterpriseId);
                recordList.add(parentDevice);
            }
            record.setYunType(yunType.getCode());
            record.setAccountType(accountType.getCode());
            record.setDeviceStatus(device.getStatus() == 1 ? "online" : "offline");
            record.setEnterpriseId(enterpriseId);
            recordList.add(record);
        }
        return recordList;
    }

}
