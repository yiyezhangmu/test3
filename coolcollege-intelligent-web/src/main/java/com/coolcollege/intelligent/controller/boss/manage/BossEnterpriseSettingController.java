package com.coolcollege.intelligent.controller.boss.manage;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseThirdPartyConfigDTO;
import com.coolcollege.intelligent.model.enterprise.param.DingDingSyncSettingUpdParam;
import com.coolcollege.intelligent.model.enterprise.request.PassengerConfigRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseLatestSyncInfoVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingInfoVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseAccessCoolCollegeDTO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOperateLogService;
import com.coolcollege.intelligent.service.enterprise.FsService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.user.ExternalUserService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @date 2021-03-25 17:36
 */
@RestController
@RequestMapping("/boss/manage/bossEnterpriseSetting")
@Slf4j
@ErrorHelper
public class BossEnterpriseSettingController {

    @Autowired
    private DingService dingService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Autowired
    private EnterpriseOperateLogService enterpriseOperateLogService;

    @Autowired
    private DingTalkClientService dingTalkClientService;

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpleMessageService simpleMessageService;

    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Resource
    private ExternalUserService externalUserService;
    @Resource
    private AIService aiService;

    /**
     * 查询企业钉钉参数配置
     *
     * @return
     */
    @GetMapping(value = "/{enterpriseId}/getDingSyncSettingByEid")
    public ResponseResult getEnterpriseSettingByEid(@PathVariable String enterpriseId) {
        DataSourceHelper.reset();
        Map<String, Object> result = new HashMap<>();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        EnterpriseOperateLogDO latestSyncLog = enterpriseOperateLogService.getLatestLogByEnterpriseIdAndOptType(enterpriseId, SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC);
        EnterpriseOperateLogDO latestSuccessLog =  enterpriseOperateLogService.getLatestSuccessLog(enterpriseId, SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC, SyncConfig.SYNC_STATUS_SUCCESS);
        EnterpriseLatestSyncInfoVO enterpriseLatestSyncInfoVO = new EnterpriseLatestSyncInfoVO();
        enterpriseLatestSyncInfoVO.setLatestStatus(latestSyncLog != null ? latestSyncLog.getStatus() : null);
        enterpriseLatestSyncInfoVO.setLatestSyncSuccessEndTime(latestSuccessLog != null?latestSuccessLog.getOperateEndTime() : null);
        enterpriseLatestSyncInfoVO.setLatestFailRemark(latestSyncLog != null && (SyncConfig.SYNC_STATUS_FAIL == latestSyncLog.getStatus()) && StrUtil.isNotEmpty(latestSyncLog.getRemark()) ? latestSyncLog.getRemark() : "");
        // 是否有进行中的同步
        String eidLockKey = redisConstantUtil.getSyncEidEffectiveKey(enterpriseId);
        if (StringUtils.isNotBlank(redisUtilPool.getString(eidLockKey))) {
            enterpriseLatestSyncInfoVO.setHasLatestSync(true);
        }
        result.put("enterpriseSetting", enterpriseSettingVO);
        result.put("enterpriseLatestSyncInfo", enterpriseLatestSyncInfoVO);
        return ResponseResult.success(result);
    }

    @PostMapping("/{enterprise-id}/saveOrUpdateEnterpriseDingDingSyncSetting")
    public ResponseResult saveBussinessManagement(@PathVariable("enterprise-id") String eId, @RequestBody DingDingSyncSettingUpdParam param) {
        DataSourceHelper.reset();
        BossLoginUserDTO sysUserLoginDTO = BossUserHolder.getUser();
        boolean isSuc = enterpriseSettingService.saveOrUpdateEnterpriseDingDingSyncSetting(eId, param, sysUserLoginDTO.getId(), sysUserLoginDTO.getUsername());
        if(!isSuc){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "保存失败");
        }
        return ResponseResult.success(true);
    }

    // 获取钉钉组织架构
    @GetMapping("/{enterprise-id}/getDeptList")
    public ResponseResult getDeptList(@PathVariable(value = "enterprise-id") String eid,
                                      @RequestParam(value = "deptId") Long deptId) throws ApiException {
        DataSourceHelper.reset();
        Map<String, Object> result = new HashMap<>();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        if(enterpriseConfigDO == null){
            return ResponseResult.success(result);
        }
        String accessToken = dingService.getAccessToken(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptBaseResponseList = Lists.newArrayList();
        if(deptId == null){
            deptBaseResponseList = dingTalkClientService.getAuthDeptList(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), accessToken);
        }else {
            deptBaseResponseList = dingTalkClientService.getDeptList(deptId, accessToken);
        }
        result.put("dingDeptSubList", deptBaseResponseList);
        return ResponseResult.success(result);
    }

    // 获取钉钉组织详情
    @GetMapping("/{enterprise-id}/getDeptDetail")
    public ResponseResult getDeptDetail(@PathVariable(value = "enterprise-id") String eid,
                                      @RequestParam(value = "deptId") Long deptId) throws ApiException {

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String accessToken = dingService.getAccessToken(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
        deptId = (deptId == null ? 1 : deptId);
        OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse =  dingTalkClientService.getDeptDetail(String.valueOf(deptId), accessToken);
        return ResponseResult.success(deptBaseResponse);
    }

    /**
     * 判断企业是否接入酷学院
     * @param enterpriseId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/12 10:09
     */
    @GetMapping("/getAccessCoolCollegeSetting")
    public ResponseResult getAccessCoolCollegeSetting(@RequestParam(value = "enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        if (StringUtils.isBlank(enterpriseId)) {
            log.info("enterpriseId为Null, enterpriseId");
            return ResponseResult.success(false);
        }
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(enterpriseId);
        if (setting == null || setting.getAccessCoolCollege() == null) {
            log.error("未找到企业, enterpriseId:{}", enterpriseId);
            return ResponseResult.success(false);
        }
        return ResponseResult.success(setting.getAccessCoolCollege());
    }

    /**
     * 设置企业是否接入酷学院
     * @param enterpriseAccessCoolCollegeDTO
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/12 10:09
     */
    @PostMapping("/updateAccessCoolCollegeSetting")
    public ResponseResult updateAccessCoolCollegeSetting(@RequestBody @Valid EnterpriseAccessCoolCollegeDTO enterpriseAccessCoolCollegeDTO) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettingDO = new EnterpriseSettingDO();
        enterpriseSettingDO.setEnterpriseId(enterpriseAccessCoolCollegeDTO.getEnterpriseId())
                .setAccessCoolCollege(enterpriseAccessCoolCollegeDTO.getAccessCoolCollege())
                .setSendUpcoming(enterpriseAccessCoolCollegeDTO.getSendUpcoming());
        enterpriseSettingService.updateAccessCoolCollegeByEnterpriseId(enterpriseSettingDO);
        return ResponseResult.success(enterpriseSettingDO.getAccessCoolCollege());
    }
    @PostMapping("/updateEnterpriseSettings")
    public ResponseResult updateEnterpriseSettings(@RequestBody @Valid EnterpriseAccessCoolCollegeDTO enterpriseAccessCoolCollegeDTO){
        DataSourceHelper.reset();
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(enterpriseAccessCoolCollegeDTO.getEnterpriseId());
        EnterpriseSettingDO enterpriseSettingDO = new EnterpriseSettingDO();
        enterpriseSettingDO.setEnterpriseId(enterpriseAccessCoolCollegeDTO.getEnterpriseId())
                .setAccessCoolCollege(enterpriseAccessCoolCollegeDTO.getAccessCoolCollege())
                .setSendUpcoming(enterpriseAccessCoolCollegeDTO.getSendUpcoming())
                .setEnableExternalUser(enterpriseAccessCoolCollegeDTO.getEnableExternalUser())
                .setCustomizePackageEndTime(enterpriseAccessCoolCollegeDTO.getCustomizePackageEndTime())
                .setAiAlgorithms(enterpriseAccessCoolCollegeDTO.getAiAlgorithms())
                .setExtendField(enterpriseAccessCoolCollegeDTO.getExtendField());

        enterpriseSettingService.updateAccessCoolCollegeByEnterpriseId(enterpriseSettingDO);
        enterpriseSettingService.updateSyncPassengerByEid(enterpriseAccessCoolCollegeDTO.getEnterpriseId(),enterpriseAccessCoolCollegeDTO.getSyncPassenger());
        if(!setting.getEnableExternalUser().equals(enterpriseAccessCoolCollegeDTO.getEnableExternalUser())){
            //只有做修改了才处理
            externalUserService.openOrCloseExternalUser(enterpriseAccessCoolCollegeDTO.getEnterpriseId(), enterpriseAccessCoolCollegeDTO.getEnableExternalUser());
        }
        return ResponseResult.success(true);
    }


    @GetMapping("/getEnterpriseSettings")
    public ResponseResult getEnterpriseSettings(@RequestParam(value = "enterpriseId") String enterpriseId){
        DataSourceHelper.reset();
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(setting)){
            return null;
        }
        EnterpriseSettingInfoVO result = new EnterpriseSettingInfoVO();
        result.setSendUpcoming(setting.getSendUpcoming());
        result.setAccessCoolCollege(setting.getAccessCoolCollege());
        result.setSyncPassenger(setting.getSyncPassenger());
        result.setEnableExternalUser(setting.getEnableExternalUser());
        result.setCustomizePackageEndTime(setting.getCustomizePackageEndTime());
        result.setAiAlgorithms(setting.getAiAlgorithms());
        result.setExtendField(setting.getExtendField());
        return ResponseResult.success(result);
    }



    // 获取钉钉组织详情
    @GetMapping("/{enterprise-id}/sendUpcomingFinish")
    public ResponseResult sendUpcomingFinish(@PathVariable(value = "enterprise-id") String enterpriseId)  {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId",enterpriseId);
        jsonObject.put("corpId",enterpriseConfigDO.getDingCorpId());
        Map<String, String> stringStringMap = redisUtilPool.hashGetAllByIdx("upcoming" + enterpriseId,2);
        List<Long> taskSubIdList = new ArrayList<>();
        for(Map.Entry<String,String> entry : stringStringMap.entrySet()) {
            taskSubIdList.add(Long.valueOf(entry.getKey()));
        }
        jsonObject.put("unifyTaskSubIdList",taskSubIdList );
        jsonObject.put("appType",enterpriseConfigDO.getAppType());
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
        return ResponseResult.success(true);
    }

    @GetMapping("/getEnterpriseIsOpenUpcoming")
    public ResponseResult getEnterpriseIsOpenUpcoming(@RequestParam(value = "enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        if (StringUtils.isBlank(enterpriseId)) {
            log.error("enterpriseId为Null, enterpriseId");
            return ResponseResult.success(false);
        }
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(enterpriseId);
        if (setting == null || setting.getAccessCoolCollege() == null) {
            log.error("未找到企业, enterpriseId:{}", enterpriseId);
            return ResponseResult.success(false);
        }
        return ResponseResult.success(setting.getSendUpcoming());
    }

    /**
     * 添加企业内部授权信息
     * @param configDTO
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/7/20 15:58
     */
    @PostMapping("/add")
    public ResponseResult reopenAudit(@RequestBody @Valid EnterpriseThirdPartyConfigDTO configDTO){
        redisUtilPool.setString(configDTO.getCorpId(), configDTO.getCorpSecret());
        return ResponseResult.success(true);
    }



    //客流数据同步

    @GetMapping("/passenger/get")
    public ResponseResult getPassenger(@RequestParam("eid")String eid){

        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingService.selectByEnterpriseId(eid);
        return ResponseResult.success(enterpriseSettingDO.getSyncPassenger());
    }
    @PostMapping("/passenger/save")
    public ResponseResult savePassenger(@RequestBody PassengerConfigRequest request) {

        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseSettingService.updateSyncPassengerByEid(request.getEid(),request.getSyncPassenger()));
    }
    /**
     * 获取企微组织架构
     * @param eid
     * @param deptId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/10/25 15:58
     */
    @GetMapping("/{enterprise-id}/getQwDeptList")
    public ResponseResult getQwDeptList(@PathVariable(value = "enterprise-id") String eid,
                                      @RequestParam(value = "deptId", required = false) Long deptId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String deptIdStr = "";
        if (deptId!=null){
            deptIdStr  = String.valueOf(deptId);
        }
        if(AppTypeEnum.isWxSelfAndPrivateType(enterpriseConfigDO.getAppType())){
            List<SysDepartmentDTO> departmentDTOList = enterpriseInitConfigApiService.getSubDepartments(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), String.valueOf(deptId),Boolean.FALSE);
            if (StringUtils.isNotEmpty(deptIdStr)) {
                //如果传入部门id,获得该部门下的子部门，不递归获取
                String finalDeptIdStr = deptIdStr;
                departmentDTOList = departmentDTOList.stream().filter(de -> finalDeptIdStr.equals(de.getParentId())).collect(Collectors.toList());
            } else {
                //如果未传入id，拿没有父级部门的部门,以非代开发token获取的部门列表为准
                List<String> deptIdList = departmentDTOList.stream().filter(de -> de != null && de.getId() != null)
                        .map(SysDepartmentDTO::getId).collect(Collectors.toList());
                //筛选出父级部门不在部门id列表中的部门，即为根部门
                departmentDTOList = departmentDTOList.stream().filter(de -> !deptIdList.contains(de.getParentId()))
                        .collect(Collectors.toList());
            }
            log.info("私部组织架构departmentDTOList = {}",JSONObject.toJSONString(departmentDTOList));
            return ResponseResult.success(departmentDTOList);
        }
        String accessToken = chatService.getPyAccessToken(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
        List<SysDepartmentDO> deptList = chatService.getSubDepts(deptIdStr, accessToken);
        if (StringUtils.isNotEmpty(deptIdStr)) {
            //如果传入部门id,获得该部门下的子部门，不递归获取
            String finalDeptId = deptIdStr;
            deptList = deptList.stream().filter(de -> finalDeptId.equals(de.getParentId())).collect(Collectors.toList());
        } else {
            //如果未传入id，拿没有父级部门的部门,以非代开发token获取的部门列表为准
            List<String> deptIdList = deptList.stream().filter(de -> de != null && de.getId() != null)
                    .map(SysDepartmentDO::getId).collect(Collectors.toList());
            //筛选出父级部门不在部门id列表中的部门，即为根部门
            deptList = deptList.stream().filter(de -> !deptIdList.contains(de.getParentId()))
                    .collect(Collectors.toList());
        }
        return ResponseResult.success(deptList);
    }
    @Resource
    FsService fsService;

    @GetMapping("/{enterprise-id}/getFsDeptList")
    public ResponseResult getFsDeptList(@PathVariable(value = "enterprise-id") String eid,
                                        @RequestParam(value = "deptId", required = false) String deptId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        List<SysDepartmentDO> subDeptList = Lists.newArrayList();
        if (StringUtils.isNotBlank(deptId)){
            subDeptList = fsService.getSubDepts(deptId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), Boolean.FALSE);
        }else {
            SysDepartmentDO sysDepartmentDO = new SysDepartmentDO();
            AuthInfoDTO authInfo = fsService.getAuthInfo(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
            sysDepartmentDO.setId(Constants.ROOT_DEPT_ID_STR);
            sysDepartmentDO.setName(authInfo.getAuthCorpInfo().getCorpName());
            subDeptList.add(sysDepartmentDO);
        }
        return ResponseResult.success(subDeptList);
    }

    @ApiOperation(value = "AI模型列表")
    @GetMapping("/getAiModelList")
    public ResponseResult getAiModelList() {
        return ResponseResult.success(aiService.getAIModelList());
    }

}
