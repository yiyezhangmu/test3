package com.coolcollege.intelligent.controller.boss.manage;

import cn.hutool.core.collection.ListUtil;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.dao.boss.dao.BossLoginEnterpriseRecordDao;
import com.coolcollege.intelligent.model.boss.BossLoginEnterpriseRecordDO;
import com.coolcollege.intelligent.model.boss.dto.AdminLoginEnterpriseDTO;
import com.coolcollege.intelligent.model.boss.request.BossEnterpriseExportRequest;
import com.coolcollege.intelligent.model.boss.vo.EnterpriseTagVO;
import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO;
import com.coolcollege.intelligent.model.bosspackage.vo.EnterpriseCurrentPackageDetailVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossExportDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseBossVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCorpNameVO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.boss.BossUserService;
import com.coolcollege.intelligent.service.bosspackage.EnterprisePackageService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * @author byd
 * @date 2021-01-29 20:36
 */
@RestController
@RequestMapping({"/boss/manage/enterprise", "/v3/enterprise"})
@BaseResponse
@Slf4j
public class BossEnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private BossUserService bossUserService;

    @Autowired
    private EnterprisePackageService enterprisePackageService;

    @Autowired
    private BossLoginEnterpriseRecordDao bossLoginEnterpriseRecordDao;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;
    @Resource
    private StoreService storeService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    /**
     * 查询企业列表
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ResponseResult<PageVO<EnterpriseBossVO>> list(@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                       @RequestParam(value = "tag", required = false) String tag,
                                       @RequestParam(value = "appType", required = false) String appType,
                                       @RequestParam(value = "currentPackageId", required = false) Integer currentPackageId,
                                       @RequestParam(value = "isPersonal", required = false) Boolean isPersonal,
                                       @RequestParam(value = "isLeaveInfo", required = false) Boolean isLeaveInfo) {
        DataSourceHelper.reset();
        BossEnterpriseExportRequest param = new BossEnterpriseExportRequest();
        param.setPageSize(pageSize);
        param.setPageNumber(pageNumber);
        param.setName(name);
        param.setEnterpriseId(enterpriseId);
        param.setIsPersonal(isPersonal);
        param.setTag(tag);
        param.setAppType(appType);
        param.setCurrentPackageId(currentPackageId);
        param.setIsLeaveInfo(isLeaveInfo);
        return ResponseResult.success(enterpriseService.listEnterprise(param));
    }

    @PostMapping(value = "/list")
    public ResponseResult<PageVO<EnterpriseBossVO>> list(@RequestBody BossEnterpriseExportRequest param) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseService.listEnterprise(param));
    }

    @PostMapping(value = "/frozen")
    public ResponseResult frozenEnterprise(@RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                           @RequestParam(value = "isFrozen", required = false) Boolean isFrozen) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseService.frozenEnterprise(enterpriseId,isFrozen));
    }

    /**
     * 查询企业列表
     *
     * @return
     */
    @GetMapping(value = "/{enterpriseId}/detail")
    public ResponseResult detail(@PathVariable String enterpriseId) {
        DataSourceHelper.reset();
        Map<String, Object> result = new HashMap<>();
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        result.put("enterprise", enterpriseDO);
        result.put("enterpriseConfig", enterpriseConfigDO);
        return ResponseResult.success(result);
    }


    @GetMapping(value = "/{enterpriseId}/callAdmin")
    public ResponseResult callAdmin(@PathVariable String enterpriseId) {
        DataSourceHelper.reset();
        return ResponseResult.success(bossUserService.callAdmin(enterpriseId));
    }

    /**
     * 企业管理企业信息详情
     *
     * @param eId
     * @return
     */
    @GetMapping("/{enterprise-id}/getBusinessManagement")
    public ResponseResult getBusinessManagement(@PathVariable("enterprise-id") String eId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseService.getBusinessManagement(eId));
    }

    @PostMapping("/{enterprise-id}/saveBussinessManagement")
    public ResponseResult saveBussinessManagement(@PathVariable("enterprise-id") String eId, @RequestBody EnterpriseDTO entity) {
        DataSourceHelper.reset();
        boolean isSuc = enterpriseService.saveBussinessManagement(eId, entity);
        if(!isSuc){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "保存失败");
        }
        return ResponseResult.success(entity);
    }
    @GetMapping("/update/name")
    public ResponseResult updateEnterpriseName(@RequestParam(value = "enterpriseId") String enterpriseId,
                                           @RequestParam(value = "enterpriseName") String enterpriseName) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseService.updateEnterpriseName(enterpriseId,enterpriseName));
    }

    /**
     * 导出
     * @param name
     * @param enterpriseId
     * @param isPersonal
     * @return
     */
    @GetMapping(value = "/export")
    public void exportEnterprise(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                 @RequestParam(value = "isPersonal", required = false) Boolean isPersonal,
                                 @RequestParam(value = "tag", required = false) String tag,
                                 @RequestParam(value = "appType", required = false) String appType,
                                 @RequestParam(value = "currentPackageId", required = false) Integer currentPackageId,
                                 @RequestParam(value = "isLeaveInfo", required = false) Boolean isLeaveInfo,
                                 HttpServletResponse response) {
        DataSourceHelper.reset();
        BossEnterpriseExportRequest param = new BossEnterpriseExportRequest();
        param.setName(name);
        param.setEnterpriseId(enterpriseId);
        param.setIsPersonal(isPersonal);
        param.setTag(tag);
        param.setAppType(appType);
        param.setCurrentPackageId(currentPackageId);
        param.setIsLeaveInfo(isLeaveInfo);
        List<EnterpriseBossExportDTO> enterpriseList = enterpriseService.exportList(param);
        FileUtil.exportBigDataExcel(enterpriseList, "企业列表", "企业列表", EnterpriseBossExportDTO.class, "企业列表.xlsx", response);
    }

    @PostMapping(value = "/exportEnterprise")
    public void exportEnterprise(@RequestBody BossEnterpriseExportRequest param, HttpServletResponse response) {
        DataSourceHelper.reset();
        List<EnterpriseBossExportDTO> enterpriseList = enterpriseService.exportList(param);
        FileUtil.exportBigDataExcel(enterpriseList, "企业列表", "企业列表", EnterpriseBossExportDTO.class, "企业列表.xlsx", response);
    }

    @ApiOperation("企业列表-获得企业已有套餐详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业id", required = true)
    })
    @GetMapping(value = "/getEnterprisePackage")
    public ResponseResult<EnterpriseCurrentPackageDetailVO> getEnterprisePackage(@RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                                                                 HttpServletResponse response) {

        DataSourceHelper.reset();
        EnterpriseCurrentPackageDetailVO result = new EnterpriseCurrentPackageDetailVO();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        EnterpriseDO enterprise = enterpriseService.selectById(enterpriseId);
        if (config.getCurrentPackage() != null) {
            result.setPackageId(config.getCurrentPackage());
            EnterprisePackageDO enterprisePackageDO = enterprisePackageService.selectByPrimaryKey(config.getCurrentPackage());
            result.setPackageName(enterprisePackageDO.getPackageName());
        }
        result.setEnterpriseId(enterpriseId);
        result.setEnterpriseName(enterprise.getName());

        return ResponseResult.success(result);
    }

    @ApiOperation("企业列表-修改企业套餐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业id", required = true),
            @ApiImplicitParam(name = "packageId", value = "套餐id", required = true)
    })
    @GetMapping(value = "/changeEnterprisePackage")
    public ResponseResult changeEnterprisePackage(@RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                        @RequestParam(value = "packageId", required = false) Long packageId,
                                        HttpServletResponse response) {

        DataSourceHelper.reset();
        enterpriseConfigService.updateCurrentPackageByEnterpriseId(enterpriseId, packageId);
        return ResponseResult.success();
    }


    @ApiOperation("企业列表-获得企业标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业id", required = true)
    })
    @GetMapping(value = "/getEnterpriseTag")
    public ResponseResult<EnterpriseTagVO> getEnterpriseTag(@RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                                            HttpServletResponse response) {
        DataSourceHelper.reset();
        EnterpriseTagVO result = new EnterpriseTagVO();
        EnterpriseDO enterprise = enterpriseService.selectById(enterpriseId);
        result.setEnterpriseId(enterpriseId);
        result.setEnterpriseName(enterprise.getName());
        result.setTag(enterprise.getTag());
        return ResponseResult.success(result);
    }

    @ApiOperation("企业列表-修改企业标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enterpriseId", value = "企业id", required = true),
            @ApiImplicitParam(name = "tag", value = "标签", required = true)
    })
    @GetMapping(value = "/changeEnterpriseTag")
    public ResponseResult changeEnterpriseTag(@RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                                  @RequestParam(value = "tag", required = false) String tag,
                                                  HttpServletResponse response) {
        DataSourceHelper.reset();
        enterpriseService.updateEnterpriseTag(enterpriseId, tag);
        return ResponseResult.success();
    }


    @GetMapping(value = "/updateEnterpriseCSM")
    public ResponseResult updateEnterpriseCSM(@RequestParam(value = "enterpriseId") String enterpriseId,
                                              @RequestParam(value = "csm") String csm) {
        DataSourceHelper.reset();
        enterpriseService.updateEnterpriseCSM(enterpriseId, csm);
        return ResponseResult.success();
    }

    @ApiOperation("企业列表-超登企业记录")
    @PostMapping(value = "/recordLoginEnterprise")
    public ResponseResult recordLoginEnterprise(@RequestBody @Valid AdminLoginEnterpriseDTO loginEnterpriseDTO,
                                                  HttpServletResponse response) {
        DataSourceHelper.reset();
        BossLoginEnterpriseRecordDO recordDO = new BossLoginEnterpriseRecordDO();
        recordDO.setEnterpriseId(loginEnterpriseDTO.getEnterpriseId());
        recordDO.setUsername(loginEnterpriseDTO.getUsername());
        recordDO.setLoginTime(new Date());
        bossLoginEnterpriseRecordDao.insertSelective(recordDO);
        return ResponseResult.success();
    }

    /**
     * 开通酷学院
     * @param enterpriseId
     * @return
     */
    @GetMapping("/{enterprise-id}/openCoolCollegeAuth")
    public ResponseResult openCoolCollegeAuth(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("accessCoolCollege") Boolean accessCoolCollege){
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettingDO = new EnterpriseSettingDO();
        enterpriseSettingDO.setEnterpriseId(enterpriseId)
                .setAccessCoolCollege(accessCoolCollege);
        enterpriseSettingService.updateAccessCoolCollegeByEnterpriseId(enterpriseSettingDO);
        if (accessCoolCollege) {
            executor.execute(() -> {
                //异步开通酷学院
                coolCollegeIntegrationApiService.openCoolCollegeAuth(enterpriseId);
            });
        }
        return ResponseResult.success(Boolean.TRUE);
    }


    @ApiOperation("更新门店数量")
    @PostMapping(value = "/{enterprise-id}/updateLimitStoreCount")
    public ResponseResult updateLimitStoreCount(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("limitStoreCount")Integer limitStoreCount) {
        DataSourceHelper.reset();
        Integer result = enterpriseService.updateLimitStoreCount(enterpriseId, limitStoreCount);
        storeService.sendLimitStoreCountMessage(enterpriseId, limitStoreCount);
        return ResponseResult.success(result);
    }

    @ApiOperation("更新设备数量")
    @PostMapping(value = "/{enterprise-id}/updateDeviceCount")
    public ResponseResult updateDeviceCount(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("deviceCount")Integer deviceCount) {
        DataSourceHelper.reset();
        Integer result = enterpriseService.updateDeviceCount(enterpriseId, deviceCount);
        return ResponseResult.success(result);
    }

    /**
     * 应付钉钉 需要加个虚拟门店数量
     * @param enterpriseId
     * @param storeCount
     * @return
     */
    @ApiOperation("更新虚拟门店数量")
    @PostMapping(value = "/{enterprise-id}/updateStoreCount")
    public ResponseResult updateStoreCount(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("storeCount")Integer storeCount) {
        DataSourceHelper.reset();
        Integer result = enterpriseService.updateStoreCount(enterpriseId, storeCount);
        return ResponseResult.success(result);
    }

    @ApiOperation("获取虚拟门店数量")
    @GetMapping(value = "/getStoreCount")
    public ResponseResult<EnterpriseCorpNameVO> getStoreCount(@RequestParam("corpId")String corpId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseService.getStoreCount(corpId));
    }

    @ApiOperation("获取鱼你万店掌门店id")
    @GetMapping(value = "/getWdzStoreMapping")
    public ResponseResult getWdzStoreMapping() {
        String deviceWdzSyncStoreIdMapperIds = redisUtilPool.getString("device_wdz_sync_store_mapper_ids");
        List<String> storeIdsMapList = ListUtil.toList(StringUtil.split(deviceWdzSyncStoreIdMapperIds, ","));
        return ResponseResult.success(storeIdsMapList);
    }

    @ApiOperation("新增万店掌门店")
    @PostMapping(value = "/setWdzStoreMapping")
    public ResponseResult setWdzStoreMapping(@RequestParam("storeIdsMapList") List<String> storeIdsMapList) {
        String deviceWdzSyncStoreIdMapperIds = redisUtilPool.getString("device_wdz_sync_store_mapper_ids");
        String addStoreIds = String.join(",", storeIdsMapList);
        deviceWdzSyncStoreIdMapperIds = StringUtils.isNotBlank(deviceWdzSyncStoreIdMapperIds) ? deviceWdzSyncStoreIdMapperIds + Constants.COMMA + addStoreIds : addStoreIds;
        redisUtilPool.setString("device_wdz_sync_store_mapper_ids", deviceWdzSyncStoreIdMapperIds);
        return ResponseResult.success(true);
    }

    @ApiOperation("删除无人员职位")
    @PostMapping(value = "/{enterprise-id}/deleteRoleWithoutUsers")
    public ResponseResult deleteRoleWithoutUsers(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("isDeleteDefault") Boolean isDeleteDefault) {
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        sysRoleService.deleteRoleWithoutUsers(enterpriseId, isDeleteDefault);
        return ResponseResult.success(true);
    }

}
