package com.coolcollege.intelligent.controller.device;


import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceAuthPageDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceCancelAuthDTO;
import com.coolcollege.intelligent.model.device.vo.DeviceAuthDetailVO;
import com.coolcollege.intelligent.model.device.vo.DeviceAuthRecordVO;
import com.coolcollege.intelligent.model.device.vo.DeviceChannelVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.device.DeviceAuthService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

@Api(tags = "设备授权")
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/devices/auth"})
public class DeviceAuthController {

    @Resource
    private DeviceAuthService deviceAuthService;
    @Resource
    private DeviceService deviceService;

    @ApiOperation("获取设备授权记录")
    @GetMapping("/getDeviceAuthDetail")
    public ResponseResult<DeviceAuthDetailVO> getDeviceAuthDetail(@PathVariable("enterprise-id")String enterpriseId,
                                                                  @RequestParam("deviceId")String deviceId,
                                                                  @RequestParam(value = "channelNo", required = false)String channelNo) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceAuthService.getDeviceAuthDetail(enterpriseId, deviceId, channelNo));
    }

    @ApiOperation("设备授权")
    @PostMapping("/deviceAuth")
    public ResponseResult<Boolean> deviceAuth(@PathVariable("enterprise-id")String enterpriseId, @Validated @RequestBody DeviceAuthDTO param) {
        DataSourceHelper.changeToMy();
        String deviceId = param.getDeviceId();
        DeviceDO device = deviceService.getDeviceByDeviceId(enterpriseId, deviceId);
        if (Objects.isNull(device)) {
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        Boolean hasChildDevice = device.getHasChildDevice();
        if ((Objects.isNull(hasChildDevice) || !hasChildDevice) && Objects.isNull(param.getChannelNo())) {
            param.setChannelNo(Constants.ZERO_STR);
        }
        if (Objects.nonNull(hasChildDevice) && hasChildDevice && Objects.nonNull(param.getChannelNo())) {
            DeviceChannelVO deviceChannel = deviceService.getDeviceChannel(enterpriseId, deviceId, param.getChannelNo());
            if (Objects.isNull(deviceChannel)) {
                throw new ServiceException(ErrorCodeEnum.CHANNEL_NOT_FOUND);
            }
            device.setDeviceStatus(deviceChannel.getStatus());
        }
        if (Objects.nonNull(hasChildDevice) && hasChildDevice && Objects.isNull(param.getChannelNo())) {
            throw new ServiceException(ErrorCodeEnum.SELECT_CHANNEL);
        }
        DataSourceHelper.reset();
        return ResponseResult.success(deviceAuthService.deviceAuth(enterpriseId, param, device));
    }

    @ApiOperation("取消授权")
    @PostMapping("/cancelDeviceAuth")
    public ResponseResult<Boolean> cancelDeviceAuth(@PathVariable("enterprise-id")String enterpriseId,@Validated @RequestBody DeviceCancelAuthDTO param) {
        DataSourceHelper.reset();
        return ResponseResult.success(deviceAuthService.cancelDeviceAuth(enterpriseId, param));
    }

    @ApiOperation("获取设备授权记录")
    @PostMapping("/getDeviceAuthPage")
    public ResponseResult<PageInfo<DeviceAuthRecordVO>> getDeviceAuthPage(@PathVariable("enterprise-id")String enterpriseId, @RequestBody DeviceAuthPageDTO param) {
        return ResponseResult.success(deviceAuthService.getDeviceAuthPage(enterpriseId, param, UserHolder.getUser()));
    }


}
