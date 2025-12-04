package com.coolcollege.intelligent.controller.video;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.controller.video.request.DevicePositionRequest;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.video.HikCloudDeviceService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.service.video.openapi.impl.HikCloudOpenServiceImpl;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/8/25 18:50
 * @Version 1.0
 */
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/hik/cloud")
public class HikCloudController {
    @Autowired
    HikCloudDeviceService hikCloudDeviceService;
    @Autowired
    VideoServiceApi videoServiceApi;

    @GetMapping("/getHikCloudRedisToken")
    public ResponseResult getHikCloudRedisToken(@PathVariable(value = "enterprise-id") String enterpriseId) {
        return ResponseResult.success(hikCloudDeviceService.getHikCloudRedisToken(enterpriseId));
    }

    @GetMapping("/getAllDeviceList")
    public ResponseResult getAllDeviceList(@PathVariable(value = "enterprise-id") String enterpriseId) {
        return ResponseResult.success(hikCloudDeviceService.getAllDeviceList(enterpriseId));
    }

    @GetMapping("/liveVideoOpen")
    public ResponseResult liveVideoOpen(@PathVariable(value = "enterprise-id") String enterpriseId,
                                        @RequestParam(value = "channelIds") List<String> channelIds) {
        return ResponseResult.success(hikCloudDeviceService.liveVideoOpen(enterpriseId, channelIds));
    }

    @GetMapping("/getLiveAddress")
    public ResponseResult getAllDeviceList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                           @RequestParam(value = "channelId") String channelId) {
        return ResponseResult.success(hikCloudDeviceService.getLiveAddress(enterpriseId,channelId));
    }

    @GetMapping("/capture")
    public ResponseResult capture(@PathVariable(value = "enterprise-id") String enterpriseId,
                                  @RequestParam(value = "deviceSerial") String deviceSerial,
                                  @RequestParam(value = "channelNo") String channelNo,
                                  @RequestParam(value = "quality") String quality) {
        return ResponseResult.success(hikCloudDeviceService.capture(enterpriseId,deviceSerial,channelNo,quality));
    }

    @GetMapping("/sync/device")
    public ResponseResult createUrl(@PathVariable("enterprise-id") String eid){
        hikCloudDeviceService.asyncDevice(eid, UserHolder.getUser().getUserId());
        return ResponseResult.success(true);
    }


    @GetMapping("/hikCloud/listByDevSerial")
    public ResponseResult createUrl(@PathVariable("enterprise-id") String eid,
                                    @RequestParam(value = "deviceSerial") String deviceSerial){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(hikCloudDeviceService.listByDevSerial(eid,deviceSerial));
    }

    @GetMapping("/ptzStart")
    public ResponseResult ptzStart(@PathVariable("enterprise-id") String eid,
                                    @RequestParam(value = "deviceId")String deviceId,
                                    @RequestParam(value = "channelNo")String channelNo,
                                    @RequestParam(value = "command")Integer command ,
                                    @RequestParam(value = "speed")Integer speed ,
                                    @RequestParam(value = "duration")Long duration){
        DataSourceHelper.changeToMy();
        //开始
        videoServiceApi.ptzStart(eid,deviceId,channelNo,command,speed,duration);
        //停止
        DataSourceHelper.changeToMy();
        videoServiceApi.ptzStop(eid,deviceId,channelNo);
        return ResponseResult.success(true);
    }

    @GetMapping("/ptzStop")
    public ResponseResult ptzStop(@PathVariable("enterprise-id") String eid,
                                   @RequestParam(value = "deviceId")String deviceId,
                                   @RequestParam(value = "channelNo")String channelNo,
                                   @RequestParam(value = "command")Integer command){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.ptzStop(eid,deviceId,channelNo));
    }

    @GetMapping("/addPtzPreset")
    public ResponseResult addPtzPreset(@PathVariable("enterprise-id") String eid,
                                  @RequestParam(value = "deviceId")String deviceId,
                                  @RequestParam(value = "channelNo")String channelNo,
                                  @RequestParam(value = "devicePositionName")String devicePositionName){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.addOrUpdatePtzPreset(eid,deviceId, null, channelNo, devicePositionName, ""));
    }

    @Resource
    HikCloudOpenServiceImpl hikCloudOpenService;
    @Resource
    private DeviceMapper deviceMapper;

    @GetMapping("/device/get1")
    @ApiOperation("调用预置位")
    public ResponseResult videoTransCode(@PathVariable("enterprise-id")String eid,
                                         @RequestParam("deviceId") String deviceId,
                                         @RequestParam("channelNo") String channelNo,
                                         @RequestParam("startTime") String startTime,
                                         @RequestParam("endTime") String endTime,
                                         @RequestParam(value = "recType" ,required = false) String recType){
        DataSourceHelper.changeToMy();
        DeviceDO device = deviceMapper.getDeviceByDeviceId(eid, deviceId);
        VideoDTO param = VideoDTO.builder()
                .channelNo(channelNo)
                .startTime(startTime)
                .endTime(endTime)
                .recType(recType)
                .build();
        return ResponseResult.success(hikCloudOpenService.videoTransCode(eid,device,param));
    }

    @GetMapping("/device/get2")
    @ApiOperation("调用预置位")
    public ResponseResult getVideoFile(@PathVariable("enterprise-id")String eid,
                                       @RequestParam("deviceId") String deviceId,
                                       @RequestParam("fileId") String fileId){
        DataSourceHelper.changeToMy();
        DeviceDO device = deviceMapper.getDeviceByDeviceId(eid, deviceId);
        return ResponseResult.success(hikCloudOpenService.getVideoFile(eid,device,fileId));
    }

    @GetMapping("/device/get3")
    @ApiOperation("调用预置位")
    public ResponseResult getVideoDownloadUrl(@PathVariable("enterprise-id")String eid,
                                              @RequestParam("deviceId") String deviceId,
                                              @RequestParam("fileId") String fileId){
        DataSourceHelper.changeToMy();
        DeviceDO device = deviceMapper.getDeviceByDeviceId(eid, deviceId);
        return ResponseResult.success(hikCloudOpenService.getVideoDownloadUrl(eid,device,fileId));
    }






}
