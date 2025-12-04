package com.coolcollege.intelligent.service.device;

import com.coolcollege.intelligent.model.device.request.DeviceChannelLicenseApply;
import com.coolcollege.intelligent.model.device.request.DeviceRegister;
import com.coolcollege.intelligent.model.device.vo.DeviceLicenseDetailVO;
import com.coolcollege.intelligent.model.device.vo.DeviceLicenseVO;
import com.coolstore.base.enums.AccountTypeEnum;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface LicenseService {

    /**
     * 申请设备国标license
     *
     * @param enterpriseId
     * @param ipc
     * @param nvr
     * @author twc
     * @date 2024/09/11
     */
    void apply(String enterpriseId, Integer ipc, Integer nvr, AccountTypeEnum accountTypeEnum);


    /**
     * 分页获取设备license
     *
     * @param enterpriseId 企业id
     * @param type 设备类型
     * @param status 占用状态
     * @param useByNew 是否被远程设备使用
     * @param pageSize pageSize
     * @param pageNum pageNum
     * @author twc
     * @date 2024/09/13
     */
    PageInfo<DeviceLicenseVO> page(String enterpriseId, Integer type,String deviceSerial,String name, Integer status, Integer useByNew, Integer pageSize, Integer pageNum);

    /**
     * 获取设备license详情
     *
     * @param enterpriseId 企业id
     * @param id licenseId
     * @author twc
     * @date 2024/09/13
     */
    DeviceLicenseDetailVO detail(String enterpriseId, String id);

    /**
     * 编辑通道名称
     *
     * @param enterpriseId 企业id
     * @param channelName 通道名称
     * @param id 通道id
     * @author twc
     * @date 2024/09/13
     */
    void editChannel(String enterpriseId, String channelName,String id);

    /**
     * 编辑设备license名称，备注
     *
     * @param enterpriseId 企业id
     * @param name 设备名称
     * @param remark 备注
     * @param id license id
     * @author twc
     * @date 2024/09/13
     */
    void editLicense(String enterpriseId, String name, String remark, String id);

    /**
     * 申请设备通道license
     *
     * @param enterpriseId              企业id
     * @param deviceChannelLicenseApply
     * @author twc
     * @date 2024/09/13
     */
    void applyChannel(String enterpriseId, DeviceChannelLicenseApply deviceChannelLicenseApply);

    /**
     * 删除国标license
     *
     * @param enterpriseId
     * @param id
     * @author twc
     * @date 2024/09/19
     */
    void deleteLicense(String enterpriseId, String id);

    /**
     * 批量为国标license申请通道
     *
     * @param enterpriseId
     * @param count
     * @param licenseIds
     * @author twc
     * @date 2024/09/19
     */
    void batchChannelLicense(String enterpriseId, Integer count, List<String> licenseIds);

}
