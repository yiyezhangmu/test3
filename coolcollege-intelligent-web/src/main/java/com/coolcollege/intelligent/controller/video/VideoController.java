package com.coolcollege.intelligent.controller.video;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.controller.video.request.DevicePositionRequest;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.device.DevicePositionMapper;
import com.coolcollege.intelligent.model.SerializationId;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.DevicePositionDO;
import com.coolcollege.intelligent.model.device.dto.DeviceConfigDTO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.device.vo.DevicePositionVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.service.video.openapi.impl.ULucuOpenServiceImpl;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author zhouyiping
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping({"/v2/enterprises/videos","/v3/enterprises/videos"})
@Api(tags = "视频巡店")
public class VideoController {


    @Resource
    private DevicePositionMapper devicePositionMapper;
    @Resource
    private VideoServiceApi videoServiceApi;

    @Resource
    private ULucuOpenServiceImpl uLucuOpenServiceApi;


    @GetMapping("/liveVideo")
    @ApiOperation("获取实时视频URL")
    public String getLiveVideoByGbId(@RequestParam("enterprise_id")String eid,
                                     @Valid VideoDTO param) {
        DataSourceHelper.changeToMy();
        LiveVideoVO liveUrl = videoServiceApi.getLiveUrl(eid, param);
        return liveUrl.getUrl();
    }
    /**
     * 获取实时视频
     *
     * @param
     * @return
     */
    @GetMapping("/liveVideo/new")
    @ApiOperation("获取实时视频")
    public ResponseResult<LiveVideoVO> getLiveVideoByGbIdNew(@RequestParam("enterprise_id")String eid,
                                     @Valid VideoDTO param) {
        DataSourceHelper.changeToMy();
        LiveVideoVO liveVideo = videoServiceApi.getLiveUrl(eid, param);
        return ResponseResult.success(liveVideo);
    }

    @GetMapping("/pastVideo")
    @ApiOperation("根据时间段获取视频")
    public ResponseResult<LiveVideoVO> getPastVideoByGbid(@RequestParam("enterprise_id")String eid,
                                     @Valid VideoDTO param) {
        DataSourceHelper.changeToMy();
        LiveVideoVO pastVideo = videoServiceApi.getPastVideoUrl(eid, param);
        return ResponseResult.success(pastVideo);
    }

    @GetMapping("/pastVideo/new")
    @ApiOperation("根据时间段获取视频")
    public ResponseResult<LiveVideoVO> getPastVideoByGbidNew(@RequestParam("enterprise_id")String eid,
                                     @Valid VideoDTO param) {
        DataSourceHelper.changeToMy();
        if(StringUtils.isAnyBlank(param.getStartTime(), param.getEndTime())){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        Date startTime = DateUtil.parse(param.getStartTime(), DateUtils.DATE_FORMAT_SEC);
        Date endTime= DateUtil.parse(param.getEndTime(), DateUtils.DATE_FORMAT_SEC);
        if(DateUtils.dayBetween(startTime, endTime) >= 1){
            throw new ServiceException(ErrorCodeEnum.TIME_INTERVAL_LONG);
        }
        LiveVideoVO pastVideo = videoServiceApi.getPastVideoUrl(eid, param);
        return ResponseResult.success(pastVideo);
    }

    @GetMapping("/ptz/start")
    @ApiOperation("云台控制")
    public ResponseResult ptzStart(@RequestParam("enterprise_id")String eid,
                                   @RequestParam("deviceId")String deviceId,
                                   @RequestParam(value = "channelNo",required = false)String channelNo,
                                   @RequestParam("command")Integer command,
                                   @RequestParam(value = "speed",defaultValue = "4")Integer speed,
                                   @RequestParam(value = "duration",defaultValue = "300")Long duration){

        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.ptzStart(eid, deviceId, channelNo, command, speed, duration));
    }

    @GetMapping("/ptz/stop")
    @ApiOperation("云台控制停止")
    public ResponseResult ptzStop(@RequestParam("enterprise_id")String eid,
                                  @RequestParam("deviceId")String deviceId,
                                  @RequestParam(value = "channelNo",required = false)String channelNo){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.ptzStop(eid,deviceId, channelNo));
    }

    @GetMapping("/device/position/list")
    @ApiOperation("预置位列表")
    public ResponseResult<List<DevicePositionVO>> getDevicePositionList(@RequestParam("enterprise_id")String eid,
                                                                     @RequestParam("deviceId") String deviceId,
                                                                     @RequestParam(value = "channelNo",required = false)String channelNo){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.getDevicePositionList(eid, deviceId, channelNo));
    }

    @PostMapping("/device/position/save")
    @ApiOperation("预置位保存")
    public ResponseResult saveDevicePosition(@RequestParam("enterprise_id")String eid,
                                             @RequestBody @Validated DevicePositionRequest request){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.addOrUpdatePtzPreset(eid, request.getDeviceId(), request.getId(), request.getChannelNo(),request.getDevicePositionName(), UserHolder.getUser().getUserId()));
    }

    @PostMapping("/device/position/delete")
    @ApiOperation("预置位删除")
    public ResponseResult deleteDevicePosition(@RequestParam("enterprise_id")String eid,
                                               @RequestBody @Validated SerializationId id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.deletePtzPreset(eid, id.getId()));
    }


    @PostMapping("/device/position/invoke")
    @ApiOperation("调用预置位")
    public ResponseResult invokeDevicePosition(@RequestParam("enterprise_id")String eid,
                                               @RequestBody @Validated SerializationId id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.loadPtzPreset(eid, id.getId()));
    }

    @GetMapping("/playbackSpeed")
    @ApiOperation("倍数播放")
    public ResponseResult playbackSpeed(@RequestParam("enterprise_id")String eid, VideoDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.playbackSpeed(eid, param.getDeviceId(), param.getChannelNo(), param.getStreamId(), param.getSpeed(), param.getProtocol()));
    }


    @GetMapping("/getAppKey")
    @ApiOperation("获取AppKey")
    public ResponseResult getAppKey(@RequestParam("enterprise_id")String eid,
                                    @RequestParam("deviceId")String deviceId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.authentication(eid, deviceId));
    }

    @GetMapping("/getAllFirstNodeList")
    @ApiOperation("查询云眸私有账户一级菜单")
    public ResponseResult getAllFirstNodeList(@RequestParam("enterprise_id")String eid){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.getAllFirstNodeList(eid, YunTypeEnum.HIKCLOUD, AccountTypeEnum.PRIVATE,Constants.ZERO,Constants.PAGE_SIZE));
    }



    @GetMapping("/heartbeat")
    @ApiOperation("心跳检测-ulucu")
    public ResponseResult<Boolean> heartbeat(@RequestParam("enterprise_id")String eid,
                                             @Valid VideoDTO param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(uLucuOpenServiceApi.heartbeat(eid, param));
    }

    @GetMapping("/mediaCheck")
    @ApiOperation("媒体播放探测-ulucu")
    public ResponseResult<String> mediaCheck(@RequestParam("enterprise_id") String eid,
                                              @RequestParam("playUrl") String playUrl) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(uLucuOpenServiceApi.mediaCheck(eid, playUrl));
    }
}
