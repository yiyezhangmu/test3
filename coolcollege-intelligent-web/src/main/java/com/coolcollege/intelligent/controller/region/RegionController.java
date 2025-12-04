package com.coolcollege.intelligent.controller.region;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.dto.openApi.vo.RegionListVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.region.request.ExternalRegionExportRequest;
import com.coolcollege.intelligent.model.region.response.RegionStoreListResp;
import com.coolcollege.intelligent.model.region.vo.RegionPathNameVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreListDTO;
import com.coolcollege.intelligent.model.store.queryDto.StoreInRegionRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.requestBody.region.*;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName RegionController
 * @Description 区域
 */
@Api(tags = "区域管理")
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/regions","/v3/enterprises/{enterprise-id}/regions"})
@BaseResponse
@Slf4j
public class RegionController {

    @Autowired
    private RegionService regionService;

    @Autowired
    private EnterpriseStoreSettingService enterpriseStoreSettingService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    /**
     * 根据名字查询区域节点集合
     *
     * @param eid
     * @param parentId        当前节点ID
     * @param queryStoreCount 是否查询门店数量
     * @return
     */
    @GetMapping(path = "/tree")
    public Object getRegionTree(@PathVariable(value = "enterprise-id", required = true) String eid,
                                @RequestParam(name = "parent_id", required = false, defaultValue = "1") String parentId,
                                @RequestParam(name = "query_store_count", required = true, defaultValue = "false") String queryStoreCount,
                                @RequestParam(value = "isExternalNode", required = false) Boolean isExternalNode,
                                @RequestParam(name = "user_id", required = false) String userId) {

        RegionQueryDTO regionQueryDTO = new RegionQueryDTO();
        regionQueryDTO.setQueryStoreCount(queryStoreCount);
        regionQueryDTO.setRegionId(parentId);
        regionQueryDTO.setUserId(userId);
        regionQueryDTO.setIsExternalNode(isExternalNode);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.getRegionTree(eid, regionQueryDTO,UserHolder.getUser()));
    }

    /**
     * 获取子列表
     *
     * @param eid
     * @param pid
     * @return
     */
    @GetMapping("childList")
    public ResponseResult<List<RegionChildDTO>> getRegionByParentId(@PathVariable(value = "enterprise-id") String eid, String pid,
                                                                    @RequestParam(value = "hasStore", required = false, defaultValue = "false") boolean hasStore,
                                                                    @RequestParam(value = "hasPerson", required = false, defaultValue = "false") boolean hasPerson,
                                                                    @RequestParam(value = "isExternalNode", required = false) Boolean isExternalNode,
                                                                    @RequestParam(value = "regionId", required = false) String regionId,
                                                                    @RequestParam(value = "hasDefaultGrouping", required = false, defaultValue = "false") boolean hasDefaultGrouping,
                                                                    @RequestParam(value = "hasAuth", required = false, defaultValue = "false") boolean hasAuth) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(regionService.getRegionByParentId(eid, UserHolder.getUser().getUserId(),pid,hasStore,hasPerson,hasDefaultGrouping, hasAuth, enterpriseConfigDO.getAppType(), isExternalNode, regionId));
    }
    /**
     * 模糊获取区域和门店
     *
     * @param eid
     * @param name
     * @return
     */
    @GetMapping("regionAndStore")
    public Object getRegionAndStore(@PathVariable(value = "enterprise-id") String eid, String name) {
        DataSourceHelper.changeToMy();
        return regionService.getRegionAndStore(eid, name,UserHolder.getUser());
    }


    /**
     * 获取区域下的门店
     * @param eid
     * @param storeInRegionRequest
     * @return
     */
    @PostMapping("storeInRegion")
    public ResponseResult<PageInfo<StoreListDTO>> getStoreInRegion(@PathVariable(value = "enterprise-id") String eid,
                                                                   @RequestBody StoreInRegionRequest storeInRegionRequest) {
        DataSourceHelper.changeToMy();
        //给门店列表设置门店完善度字段
        PageInfo<StoreListDTO> storeInRegion = regionService.getStoreInRegion(eid, UserHolder.getUser().getUserId(), storeInRegionRequest);
        return ResponseResult.success(storeInRegion);
    }


    /**
     * 根据名字查询区域节点集合
     *
     * @param eid 当前节点ID
     * @return
     */
    @GetMapping(path = "/updatestore_tree")
    public ResponseResult<RegionStoreDTO> getRegionTree(@PathVariable(value = "enterprise-id") String eid,
                                                        @RequestParam(value = "user_id", required = false) String userId) {

        return ResponseResult.success(regionService.getRegionStore(eid, userId));
    }

    /**
     * 根据名字查询区域节点集合
     *
     * @param eid 当前节点ID
     * @return
     */
    @GetMapping(path = "/regionStoreTreeAsync")
    public ResponseResult<RegionStoreDTO> getRegionTreeAsync(@PathVariable(value = "enterprise-id") String eid,
                                                             @RequestParam(value = "user_id", required = false) String userId) {

        return ResponseResult.success(regionService.getRegionStore(eid, userId));
    }


    /**
     * 查询区域集合
     *
     * @param eid
     * @param name
     * @return
     */
    @GetMapping(path = "")
    public Object getRegionListByName(@PathVariable(value = "enterprise-id", required = true) String eid,
                                      @RequestParam(name = "name", required = false, defaultValue = "") String name) {

        DataSourceHelper.changeToMy();
        return new RegionSearchRespDTO(regionService.getRegionListByName(eid, name));
    }

    /**
     * 查询区域集合
     *
     * @param eid
     * @param name
     * @return
     */
    @GetMapping(path = "page")
    public PageVO getRegionListByPage(@PathVariable(value = "enterprise-id", required = true) String eid,
                                      @RequestParam(name = "name", required = false, defaultValue = "") String name,
                                      @RequestParam(name = "page_size", defaultValue = "20") Integer pageSize,
                                      @RequestParam(name = "page_num", defaultValue = "1") Integer pageNum) {

        DataSourceHelper.changeToMy();
        return regionService.getRegionListByPage(eid, name, pageNum, pageSize);
    }

    /**
     * 新增
     *
     * @param eid
     * @param regionRequestBody
     * @return
     */
    @PostMapping(path = "/add")
    @OperateLog(operateModule = CommonConstant.Function.REGION, operateType = CommonConstant.LOG_ADD, operateDesc = "新增区域")
    @SysLog(func = "新增区域", opModule = OpModuleEnum.SETTING_REGION_STORE, opType = OpTypeEnum.INSERT)
    public ResponseResult<String> addRegion(@PathVariable(value = "enterprise-id", required = true) String eid,
                            @RequestBody RegionRequestBody regionRequestBody) {

        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.addRegion(eid, regionRequestBody));
    }

    /**
     * 删除
     *
     * @param eid
     * @param regionId
     * @return
     */
    @PostMapping(path = "/{region-id}/delete")
    @OperateLog(operateModule = CommonConstant.Function.REGION, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除区域")
    @SysLog(func = "删除区域", opModule = OpModuleEnum.SETTING_REGION_STORE, opType = OpTypeEnum.DELETE, preprocess = true)
    public Object deleteRegion(@PathVariable(value = "enterprise-id", required = true) String eid,
                               @PathVariable(value = "region-id", required = true) String regionId) {

        DataSourceHelper.changeToMy();
        return regionService.deleteRegion(eid, regionId);
    }

    /**
     * 修改
     *
     * @param eid
     * @param regionId
     * @param regionRequestBody
     * @return
     */
    @PostMapping(path = "/{region-id}/update")
    @OperateLog(operateModule = CommonConstant.Function.REGION, operateType = CommonConstant.LOG_UPDATE, operateDesc = "修改区域")
    @SysLog(func = "编辑区域", opModule = OpModuleEnum.SETTING_REGION_STORE, opType = OpTypeEnum.EDIT)
    public RegionDTO updateRegion(@PathVariable(value = "enterprise-id", required = true) String eid,
                                  @PathVariable(value = "region-id", required = true) String regionId,
                                  @RequestBody RegionRequestBody regionRequestBody) {

        DataSourceHelper.changeToMy();
        regionRequestBody.setRegion_id(regionId);
        return regionService.updateRegion(eid, regionRequestBody);
    }

    /**
     * 修改节点的排序
     * @param eid
     * @param regionOrderNumRequest
     * @return
     */
    @PostMapping(path = "/updateOrderNum")
    public Boolean updateOrderNum(@PathVariable(value = "enterprise-id", required = true) String eid,
                               @RequestBody RegionOrderNumRequest regionOrderNumRequest) {

        DataSourceHelper.changeToMy();
        return regionService.updateOrderNum(eid,regionOrderNumRequest.getRegionIds());
    }

    /**
     * 获取门店的groupId
     *
     * @param eid
     * @param storeId
     * @return
     */
    @GetMapping("store_group_id")
    public Object getStoreGroupId(@PathVariable(value = "enterprise-id") String eid, String storeId) {
        DataSourceHelper.changeToMy();
        return regionService.getGroupIdByStore(eid, storeId);
    }

    @GetMapping("regionStoreList")
    public ResponseResult regionStoreList(@PathVariable(value = "enterprise-id") String eid,
                                          @RequestParam(value = "parentId", required = false) Long parentId,
                                          @RequestParam(value = "hasDevice",required = false,defaultValue = "false") Boolean hasDevice,
                                          @RequestParam(value = "hasCollection",required = false,defaultValue = "false") Boolean hasCollection) {

        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        RegionStoreListResp resp = regionService.regionStoreList(eid, parentId, user, hasDevice, hasCollection);
        //添加门店信息是否完善字段
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingService.getEnterpriseStoreSetting(eid);
        if (CollectionUtils.isNotEmpty(resp.getStoreList())) {
            resp.getStoreList().forEach(vo -> {
                StoreDO store = vo.getStore();
                store.setIsPerfect(enterpriseStoreSettingService.getStorePerfection(store, storeSettingDO.getPerfectionField()));
            });
        }
        return ResponseResult.success(resp);
    }


    @ApiOperation("获取区域全路径")    @ApiImplicitParams({
            @ApiImplicitParam(name = "regionId", value = "区域id", dataType = "Long")
    })
    @GetMapping("getAllRegionName")
    public ResponseResult<RegionPathNameVO> getAllRegionName(@PathVariable(value = "enterprise-id") String eid, @RequestParam("regionId") Long regionId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.getAllRegionName(eid, regionId));
    }


    @GetMapping("setRegionToStore")
    public ResponseResult<Boolean> setRegionToStore(@PathVariable(value = "enterprise-id") String eid,
                                          @RequestParam(value = "regionId", required = true) Long regionId) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.setRegionToStore(eid,String.valueOf(regionId),user));
    }

    @PostMapping("addPersonal")
    @SysLog(func = "新增人员", opModule = OpModuleEnum.SETTING_REGION_STORE, opType = OpTypeEnum.INSERT_PERSON, preprocess = true)
    public ResponseResult<Boolean> addPersonal(@PathVariable(value = "enterprise-id") String eid,
                                               @RequestBody RegionAddPersonalRequest request) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.addPersonal(eid,request.getRegionId(),request.getUserIds(),user));
    }



    /**
     * 选择组件中部门筛选
     * @param enterpriseId
     * @param pageNum
     * @param pageSize
     * @param keyword
     * @return
     */
    @GetMapping("/getSelectionNodeByKeyword")
    public ResponseResult getSelectionNodeByKeyword(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                          @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                                          @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                                          @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.getSelectionRegionByKeyword(enterpriseId,keyword,pageNum,pageSize));
    }

    @PostMapping("/externalRegionExport")
    public ResponseResult externalRegionExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody ExternalRegionExportRequest request) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_USER_INFO);
        request.setUser(UserHolder.getUser());
        request.setEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        ImportTaskDO importTaskDO = regionService.externalRegionExport(request);
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 查询当前登录人的部门列表
     *
     * @param enterpriseId 当前企业
     * @return 部门列表
     */
    @ApiOperation("查询当前登录人的部门列表")
    @GetMapping("/currentUserRegion")
    public ResponseResult<List<RegionListVO>> currentUserRegion(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.currentUserRegion(enterpriseId,user));
    }

}
