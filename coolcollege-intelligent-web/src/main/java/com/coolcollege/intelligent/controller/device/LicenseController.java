package com.coolcollege.intelligent.controller.device;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.device.request.DeviceChannelLicenseApply;
import com.coolcollege.intelligent.model.device.request.DeviceChannelLicenseBatch;
import com.coolcollege.intelligent.model.device.request.EditDeviceLicense;
import com.coolcollege.intelligent.model.device.vo.DeviceLicenseDetailVO;
import com.coolcollege.intelligent.model.device.vo.DeviceLicenseVO;
import com.coolcollege.intelligent.service.device.LicenseService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 设备 license api
 *
 * @author twc
 * @date 2024/09/10
 */
@Api(tags = "设备license")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/devices/license")
public class LicenseController {
    @Autowired
    private LicenseService licenseService;


    /**
     * 申请设备国标license
     *
     * @param enterpriseId 企业id
     * @param ipc          最大值 100 不建议超过20
     * @param nvr          最大值 100 不建议超过20
     * @author twc
     * @date 2024/09/11
     */
    @GetMapping("/apply")
    public ResponseResult<String> apply(@PathVariable("enterprise-id") String enterpriseId,
                                        @RequestParam("ipc") Integer ipc,
                                        @RequestParam("nvr") Integer nvr) {
        DataSourceHelper.changeToMy();
        licenseService.apply(enterpriseId, ipc, nvr, AccountTypeEnum.PLATFORM);
        return ResponseResult.success("正在申请中... 请稍后，查看license列表");
    }

    /**
     * 分页获取设备license
     *
     * @param enterpriseId 企业id
     * @param type         设备类型 （1:IPC,2:NVR）
     * @param deviceSerial 设备序列号
     * @param name          设备名称
     * @param status       占用状态 （0:未占用，1:已占用）
     * @param useByNew     是否被远程设备使用
     * @param pageSize     pageSize
     * @param pageNum      pageNum
     * @author twc
     * @date 2024/09/13
     */
    @GetMapping("/page")
    public ResponseResult<PageInfo<DeviceLicenseVO>> page(@PathVariable("enterprise-id") String enterpriseId,
                                                          @RequestParam(value = "type", required = false) Integer type,
                                                          @RequestParam(value = "deviceSerial", required = false) String deviceSerial,
                                                          @RequestParam(value = "name", required = false) String name,
                                                          @RequestParam(value = "status", required = false) Integer status,
                                                          @RequestParam(value = "useByNew", required = false) Integer useByNew,
                                                          @RequestParam("pageSize") Integer pageSize,
                                                          @RequestParam("pageNum") Integer pageNum) {
        return ResponseResult.success(licenseService.page(enterpriseId, type, deviceSerial, name, status, useByNew, pageSize, pageNum));
    }

    /**
     * 获取设备license详情
     *
     * @param enterpriseId 企业id
     * @param id           licenseId
     * @author twc
     * @date 2024/09/13
     */
    @GetMapping("/detail")
    public ResponseResult<DeviceLicenseDetailVO> detail(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestParam("id") String id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(licenseService.detail(enterpriseId, id));
    }

    /**
     * 编辑设备license名称，备注
     *
     * @param enterpriseId 企业id
     * @author twc
     * @date 2024/09/13
     */
    @PostMapping("/editLicense")
    public ResponseResult<Void> editLicense(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestBody @Validated EditDeviceLicense editDeviceLicense) {
        DataSourceHelper.changeToMy();
        licenseService.editLicense(enterpriseId, editDeviceLicense.getName(), editDeviceLicense.getRemark(), editDeviceLicense.getId());
        return ResponseResult.success();
    }

    /**
     * 删除国标license
     *
     * @param enterpriseId
     * @param id
     * @author twc
     * @date 2024/09/19
     */
    @DeleteMapping("/deleteLicense")
    public ResponseResult<Void> deleteLicense(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestParam("id") String id) {
        DataSourceHelper.changeToMy();
        licenseService.deleteLicense(enterpriseId, id);
        return ResponseResult.success();
    }

    /**
     * 申请设备通道license
     *
     * @param enterpriseId              企业id
     * @param deviceChannelLicenseApply
     * @author twc
     * @date 2024/09/13
     */
    @PostMapping("apply/channel")
    public ResponseResult<Void> applyChannel(@PathVariable("enterprise-id") String enterpriseId,
                                             @Validated @RequestBody DeviceChannelLicenseApply deviceChannelLicenseApply) {
        DataSourceHelper.changeToMy();
        licenseService.applyChannel(enterpriseId, deviceChannelLicenseApply);
        return ResponseResult.success();
    }

    /**
     * 编辑通道名称（自定义）
     *
     * @param enterpriseId 企业id
     * @param channelName  通道名称
     * @param id           通道id
     * @author twc
     * @date 2024/09/13
     */
    @PostMapping("/editChannel")
    public ResponseResult<Void> editChannel(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestParam("channelName") String channelName,
                                            @RequestParam("id") String id) {
        DataSourceHelper.changeToMy();
        licenseService.editChannel(enterpriseId, channelName, id);
        return ResponseResult.success();
    }

    /**
     * 批量为国标license申请通道
     *
     * @param enterpriseId
     * @param batch
     * @author twc
     * @date 2024/09/19
     */
    @PostMapping("/batchChannelLicense")
    public ResponseResult<Void> batchChannelLicense(@PathVariable("enterprise-id") String enterpriseId,
                                                    @RequestBody DeviceChannelLicenseBatch batch) {
        DataSourceHelper.changeToMy();
        licenseService.batchChannelLicense(enterpriseId, batch.getCount(), batch.getLicenseIds());
        return ResponseResult.success();
    }
}
