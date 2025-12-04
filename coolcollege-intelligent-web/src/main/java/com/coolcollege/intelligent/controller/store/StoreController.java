package com.coolcollege.intelligent.controller.store;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.device.dto.DeviceChannelYunMouDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.StoreInfoExportFieldEnum;
import com.coolcollege.intelligent.model.export.request.StoreExportInfoFileRequest;
import com.coolcollege.intelligent.model.export.request.StoreInfoExportRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.dto.*;
import com.coolcollege.intelligent.model.store.queryDto.StoreGroupQueryDTO;
import com.coolcollege.intelligent.model.store.queryDto.StoreQueryDTO;
import com.coolcollege.intelligent.model.store.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.requestBody.store.StoreCoverRequestBody;
import com.coolcollege.intelligent.service.requestBody.store.StoreRequestBody;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName StoreController
 * @Description 用一句话描述什么
 */
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/stores","/v3/enterprises/{enterprise-id}/stores" })
@BaseResponse
@Slf4j
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Resource
    private ExportUtil exportUtil;
    @Resource
    private EnterpriseStoreSettingService enterpriseStoreSettingService;


    private static final String EXPORT_TITLE = "填写须知：" +
            "\n1.请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败。" +
            "\n2.门店id栏不要动，门店id栏不要动，门店id栏不要动（重要的事情说三遍）。" +
            "\n3.操作方法：保留需要导入的门店，删除不需要导入的门店，保存后进行导入。";


    /**
     * 模板下载
     *
     * @Title: loadingRateExport
     * @author Aaron
     */
    @GetMapping("/download_template")
    public void loadingRateExport(HttpServletResponse response) {
        try {
            InputStream resourceAsStream = StoreController.class.getClassLoader().getResourceAsStream("template/批量新增门店导入模板.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("content-disposition", "attachment;");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            log.info("模板下载异常:" + e.getMessage());
        }
    }

    /**
     * 门店列表
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/list")
    public Object getPageStoreList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               StoreQueryDTO storeQueryDTO,
                                   @RequestParam(value = "area_id", required = false) String areaId) {

        storeQueryDTO.setStore_area(areaId);
        DataSourceHelper.changeToMy();
        Map<String, Object> aa = storeService.getPageInfoStores(enterpriseId, storeQueryDTO);
        //给门店列表设置门店完善度字段
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingService.getEnterpriseStoreSetting(enterpriseId);
        List<StoreDTO> storeList = (List<StoreDTO>) aa.get("list");
        if (CollectionUtils.isNotEmpty(storeList)) {
            storeList.forEach(store -> {
                store.setIsPerfect(enterpriseStoreSettingService.getStorePerfection(store, storeSettingDO.getPerfectionField()));
            });
        }
        return ResponseResult.success(aa);
    }

    @PostMapping("/get")
    public Map<String, Object> getStoreList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                            @RequestBody(required = false) StoreQueryDTO storeQueryDTO) {
        DataSourceHelper.changeToMy();
        return storeService.getPageInfoStores(enterpriseId, storeQueryDTO);
    }

    @GetMapping("select_store_key")
    public Object selectStoreKey(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.changeToMy();
        return storeService.storeGroupByKey(enterpriseId);
    }

    @PostMapping("/export")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "门店-门店列表-门店档案")
    public ResponseResult exportStoreList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,@RequestBody StoreInfoExportRequest request) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_STORE_BASE);
        if(org.apache.shiro.util.CollectionUtils.isEmpty(request.getFieldList())){
            request.setFieldList(StoreInfoExportFieldEnum.nameList());
        }
        request.setEnterpriseId(enterpriseId);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    @PostMapping("/export/base")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "门店-门店列表-门店档案")
    public ResponseResult<ImportTaskDO> exportStoreBaseList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,@RequestBody StoreExportInfoFileRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.exportBaseInfo(enterpriseId,request,UserHolder.getUser()));
    }

    @PostMapping("/add")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_ADD, operateDesc = "新增门店")
    @SysLog(func = "添加门店", opModule = OpModuleEnum.STORE_FILE, opType = OpTypeEnum.INSERT)
    public Object addStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                           @RequestBody(required = false) StoreRequestBody storeRequestBody) {
        DataSourceHelper.changeToMy();
        return storeService.addStore(enterpriseId, storeRequestBody);
    }

    @PostMapping("/add_spacial")
    @Deprecated
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_ADD, operateDesc = "批量插入特殊的门店列表")
    public Object addSpacialStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                           String dbName,
                           @RequestBody(required = false) List<StoreRequestBody> storeRequestBodys) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        storeService.batchInsertSpecialStore(enterpriseId, storeRequestBodys);
        return true;
    }

    @PostMapping("/delete")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除门店")
    @SysLog(func = "批量删除门店", opModule = OpModuleEnum.STORE_FILE, opType = OpTypeEnum.BATCH_DELETE)
    public Object deleteStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                              @RequestBody(required = false) Map<String, Object> map) {
        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能删除");
        }

        DataSourceHelper.changeToMy();
        return storeService.deleteStoreByStoreIds(enterpriseId, map,Boolean.TRUE);
    }


    @PostMapping("/{store-id}/update")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新门店")
    @SysLog(func = "编辑", opModule = OpModuleEnum.STORE_FILE, opType = OpTypeEnum.EDIT, preprocess = true)
    public Object updateStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                              @PathVariable(value = "store-id", required = false) String storeId,
                              @RequestBody(required = false) StoreRequestBody storeRequestBody) {
        DataSourceHelper.changeToMy();
        storeRequestBody.setStore_id(storeId);
        return storeService.updateStore(enterpriseId, storeRequestBody, false);
    }

    @PostMapping("/lock")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "锁定门店")
    public Object lockStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                            @RequestBody(required = false) Map<String, Object> map) {
        DataSourceHelper.changeToMy();
        return storeService.lockStoreByStoreIds(enterpriseId, map);
    }

    /**
     * 门店详情查询
     * @param enterpriseId
     * @param storeId
     * @return
     */
    @GetMapping("/{store-id}/get")
    public Object queryStoreDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                   @PathVariable(value = "store-id", required = false) String storeId) {
        DataSourceHelper.changeToMy();
        return storeService.queryStoreDetail(enterpriseId, storeId);
    }

    @GetMapping("/{store-id}/xfsg/get")
    public Object queryXfsgStoreDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                   @PathVariable(value = "store-id", required = false) String storeId) {
        DataSourceHelper.changeToMy();
        return storeService.queryStoreDetail(enterpriseId, storeId);
    }


    @GetMapping("/mobile/get/{store-id}")
    public Object queryMobileStoreDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @PathVariable(value = "store-id", required = false) String storeId) {
        DataSourceHelper.changeToMy();
        return storeService.getStoreByStoreId(enterpriseId, storeId);
    }



    /**
     * 门店人员及职位列表
     * @param enterpriseId
     * @param storeId
     * @return
     */
    @GetMapping("storeUserPositionList")
    public Object getStoreUserList(@PathVariable(value = "enterprise-id") String enterpriseId,
                                   @RequestParam(value = "appType", required = false) String appType,
                                   String storeId) {
        DataSourceHelper.changeToMy();
        List<StoreUserDTO> storeUserDTOList =  storeService.getStoreUserPositionList(enterpriseId, storeId, null, null, null, appType);
        Map<String, List<StoreUserDTO>> storeUserDTOMap = ListUtils.emptyIfNull(storeUserDTOList).stream().collect(Collectors.groupingBy(k -> k.getUserId()));
        List<StoreUserDTO> result = Lists.newArrayList();
        for (String userId : storeUserDTOMap.keySet()) {
            List<StoreUserDTO> singleUserDTOList = storeUserDTOMap.get(userId);
            if(CollectionUtils.isEmpty(singleUserDTOList)){
                continue;
            }
            StoreUserDTO storeUserDTO = singleUserDTOList.get(0);
            List<String> positionNameList = ListUtils.emptyIfNull(singleUserDTOList)
                    .stream().map(StoreUserDTO::getPositionName).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(positionNameList)){
                storeUserDTO.setPositionName(String.join(Constants.COMMA, positionNameList));
            }
            result.add(storeUserDTO);
        }
        return result;
    }

    @GetMapping("/getUserListByStoreId")
    public ResponseResult<List<StoreUserDTO>> getUserListByStoreId(@PathVariable(value = "enterprise-id") String enterpriseId,@RequestParam("storeId") String storeId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getUserListByStoreId(enterpriseId, storeId));
    }

    /**
     * 门店人员及职位列表
     * @param enterpriseId
     * @param storeId
     * @return
     */
    @GetMapping("storeUserPositionListPage")
    public Object getStoreUserListPage(@PathVariable(value = "enterprise-id") String enterpriseId,
                                   String storeId,
                                   String userName,
                                   @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize,
                                   @RequestParam(value = "page_num", required = false, defaultValue = "1") Integer pageNum) {
        DataSourceHelper.changeToMy();
        return storeService.getStoreUserPositionListPage(enterpriseId, storeId, userName, pageSize, pageNum);
    }



    /**
     * 收藏和取消收藏门店
     *
     * @param enterpriseId
     * @param storeDTO
     * @return
     */
    @PostMapping("/collectStore")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_ADD, operateDesc = "收藏和取消收藏门店")
    public Object collectOrCancelStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestParam(value = "user_id") String userId,
                                       @RequestBody StoreDTO storeDTO) {
        DataSourceHelper.changeToMy();
//        DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_1");
        return storeService.collectStore(enterpriseId, storeDTO.getStoreId(), userId);
    }








    /**
     * 查询区域内用户下的门店信息（不分页）
     *
     * @param enterpriseId
     * @param storeQueryDTO
     * @return
     */
    @PostMapping(path = "/getStoresByUserAndRegionId")
    public Object getStoresByUserAndRegionId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                             @RequestParam(value = "user_id", required = false) String userId,
                                             @RequestBody(required = false) StoreQueryDTO storeQueryDTO) {

        DataSourceHelper.changeToMy();

        return ResponseResult.success(storeService.getStoresByUserAndRegionId(enterpriseId,storeQueryDTO.getRegionIds()));
    }

    /**
     * 获取用户收藏接口
     *
     * @param enterpriseId
     * @param userId
     * @param pageSize
     * @param pageNum
     * @param isCollect
     * @param storeName
     * @return
     */
    @GetMapping("/getCollectStoresByUser")
    public Object getCollectStoresByUser(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestParam(value = "user_id", required = false) String userId,
                                         @RequestParam(value = "page_size", required = false, defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "page_num", required = false, defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "isCollect", required = false) Boolean isCollect,
                                         @RequestParam(value = "store_name", required = false) String storeName,
                                         @RequestParam(value = "longitude",required= false) String longitude,
                                         @RequestParam(value = "latitude",required= false) String latitude,
                                         @RequestParam(value = "range",required = false)Long range,
                                         @RequestParam(value = "storeStatusList",required = false)List<String> storeStatusList) {
        DataSourceHelper.changeToMy();
        StoreQueryDTO storeQueryDTO = new StoreQueryDTO();
        storeQueryDTO.setUser_id(userId);
        storeQueryDTO.setPage_num(pageNum);
        storeQueryDTO.setPage_size(pageSize);
        storeQueryDTO.setIsCollect(isCollect);
        storeQueryDTO.setStore_name(storeName);
        storeQueryDTO.setLongitude(longitude);
        storeQueryDTO.setLatitude(latitude);
        storeQueryDTO.setRange(range);
        storeQueryDTO.setStoreStatusList(storeStatusList);
        return ResponseResult.success(storeService.getCollectStoresByUser(enterpriseId, storeQueryDTO));

    }

    /**
     * 批量移动门店
     *
     * @param enterpriseId
     * @param moveDTO
     * @return
     */
    @PostMapping("batchMoveStore")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_ADD, operateDesc = "批量移动门店")
    @SysLog(func = "批量移动门店", opModule = OpModuleEnum.STORE_FILE, opType = OpTypeEnum.BATCH_MOVE)
    public Object moveStoreBatch(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                 @RequestBody StoreBatchMoveDTO moveDTO) {
        DataSourceHelper.changeToMy();
//       DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");
        return storeService.batchMoveStore(enterpriseId, moveDTO.getAreaId(), moveDTO.getStoreIds());
    }



    /**
     * 根据多个门店id获取门店列表
     *
     * @param enterpriseId
     * @param storeIds
     * @return
     */
    @GetMapping("/getStoreListByStoreIds")
    public Object getStoreListByStoreIds(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestParam(value = "storeIds", required = false) String storeIds,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        DataSourceHelper.changeToMy();
        return storeService.getStoreListByStoreIds(enterpriseId, storeIds, pageSize, pageNum);
    }

    /**
     * 分页查询用户下的门店列表
     *
     * @param enterpriseId
     * @param userId
     * @param pageSize
     * @param pageNum
     * @param regionIds
     * @param storeName
     * @return
     */
    @GetMapping("/getStoreListByPage")
    public Object getStoreListByPage(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestParam(value = "userId", required = false) String userId,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                     @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "recursion", required = false, defaultValue = "true") Boolean recursion,
                                     @RequestParam(value = "regionIds", required = false) String regionIds,
                                     @RequestParam(value = "storeName", required = false) String storeName) {
        DataSourceHelper.changeToMy();
        //DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");
        return ResponseResult.success(storeService.getStoreListByPage(enterpriseId, userId, recursion, pageSize, pageNum, regionIds, storeName));
    }

    /**
     * 根据门店ID获取门店下的相关人员信息
     *
     * @param enterpeiseId
     * @param storeId
     * @return
     */
    @GetMapping("/getStorePersons")

    public Object getStorePersons(@PathVariable(value = "enterprise-id", required = true) String enterpeiseId,
                                  @RequestParam(value = "store_id", required = false) String storeId) {
        log.info("getStorePersons");
        DataSourceHelper.changeToMy();

        return ResponseResult.success(storeService.getStorePersons(enterpeiseId, storeId));
    }


    /**
     * 根据门店ID获取门店下的相关人员信息
     *
     * @param enterpeiseId
     * @param storeId
     * @return
     */
    @GetMapping("/getPersonByStoreId")

    public Object getPersonByStoreId(@PathVariable(value = "enterprise-id", required = true) String enterpeiseId,
                                     @RequestParam(value = "store_id", required = false) String storeId
    ) {
        DataSourceHelper.changeToMy();
        //DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");

        return ResponseResult.success(storeService.getPersonByStoreId(enterpeiseId, storeId));
    }

    @PostMapping("/get_store_name")
    public Object getStoreNameById(@PathVariable(value = "enterprise-id")String enterpriseId,
                                   @RequestBody StoreIdVO storeIds){
        DataSourceHelper.changeToMy();
//        DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");
        return storeService.getStoreNameById(enterpriseId, storeIds.getStoreIds());
    }


    @GetMapping("/effectiveStore")

    public ResponseResult getExistStoreByStoreId(@PathVariable(value = "enterprise-id")String enterpriseId,
                                                 @RequestParam(value = "store_id",required = false) String storeId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getExistStoreByStoreId(enterpriseId, storeId));
    }

    /**
     * 删除分组信息
     * @param eId
     * @param entity
     * @return
     */
    @PostMapping("/deleteStoreGroup")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除分组信息")
    public Boolean deleteStoreGroup(@PathVariable("enterprise-id")String eId, @RequestBody StoreGroupDO entity){
        DataSourceHelper.changeToMy();
        return storeService.deleteStoreGroup(eId,entity);
    }

    /**
     * 添加分组
     * @param eId 企业id
     * @param storeGroupDTO
     * @return
     */
    @PostMapping("/addStoreGroup")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_ADD, operateDesc = "添加分组")
    @SysLog(func = "新建分组", opModule = OpModuleEnum.STORE_GROUP, opType = OpTypeEnum.INSERT)
    public ResponseResult addStoreGroup(@PathVariable("enterprise-id")String eId,@RequestBody StoreGroupDTO storeGroupDTO){
        DataSourceHelper.changeToMy();
        return storeService.addStoreGroup(eId,storeGroupDTO, UserHolder.getUser().getUserId());
    }

    /**
     * 更新分组信息XX
     * @param eId
     * @param storeGroupDTO
     * @return
     */
    @PostMapping("/updateStoreGroup")
    @SysLog(func = "编辑分组", opModule = OpModuleEnum.STORE_GROUP, opType = OpTypeEnum.EDIT, preprocess = true)
    public Boolean updateStoreGroup(@PathVariable("enterprise-id")String eId,@RequestBody StoreGroupDTO storeGroupDTO){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        storeGroupDTO.setUserName(user.getUserId());
        return CollectionUtils.isEmpty(storeService.updateStoreGroup(eId,storeGroupDTO));
    }

    /**
     * 清空分组门店
     * @param eId
     * @param storeGroupDTO
     * @return
     */
    @PostMapping("/clearGroupStore")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_DELETE, operateDesc = "清空分组门店")
    public ResponseResult clearGroupStore(@PathVariable("enterprise-id") String eId, @RequestBody StoreGroupDTO storeGroupDTO){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.clearGroupStore(eId, storeGroupDTO));
    }

    /**
     * 获取分组信息
     * @param eId
     * @return
     */
    @GetMapping("/getStoreGroup")
    public Object getStoreGroup(@PathVariable("enterprise-id") String eId,@RequestParam(value = "group_name",required = false)String groupName){
        DataSourceHelper.changeToMy();
        return storeService.getStoreGroup(eId,groupName);
    }

    /**
     * 获取分组列表
     * @param enterpriseId
     * @param groupName
     * @param isCount
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/getStoreGroupList")
    public ResponseResult getStoreGroupList(@PathVariable("enterprise-id") String enterpriseId,
                                    @RequestParam(value = "groupName",required = false,defaultValue = "")String groupName,
                                    @RequestParam(value = "isCount",required = false,defaultValue = "false") Boolean isCount ,
                                    @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize ,
                                    @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "authUserId",required = false) String authUserId){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String userId = user.getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if(StringUtils.isNotBlank(authUserId)){
            userId = authUserId;
        }
        PageInfo pageInfo =  storeService.getStoreGroupList(enterpriseId,groupName,isCount,userId,pageNum,pageSize);
        return ResponseResult.success( PageHelperUtil.getPageInfo(pageInfo));
    }

    @PostMapping("/modifyStoreGroup")
    public ResponseResult modifyStoreGroup(@PathVariable("enterprise-id") String enterpriseId, @RequestBody StoreGroupQueryDTO storeGroupQueryDTO){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(storeService.modifyStoreGroup(enterpriseId, userId,storeGroupQueryDTO));
    }
    @GetMapping("/getGroupInfo")
    public ResponseResult getGroupInfo(@PathVariable("enterprise-id") String enterpriseId,@RequestParam("groupId") @NotNull String groupId,
                                       @RequestParam(value = "storeStatusList", required = false) List<String> storeStatusList){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        StoreGroupVO storeGroupVO = storeService.getGroupInfo(enterpriseId,userId,groupId, storeStatusList);
        return ResponseResult.success(storeGroupVO);
    }

    @GetMapping("/listByGroupId")
    public ResponseResult<List<StoreDTO>> listByGroupId(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestParam("groupId") @NotNull String groupId,
                                                        @RequestParam(value = "storeName",required = false,defaultValue = "")String storeName,
                                                        @RequestParam(value = "authUserId",required = false) String authUserId){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if(StringUtils.isNotBlank(authUserId)){
            userId = authUserId;
        }
        List<StoreDTO> storeDTOList = storeService.listByGroupId(enterpriseId, userId, groupId, storeName);
        return ResponseResult.success(storeDTOList);
    }

    @GetMapping("/getStoreListByGroupId")
    @ApiOperation("根据分组获取门店列表")
    public ResponseResult getStoreListByGroupId(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestParam(value = "keywords",required = false)  String keywords,
                                                        @RequestParam(value = "storeId",required = false)  String storeId,
                                                        @RequestParam(value = "pageNum",defaultValue = "1")  Integer pageNum,
                                                        @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                                        @RequestParam("groupId") @NotNull String groupId){
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        List<StoreDeviceVO> storeDTOList = storeService.getStoreListByGroupId(enterpriseId, keywords, storeId, pageNum, pageSize, userId, groupId);
        if (CollectionUtils.isNotEmpty(storeDTOList)) {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(storeDTOList)));
        } else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>()));
        }
    }

    /**
     * 批量删除分组
     */
    @PostMapping("/batchDeleteGroup")
    @OperateLog(operateModule = CommonConstant.Function.STORE, operateType = CommonConstant.LOG_DELETE, operateDesc = "批量删除分组")
    @SysLog(func = "删除分组", opModule = OpModuleEnum.STORE_GROUP, opType = OpTypeEnum.DELETE, preprocess = true)
    public ResponseResult batchDeleteGroup(@PathVariable("enterprise-id") String enterpriseId,@RequestBody StoreGroupDTO storeGroupDTO){
        DataSourceHelper.changeToMy();
        storeService.batchDeleteGroup(enterpriseId,storeGroupDTO.getGroupIdList());
        return ResponseResult.success(true);
    }

    @GetMapping("/getDeviceStore")
    public ResponseResult getDeviceStore(@PathVariable("enterprise-id") String enterpriseId,
                                         @RequestParam(value = "keywords",required = false)  String keywords,
                                         @RequestParam(value = "storeId",required = false)  String storeId,
                                         @RequestParam(value = "pageNum",defaultValue = "1")  Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "deviceTypeStr",required = false)  String deviceTypeStr,
                                         @RequestParam(value = "hasReturnTask",defaultValue = "false")  Boolean hasReturnTask){
        DataSourceHelper.changeToMy();
        List<StoreDeviceVO> deviceStore = storeService.getDeviceStore(enterpriseId, keywords, pageNum, pageSize, deviceTypeStr,hasReturnTask,storeId);
        if(CollectionUtils.isNotEmpty(deviceStore)){
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(deviceStore)));
        }else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>()));
        }
    }
    @GetMapping("/listStore")
    public ResponseResult listStore(@PathVariable("enterprise-id") String enterpriseId,
                                    @RequestParam(value = "storeName",required = false) String storeName,
                                    @RequestParam(value = "pageNum",defaultValue = "1",required = false) Integer pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "10",required = false) Integer pageSize,
                                    @RequestParam(value = "hasDevice",required = false,defaultValue = "false") Boolean hasDevice,
                                    @RequestParam(value = "hasCollection",required = false,defaultValue = "false") Boolean hasCollection,
                                    @RequestParam(value = "longitude",required= false) String longitude,
                                    @RequestParam(value = "latitude",required= false) String latitude,
                                    @RequestParam(value = "range",required = false)Long range,
                                    @RequestParam(value = "storeStatusList",required = false)List<String> storeStatusList,
                                    @RequestParam(value = "regionId",required = false)String regionId){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        PageInfo<StoreAndDeviceVO> pageInfo = storeService.listStore(enterpriseId,storeName,pageNum,pageSize,hasDevice,hasCollection,user,longitude,latitude,range,storeStatusList,regionId);
        //给门店信息中添加一个门店信息完善度的字段
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingService.getEnterpriseStoreSetting(enterpriseId);
        if (CollectionUtils.isNotEmpty(pageInfo.getList())) {
            pageInfo.getList().forEach(page -> {
                StoreDO store = page.getStore();
                store.setIsPerfect(enterpriseStoreSettingService.getStorePerfection(store, storeSettingDO.getPerfectionField()));
            });
        }
        return ResponseResult.success(pageInfo);

    }


    @GetMapping("/list/store/new")
    public ResponseResult<PageVO<StoreBaseVO>> listStoreNew(@PathVariable("enterprise-id") String enterpriseId,
                                    @RequestParam(value = "storeName",required = false) String storeName,
                                    @RequestParam(value = "pageNum",defaultValue = "1",required = false) Integer pageNum,
                                    @RequestParam(value = "pageSize",defaultValue = "10",required = false) Integer pageSize){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        List<StoreBaseVO> storeBaseVOList = storeService.listStoreNew(enterpriseId, storeName, pageNum, pageSize, user);
        if(CollectionUtils.isNotEmpty(storeBaseVOList)){
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>(storeBaseVOList)));
        }else {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>()));
        }
    }

    @GetMapping("groupStore")
    public ResponseResult<PageInfo<StoreBaseVO>> groupStore(@PathVariable("enterprise-id") String enterpriseId,
                                                    @RequestParam(value = "pageSize") Integer pageSize,
                                                    @RequestParam(value = "pageNum") Integer pageNum,
                                                    @RequestParam(value = "groupId") String groupId){
        DataSourceHelper.changeToMy();
        PageInfo<StoreBaseVO> pageInfo = storeService.groupStore(enterpriseId,pageNum,pageSize,groupId, UserHolder.getUser().getUserId());
        return ResponseResult.success(pageInfo);
    }

    @PostMapping("storeCover")
    public ResponseResult<PageVO<StoreCoverVO>> storeCover(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody StoreCoverRequestBody requestBody){
        DataSourceHelper.changeToMy();
        PageInfo<StoreCoverVO> storeBaseVoList = storeService.storeCover(enterpriseId,requestBody);
        return ResponseResult.success(PageHelperUtil.getPageVO(storeBaseVoList));
    }
    @GetMapping("/group/all")
    public ResponseResult<List<String>> getGroupStoreAll(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestParam(value = "groupId") String groupId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getGroupStoreAll(enterpriseId,groupId,UserHolder.getUser()));
    }

    @GetMapping("/address")
    public ResponseResult<String> getAddress(@PathVariable("enterprise-id") String enterpriseId, @RequestParam(value = "lat") String lat,
                                             @RequestParam(value = "lng") String lng){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getAddress(enterpriseId, lat,lng));
    }

    @GetMapping("/getStoreCountAndLimitCount")
    public ResponseResult<StoreCountVO> getStoreCountAndLimitCount(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getStoreCountAndLimitCount(enterpriseId));
    }

    @ApiOperation(value = "门店分组导出")
    @GetMapping("/group/exportByGroupId")
    @SysLog(func = "导出门店", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "门店-门店列表-门店分组")
    public ResponseResult<ImportTaskDO> exportByGroupId(@PathVariable("enterprise-id") String enterpriseId,
                                                         @RequestParam(value = "groupId") String groupId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.exportByGroupId(enterpriseId, groupId, UserHolder.getUser()));
    }

    /**
    * @Description:  华莱士---山东云眸设备与门店对应
    * @Param: []
    * @Author: tangziqi
    * @Date: 2023/5/29~13:36
    */

    @ApiOperation(value = "华莱士---山东云眸设备与门店对应")
    @GetMapping("/yunmou/monitor")
    public Boolean yunMouMonitor(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return storeService.yunMouMonitorCutIn();
    }

    @ApiOperation(value = "华莱士---山东云眸设备解密")
    @GetMapping("/monitor/decode")
    public Boolean yunMouMonitorDecode(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return storeService.yunMouMonitorDecode();
    }

    @ApiOperation("新增门店并给用户授权")
    @PostMapping("/addStoreAndAuth")
    public ResponseResult addStoreAndAuth(@PathVariable("enterprise-id") String enterpriseId,
                                          @RequestBody StoreAddAndAuthDTO request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.addStoreAndAuthUser(enterpriseId, request));
    }
}
