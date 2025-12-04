package com.coolcollege.intelligent.controller.device;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceEncryptEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.*;
import com.coolcollege.intelligent.controller.common.vo.EnumVO;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.facade.device.DeviceFacade;
import com.coolcollege.intelligent.facade.setting.SettingFacade;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.DeviceMappingDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.export.StoreDeviceExportEntity;
import com.coolcollege.intelligent.model.device.request.*;
import com.coolcollege.intelligent.model.device.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author wch
 * @ClassName DeviceController
 * @Description 用一句话描述什么
 */
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/devices","/v3/enterprises/{enterprise-id}/devices"})
@BaseResponse
@Slf4j
@Api(tags = "设备")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RedisUtilPool redis;

    @Resource
    private DeviceMapper deviceMapper;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private DeviceFacade deviceFacade;

    @Autowired
    private SettingFacade settingFacade;

    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private VideoServiceApi videoServiceApi;



    private final String EXPORT_TITLE = "填写须知：" +
            "请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败。\n"+
            "请在需要绑定设备的门店后边填写必填项，不需要的直接空着。\n";


    /**
     * 查询我的门店BI设备或总公司设备列表
     * @param enterpriseId
     * @param storeId
     * @param areaId
     * @param deviceType
     * @param keywords
     * @param bindStatus
     * @param deviceStatus
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping(path = "/list")
    @ApiOperation("设备列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "store_id", value = "门店ID", required = false),
            @ApiImplicitParam(name = "area_id", value = "", required = false),
            @ApiImplicitParam(name = "device_type", value = "设备类型", required = false),
            @ApiImplicitParam(name = "keyword", value = "名称", required = false),
            @ApiImplicitParam(name = "bind_status", value = "绑定状态", required = false),
            @ApiImplicitParam(name = "device_status", value = "设备状态", required = false),
            @ApiImplicitParam(name = "page_number", value = "页码", required = false),
            @ApiImplicitParam(name = "page_size", value = "分页大小", required = false)
    })
    public ResponseResult deviceList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                             @RequestParam(value = "store_id",required = false)String storeId,
                             @RequestParam(value = "area_id",required = false)String areaId,
                             @RequestParam(value = "device_type", required = false)String deviceType,
                             @RequestParam(value = "keywords", required =false )String keywords,
                             @RequestParam(value = "bind_status", required =false )String bindStatus,
                             @RequestParam(value = "device_status",required = false)String deviceStatus,
                             @RequestParam(value = "page_number",required = false,defaultValue = "1")Integer pageNumber,
                             @RequestParam(value = "page_size",required = false,defaultValue = "10")Integer pageSize) {

        DataSourceHelper.changeToMy();
        DeviceListRequest request = new DeviceListRequest();
        request.setStoreId(storeId);
        request.setAreaId(areaId);
        request.setDeviceType(deviceType);
        request.setKeywords(keywords);
        request.setBindStatus(bindStatus);
        request.setDeviceStatus(deviceStatus);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        List<DeviceMappingDTO> devices =deviceService.deviceList(enterpriseId,request,UserHolder.getUser());
        if (CollectionUtils.isNotEmpty(devices)) {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(devices)));
        } else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>())));
        }
    }

    /**
     * 设备详情
     * @param enterpriseId
     * @param deviceId
     * @return
     */
    @GetMapping(path = "/detail")
    @ApiOperation("设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "device_id", value = "设备ID", required = true)
    })
    public ResponseResult<DeviceInfoVO> detail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                             @RequestParam("device_id")String deviceId) {
        DataSourceHelper.changeToMy();
        DeviceInfoVO detail = deviceService.detail(enterpriseId, deviceId);
        return ResponseResult.success(detail);
    }

    /**
     * 设备配置客流详情
     * @param enterpriseId
     * @param deviceId
     * @return
     */
    @GetMapping(path = "/passenger/config/detail")
    @ApiOperation("设备配置客流详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备ID", required = true)
    })
    public ResponseResult<PassengerConfigDTO> passengerDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                  @RequestParam("deviceId")String deviceId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.passengerDetail(enterpriseId, deviceId));
    }
    @PostMapping(path = "/passenger/config/set")
    public ResponseResult<Boolean> savePassenger(@PathVariable(value = "enterprise-id", required = false) String eid,
                                  @RequestBody PassengerConfigDTO passengerConfigDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.setPassengerConfig(eid, passengerConfigDTO));
    }


    /**
     * 门店绑定设备
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping(path = "/bind")
    @ApiOperation("门店绑定设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_ADD, operateDesc = "绑定设备")
    public ResponseResult bind(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestBody @Validated BindDeviceRequest request) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        log.info("绑定设备{}:user_id={},request={}",enterpriseId,userId,request);
        return ResponseResult.success(deviceService.bind(enterpriseId,request.getStoreIds(), request.getDeviceIdList()));
    }

    /**
     * 解绑设备
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping(path = "/unbind")
    @ApiOperation("解绑设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "解绑设备")
    public ResponseResult unbind(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                 @RequestBody UnBindDeviceRequest request) {

        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        log.info("解绑设备{}:user_id={},request={}",enterpriseId,userId,request);
            deviceService.unbind(enterpriseId, request.getDeviceIdList());
        return ResponseResult.success(true);
    }
    @PostMapping(path = "/init/rootVdsCorpId")
    public ResponseResult bindRegion(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                 @RequestBody BindDeviceRegionRequest request) {
        String vcsCorpId=aliyunService.getVcsCorpId(enterpriseId);
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        log.info("绑定区域{}:user_id={},request={}",enterpriseId,userId,request);
        deviceService.bindRegion(enterpriseId, vcsCorpId,request.getRegionId());
        return ResponseResult.success(true);
    }

    /**
     * 更新设备名称
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping(path = "/update")
    @ApiOperation("更新设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "编辑设备")
    @SysLog(func = "编辑", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.EDIT)
    public ResponseResult<Boolean> updateBone(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestBody DeviceUpdateRequest request) {
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        log.info("更新设备{}:user_id={},request={}",enterpriseId,userId,request);
        return ResponseResult.success(deviceService.updateDevice(enterpriseId, request));
    }

    @ApiOperation("更新设备通道")
    @PostMapping(path = "/channel/update")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新设备通道")
    public ResponseResult<Boolean> updateDeviceChannel(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                       @RequestBody @Validated DeviceChannelUpdateRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.updateDeviceChannel(enterpriseId, request));
    }

    /**
     * 设备状态
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/status")
    @ApiOperation("设备状态")
    public ResponseResult deviceStatus(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        List<EnumVO> enumVOList = Arrays.stream(DeviceStatusEnum.values())
                .map(data -> {
                    EnumVO enumVO = new EnumVO();
                    enumVO.setEnumKey(data.getCode());
                    enumVO.setEnumValue(data.getDesc());
                    return enumVO;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(enumVOList);
    }
    /**
     * 门店场景
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/scene")
    @ApiOperation("门店场景")
    public ResponseResult deviceScene(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        List<EnumVO> enumVOList = Arrays.stream(DeviceSceneEnum.values())
                .map(data -> {
                    EnumVO enumVO = new EnumVO();
                    enumVO.setEnumKey(data.getCode());
                    enumVO.setEnumValue(data.getDesc());
                    return enumVO;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(enumVOList);
    }
    /**
     * 门店场景
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/type")
    public ResponseResult deviceType(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        List<EnumVO> enumVOList = Arrays.stream(DeviceTypeEnum.values())
                .map(data -> {
                    EnumVO enumVO = new EnumVO();
                    enumVO.setEnumKey(data.getCode());
                    enumVO.setEnumValue(data.getDesc());
                    return enumVO;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(enumVOList);
    }


    /**
     * @param enterpriseId
     * @return java.lang.Object
     * @throws
     * @Title updateDevices
     * @Description 手动拉取全量可用设备列表
     */
    @GetMapping(path = "/syncDevices")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "同步设备信息")
    @SysLog(func = "同步", opModule = OpModuleEnum.SETTING_DEVICE_INTEGRATION, opType = OpTypeEnum.DEVICE_SYNC)
    public ResponseResult syncDevices(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam("yunType")YunTypeEnum yunType) {
        String userId = UserHolder.getUser().getUserId();
        String dbName = UserHolder.getUser().getDbName();
        DataSourceHelper.changeToMy();
        deviceService.syncDevice(enterpriseId, yunType, userId, dbName);
        return ResponseResult.success();
    }

    /**
     * 同步单个设备
     * @param enterpriseId 企业id
     * @param yunTypeEnum 云类型
     * @param deviceId 设备id
     * @param storeId 门店id
     * @param accountType 账号类型
     * @param deviceName 设备名称
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     */
    @GetMapping("/syncSingleDevice")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "同步单个设备信息")
    public ResponseResult syncSingleDevice(@PathVariable(value = "enterprise-id") String enterpriseId,
                                           @NotNull(message = "云类型不能为空") YunTypeEnum yunTypeEnum,
                                           @NotEmpty(message = "设备id不能为空") String deviceId, String storeId,
                                           AccountTypeEnum accountType,
                                           String deviceName) {
        DataSourceHelper.changeToMy();
        if (Objects.isNull(accountType)) {
            accountType = AccountTypeEnum.PRIVATE;
        }
        return ResponseResult.success(deviceService.syncSingleDevice(enterpriseId, yunTypeEnum, deviceId, storeId, accountType, deviceName));
    }

    @PostMapping("delete")
    @ApiOperation("删除设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除摄像设备")
    @SysLog(func = "删除", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.DELETE, preprocess = true)
    public Object deleteDevice(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestBody @Validated BaseDeviceListRequest request) {
        String userId = UserHolder.getUser().getUserId();
        log.info("删除摄像设备{}:user_id={},request={}",enterpriseId,userId,request);
        DataSourceHelper.changeToMy();
        return deviceService.deleteDevice(enterpriseId, request.getDeviceIdList(), Boolean.TRUE);
    }

    @PostMapping("deleteLocalDevice")
    @ApiOperation("仅删除本地设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除本地摄像设备")
    @SysLog(func = "删除", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.DELETE, preprocess = true)
    public Object deleteLocalDevice(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestBody @Validated DeviceIdRequest request) {
        String userId = UserHolder.getUser().getUserId();
        log.info("删除摄像设备{}:user_id={},request={}",enterpriseId,userId,request);
        DataSourceHelper.changeToMy();
        return deviceService.deleteDevice(enterpriseId, request.getDeviceIdList(), Boolean.FALSE);
    }

    @ApiOperation("批量删除并取消外卖平台授权")
    @PostMapping("/deleteAndCancelAuth")
    @SysLog(func = "批量删除并取消外卖平台授权", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.DELETE, resolve = false)
    public Object deleteAndCancelAuth(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestBody @Validated List<DeviceDeleteRequest> request) {
        String userId = UserHolder.getUser().getUserId();
        log.info("批量删除并取消授权{}:user_id={},request={}", enterpriseId, userId, request);
        DataSourceHelper.changeToMy();
        return deviceService.deleteDeviceAndCancelAuth(enterpriseId, request, Boolean.TRUE);
    }


    @PostMapping("deleteLocalChannel")
    @ApiOperation("刪除本地通道")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_DELETE, operateDesc = "刪除本地通道")
    @SysLog(func = "刪除本地通道", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.DELETE, preprocess = true)
    public Object deleteLocalChannel(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @RequestBody @Validated DeleteChannelRequest request) {
        String userId = UserHolder.getUser().getUserId();
        log.info("删除摄像设备{}:user_id={},request={}",enterpriseId,userId,request);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deleteLocalChannel(enterpriseId, request));
    }


    @PostMapping("refreshDevice")
    @ApiOperation("刷新设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "刷新设备")
    @SysLog(func = "刷新设备", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.DEVICE_REFRESH, preprocess = true)
    public Object refreshDevice(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @RequestBody @Validated DeviceIdRequest request) {
        String userId = UserHolder.getUser().getUserId();
        log.info("刷新设备{}:user_id={},request={}",enterpriseId,userId,request);
        DataSourceHelper.changeToMy();
        deviceService.refreshDevice(enterpriseId, request.getDeviceIdList(), UserHolder.getUser().getUserId());
        return ResponseResult.success(true);
    }

    @PostMapping("refreshAllDevice")
    @ApiOperation("全量刷新设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "全量刷新设备")
    @SysLog(func = "全量刷新设备", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.DEVICE_REFRESH, preprocess = true)
    public Object refreshAllDevice(@PathVariable(value = "enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        deviceService.refreshAllDevice(enterpriseId);
        return ResponseResult.success(true);
    }

    @GetMapping("get/device")
    @ApiOperation("获取门店设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "store_id", value = "门店ID", required = true)
    })
    public ResponseResult getDeviceByStoreId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                             @RequestParam(value = "store_id", required = false) String storeId) {
        DataSourceHelper.changeToMy();
        List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(enterpriseId, Collections.singletonList(storeId),null, null, null);
        List<DeviceVO> deviceMappingDOS = ListUtils.emptyIfNull(deviceByStoreIdList).stream().map(e -> {
            DeviceVO tempDo = DeviceVO.convertVO(e);
            return tempDo;
        }).collect(Collectors.toList());
        return ResponseResult.success(deviceMappingDOS);
    }

    /**
     * 添加摄像头的设备
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("add")
    @ApiOperation("添加设备")
    @OperateLog(operateModule = CommonConstant.Function.DEVICE, operateType = CommonConstant.LOG_ADD, operateDesc = "添加摄像头的设备")
    @SysLog(func = "新建", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.INSERT)
    public ResponseResult addDevice(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                             @RequestBody DeviceAddRequest request) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        log.info("添加摄像设备{}:userId={},request={}",enterpriseId,userId,request);
        deviceService.addVideo(enterpriseId, userId, request);
        return ResponseResult.success(true);
    }


    @GetMapping("/download/template")
    @ApiOperation("批量导入设备模板下载")
    public void loadingRateExport(@PathVariable("enterprise-id") String enterpriseId, HttpServletResponse response) {
        try {
            List<StoreDeviceExportEntity> exportEntityList = deviceService.getExportStore(enterpriseId);
            FileUtil.exportBigDataExcel(exportEntityList, EXPORT_TITLE, "设备列表", StoreDeviceExportEntity.class, "批量导入设备模板.xlsx", response);
        } catch (Exception e) {
            log.info("模板下载异常:" + e.getMessage());
        }
    }

    @GetMapping("/exportDevice")
    @ApiOperation("设备列表导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "store_id", value = "门店ID", required = false),
            @ApiImplicitParam(name = "area_id", value = "", required = false),
            @ApiImplicitParam(name = "device_type", value = "设备类型", required = false),
            @ApiImplicitParam(name = "keyword", value = "名称", required = false),
            @ApiImplicitParam(name = "bind_status", value = "绑定状态", required = false),
            @ApiImplicitParam(name = "device_status", value = "设备状态", required = false),
            @ApiImplicitParam(name = "page_number", value = "页码", required = false),
            @ApiImplicitParam(name = "page_size", value = "分页大小", required = false)
    })
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "设置-设备管理-设备列表")
    public ResponseResult<ImportTaskDO> exportDevice(@PathVariable("enterprise-id") String enterpriseId,
                                                     @RequestParam(value = "store_id",required = false)String storeId,
                                                     @RequestParam(value = "area_id",required = false)String areaId,
                                                     @RequestParam(value = "device_type", required = false)String deviceType,
                                                     @RequestParam(value = "keywords", required =false )String keywords,
                                                     @RequestParam(value = "bind_status", required =false )String bindStatus,
                                                     @RequestParam(value = "device_status",required = false)String deviceStatus){
        DataSourceHelper.changeToMy();
        DeviceListRequest request = new DeviceListRequest();
        request.setStoreId(storeId);
        request.setAreaId(areaId);
        request.setDeviceType(deviceType);
        request.setKeywords(keywords);
        request.setBindStatus(bindStatus);
        request.setDeviceStatus(deviceStatus);
        return ResponseResult.success(deviceService.getExportDevice(enterpriseId,request,UserHolder.getUser()));
    }


    @GetMapping("/refreshDeviceStatus")
    @ApiOperation("设备状态刷新")
    public ResponseResult refreshDeviceStatus(@PathVariable("enterprise-id") String enterpriseId, HttpServletResponse response){
        DataSourceHelper.changeToMy();
        String dbName = UserHolder.getUser().getDbName();
        return ResponseResult.success(deviceService.refreshDeviceStatus(enterpriseId, dbName));
    }

    @GetMapping("/refreshDeviceStatusByStore")
    @ApiOperation("按门店刷新设备状态")
    public ResponseResult refreshDeviceStatusByStore(@PathVariable("enterprise-id") String enterpriseId, @RequestParam(value = "storeId")String storeId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.refreshDeviceStatusByStore(enterpriseId, storeId));
    }


    @GetMapping("/deviceSummaryData")
    @ApiOperation("设备概况")
    public ResponseResult deviceSummaryData(@PathVariable("enterprise-id") String enterpriseId, DeviceReportSearchRequest request){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.getDeviceSummaryData(enterpriseId,UserHolder.getUser(),request));
    }



    @PostMapping("saveDeviceCapture")
    @ApiOperation("抓拍图片保存")
    public ResponseResult<Boolean> saveDeviceCapture(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                     @RequestBody DeviceCaptureRequest request) {
        DataSourceHelper.changeToMy();
        log.info("添加摄像设备{}:request={}",enterpriseId, JSONObject.toJSONString(request));
        deviceService.saveDeviceCapture(enterpriseId, request, UserHolder.getUser());
        return ResponseResult.success(true);
    }

    @PostMapping("/delDeviceCapture")
    @ApiOperation("删除图片")
    public ResponseResult<Boolean> delDeviceCapture(@PathVariable("enterprise-id") String enterpriseId,
                                                    @RequestBody DeviceCaptureRequest param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.delDeviceCapture(enterpriseId,param.getDeviceCaptureIds()));
    }

    @GetMapping("/listByStoreId")
    @ApiOperation("根据门店查询图片")
    public ResponseResult<PageInfo<DeviceCaptureLibDTO>> listByStoreId(@PathVariable("enterprise-id") String enterpriseId,
                                                                       @RequestParam(value = "storeId",required = true)String storeId,
                                                                       @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                                                       @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                                                       @RequestParam(value = "beginTime",required = true)Long beginTime,
                                                                       @RequestParam(value = "endTime",required = true)Long endTime){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.listByStoreId(enterpriseId,storeId,pageSize,pageNum, DateUtils.convertTimeToString(beginTime,DateUtils.DATE_FORMAT_MINUTE), DateUtils.convertTimeToString(endTime,DateUtils.DATE_FORMAT_MINUTE)));
    }

    @GetMapping("/getDeviceSummaryGroupStoreId")
    @ApiOperation("设备汇总数据列表")
    public ResponseResult<PageInfo<DeviceSummaryListDTO>> getDeviceSummaryGroupStoreId(@PathVariable("enterprise-id") String enterpriseId, DeviceReportSearchRequest request){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.getDeviceSummaryGroupStoreId(enterpriseId, UserHolder.getUser(), request));
    }

    @GetMapping("/getDeviceYunTypeList")
    @ApiOperation("获取企业设备云类型")
    public ResponseResult<List<DeviceYunTypeVO>> getDeviceYunTypeList(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.getDeviceYunTypeList(enterpriseId));
    }

    @GetMapping("/ExportDeviceSummaryGroupStoreId")
    @ApiOperation("设备汇总数据列表")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "设置-设备管理-设备报表")
    public ResponseResult<ImportTaskDO> ExportDeviceSummaryGroupStoreId(@PathVariable("enterprise-id") String enterpriseId, DeviceReportSearchRequest request){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.ExportDeviceSummaryGroupStoreId(enterpriseId, UserHolder.getUser(), request));
    }

    @GetMapping("/getDevicePackage")
    @ApiOperation("获取设备套餐")
    public ResponseResult<DevicePackageVO> getDevicePackage(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.getDevicePackage(enterpriseId));
    }


    @GetMapping("/getLastPatrolStore")
    @ApiOperation("获取最近巡店的门店")
    public ResponseResult<List<LastPatrolStoreVO>> getLastPatrolStore(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.getLastPatrolStore(enterpriseId, UserHolder.getUser().getUserId()));
    }

    @PostMapping("deviceDownloadCenter")
    @ApiOperation("下载中心视频上传")
    public ResponseResult<Boolean> saveDeviceDownloadCenter(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                            @RequestBody DeviceDownloadCenterRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deviceDownloadCenter(enterpriseId,request,UserHolder.getUser()));
    }

    @GetMapping("/deleteDeviceDownloadCenter")
    @ApiOperation("下载中心视频删除")
    public ResponseResult<Boolean> deleteDeviceDownloadCenter(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                       @RequestParam(name = "id") Long id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deletedVideoRecord(enterpriseId,id));
    }


    @GetMapping("/listDeviceDownloadCenter")
    @ApiOperation("下载中心视频列表")
    public ResponseResult<PageInfo<DeviceDownloadCenterDTO>> listDeviceDownloadCenter(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                      @RequestParam(name = "pageSize") Integer pageSize,
                                                                                      @RequestParam(name = "pageNum") Integer pageNum){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.listDeviceDownloadCenter(enterpriseId,pageSize,pageNum,UserHolder.getUser()));
    }


    @GetMapping("/download")
    @ApiOperation("下载中心视频下载")
    public ResponseResult<String> download(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestParam(name = "id") Long id){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.download(enterpriseId,id));
    }



    @PostMapping("syncCaptureLib")
    @ApiOperation("下载中心视频同步到图片库")
    public ResponseResult<Boolean> syncCaptureLib(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                  @RequestBody DeviceDownloadCenterSyncRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.syncCaptureLib(enterpriseId,request.getFileUrl(),request.getDeviceId(),request.getStoreId(),request.getName(),UserHolder.getUser(),request.getId()));
    }


    @GetMapping("/getDeviceStatus")
    @ApiOperation("获取设备状态")
    public ResponseResult<String> getDeviceStatus(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                               @RequestParam(name = "deviceId") String deviceId,
                                               @RequestParam(name = "channelNo") String channelNo){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.getDeviceStatus(enterpriseId,deviceId,channelNo));
    }


    @GetMapping("/yingShiVideoDownloadTaskStatus")
    @ApiOperation("检查并更新萤石云录像回放下载任务状态")
    public ResponseResult checkAndUpdateYingShiVideoDownloadTaskStatus(@PathVariable(value = "enterprise-id") String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(configDO)){
            log.info("enterprise-id：{}，企业信息不存在, enterpriseConfigDO", enterpriseId);
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        deviceService.checkAndUpdateYingShiVideoDownloadTaskStatus(enterpriseId);
        return ResponseResult.success();
    }

    /**
     * 获取设备accessToken
     * @param accountType 账号类型
     * @param yunType 云类型
     * @param deviceId 设备id
     * @param refresh 刷新token
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     */
    @GetMapping("/getAccessToken")
    @ApiOperation("获取设备accessToken")
    public ResponseResult getAccessToken(@PathVariable(value = "enterprise-id") String enterpriseId,
                                         @NotNull(message = "账号类型不能为空") AccountTypeEnum accountType,
                                         @NotNull(message = "云类型不能为空") YunTypeEnum yunType,
                                         String deviceId,
                                         Boolean refresh) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.getAccessToken(enterpriseId, accountType, yunType, deviceId, refresh));
    }

    @ApiOperation("画面翻转")
    @PostMapping("/pictureFlip")
    public ResponseResult pictureFlip(@PathVariable(value = "enterprise-id") String enterpriseId,
                                      @RequestBody DeviceConfigDTO configDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.pictureFlip(enterpriseId, configDTO));
    }

    @ApiOperation("获取设备录像文件")
    @PostMapping("/listDeviceRecordByTime")
    public ResponseResult<List<DeviceVideoRecordVO>> listDeviceRecordByTime(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                            @RequestBody DeviceRecordQueryRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.listDeviceRecordByTime(enterpriseId, request));
    }

    @ApiOperation("设备对讲")
    @PostMapping("/talkback")
    public ResponseResult talkback(@PathVariable(value = "enterprise-id") String enterpriseId,
                                   @RequestBody DeviceTalkbackDTO talkbackDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deviceTalkback(enterpriseId, talkbackDTO));
    }

    @ApiOperation("设备重启")
    @PostMapping("/reboot")
    public ResponseResult<Boolean> deviceReboot(@PathVariable(value = "enterprise-id") String enterpriseId,
                                       @NotBlank(message = "设备id不能为空") String deviceId,
                                       String channelNo) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deviceReboot(enterpriseId, deviceId, channelNo));
    }

    @ApiOperation("设备存储信息查询")
    @GetMapping("/storageInfo")
    public ResponseResult<List<DeviceStorageInfoVO>> deviceStorageInfo(@PathVariable(value = "enterprise-id") String enterpriseId,
                                            @NotBlank(message = "设备id不能为空") String deviceId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deviceStorageInfo(enterpriseId, deviceId));
    }

    @ApiOperation("设备存储格式化")
    @PostMapping("/storageFormatting")
    @SysLog(func = "设备存储格式化", opModule = OpModuleEnum.SETTING_DEVICE_LIST, opType = OpTypeEnum.DEVICE_FORMAT, resolve = false)
    public ResponseResult<Boolean> deviceStorageFormatting(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                  @NotBlank(message = "设备id不能为空") String deviceId, String channelNo) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deviceStorageFormatting(enterpriseId, deviceId, channelNo));
    }

    @ApiOperation("设备软硬件信息")
    @GetMapping("/softHardInfo")
    public ResponseResult<DeviceSoftHardwareInfoVO> deviceSoftHardwareInfo(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                 @NotBlank(message = "设备id不能为空") String deviceId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.deviceSoftHardwareInfo(enterpriseId, deviceId));
    }

    @ApiOperation("更新视频码流")
    @GetMapping("/updateVideoVencType")
    public ResponseResult<Boolean> updateVideoVencType(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                           @NotBlank(message = "设备id不能为空") String deviceId, @NotBlank(message = "视频码流") String vencType) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(deviceService.updateVideoVencType(enterpriseId, deviceId, vencType));
    }

    @ApiOperation("更改设备存储策略")
    @PostMapping("/modifyDeviceStorageStrategy")
    public ResponseResult<Boolean> modifyDeviceStorageStrategy(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                               @RequestParam(value = "deviceId") String deviceId, String channelNo, @RequestBody SetStorageStrategyDTO param) {
        DataSourceHelper.changeToMy();

        return ResponseResult.success(videoServiceApi.modifyDeviceStorageStrategy(enterpriseId, deviceId, channelNo,  param));
    }

    @ApiOperation("视频加密/解密")
    @PostMapping("/deviceEncrypt")
    public ResponseResult<Boolean> deviceEncrypt(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                               @RequestParam(value = "deviceId") String deviceId, String channelNo, DeviceEncryptEnum encryptType) {
        DataSourceHelper.changeToMy();

        return ResponseResult.success(videoServiceApi.deviceEncrypt(enterpriseId, deviceId, channelNo,  encryptType));
    }
}



