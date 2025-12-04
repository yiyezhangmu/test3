package com.coolcollege.intelligent.controller.enterprise;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseAuditStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonGroupMapper;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupDO;
import com.coolcollege.intelligent.model.boss.request.BossEnterpriseExportRequest;
import com.coolcollege.intelligent.model.enterprise.EnterpriseAuditInfoDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.*;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseLicenseSettingRequest;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseStoreSettingRequest;
import com.coolcollege.intelligent.model.enterprise.vo.BannerVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseStoreCheckSettingVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseStoreSettingVO;
import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;
import com.coolcollege.intelligent.model.setting.request.EnterpriseNoticeSettingRequest;
import com.coolcollege.intelligent.model.setting.vo.EnterpriseNoticeSettingVO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseAuditInfoService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import com.coolcollege.intelligent.service.setting.EnterpriseNoticeSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.common.util.MD5Util;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.UPDATE_NAME_ERROR_NOT_MASTER;

/**
 * @author wch
 * @ClassName EnterpriseController
 * @Description 用一句话描述什么
 */
@RestController
@RequestMapping({"/v2/enterprise", "/v3/enterprise"})
@BaseResponse
public class EnterpriseController {

    @Autowired
    EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EnterpriseNoticeSettingService enterpriseNoticeSettingService;

    @Resource
    private AliyunPersonGroupMapper aliyunPersonGroupMapper;

    @Autowired
    private EnterpriseAuditInfoService enterpriseAuditInfoService;

    @Autowired
    private EnterpriseStoreSettingService enterpriseStoreSettingService;

    @Autowired
    private StoreService storeService;

    @Resource
    private RedisUtilPool redisUtilPool;

    /**
     * 设置企业信息
     *
     * @param enterpriseDO
     * @return
     */
    @PostMapping("update")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "设置企业信息")
    public Object setImage(@RequestBody EnterpriseDTO enterpriseDO) {
        DataSourceHelper.reset();
        return enterpriseService.updateInfo(enterpriseDO);
    }


    @GetMapping("{enterprise-id}/update/name")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "设置企业名称")
    public ResponseResult<Boolean> setEnterpriseName(@PathVariable("enterprise-id") String eid,
                                                     @RequestParam("enterpriseName") String enterpriseName) {
        DataSourceHelper.reset();
        CurrentUser user = UserHolder.getUser();
        //非管理无法修改企业名称
        if (!Role.isAdmin(user.getSysRoleDO().getRoleEnum())) {
            throw new ServiceException(UPDATE_NAME_ERROR_NOT_MASTER);
        }
        return ResponseResult.success(enterpriseService.updateEnterpriseName(eid, enterpriseName));
    }

    /**
     * 获取企业信息
     *
     * @param id
     * @return
     */
    @GetMapping("baseInfo")
    public Object getBaseInfo(String id) {
        DataSourceHelper.reset();
        return enterpriseService.getBaseInfo(id);
    }

    /**
     * 设置banner图
     *
     * @param enterpriseId
     * @param banner
     * @return
     */
    @PostMapping("/{enterprise-id}/setBanner")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "设置banner图")
    public Object setBanner(@PathVariable("enterprise-id") String enterpriseId, @RequestBody BannerVO banner) {
        DataSourceHelper.reset();
        return enterpriseService.setBanner(enterpriseId, banner.getBanner());
    }

    /**
     * 获取banner图
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/{enterprise-id}/getBannerList")
    public Object getBannerList(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.reset();
        return enterpriseService.getBanner(enterpriseId);
    }

    /**
     * 获取门店必填项
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/{enterprise-id}/storeRequired")
    public Object getStoreRequired(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.reset();
        return enterpriseService.getStoreRequired(enterpriseId);
    }

    /**
     * 设置企业通用设置
     *
     * @param enterpriseId
     * @param settingDO
     * @return
     */
    @PostMapping("/{enterprise-id}/setting")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "设置企业通用设置")
    public Object setEnterpriseSetting(@PathVariable("enterprise-id") String enterpriseId,
                                       @RequestBody EnterpriseSettingDO settingDO) {
        DataSourceHelper.reset();
        return enterpriseService.saveOrUpdateSettings(enterpriseId, settingDO);
    }

    /**
     * 获取企业设置
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/{enterprise-id}/setting")
    public Object getEnterpriseSetting(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.reset();
        return enterpriseService.getEnterpriseSettings(enterpriseId);
    }


    @GetMapping("/list")
    public ResponseResult<PageVO> listEnterprise(@RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(value = "page_number", defaultValue = "1") Integer pageNumber) {
        DataSourceHelper.reset();
        BossEnterpriseExportRequest param = new BossEnterpriseExportRequest();
        param.setName(name);
        param.setPageSize(pageSize);
        param.setPageNumber(pageNumber);
        return ResponseResult.success(enterpriseService.listEnterprise(param));
    }

    /**
     * 获取动态配置
     *
     * @param eid
     * @param model  模块名称
     * @param fields 字段列表（用英文逗号隔开）
     * @return
     */
    @GetMapping("/{enterprise-id}/dynamic_setting")
    public Object getSystemSetting(@PathVariable("enterprise-id") String eid, String model, String fields) {
        DataSourceHelper.reset();
        return enterpriseService.getSystemSetting(eid, model, fields);
    }

    /**
     * 获取企业巡店设置
     *
     * @param eId 企业id
     * @return
     */
    @GetMapping("/{enterprise-id}/getStoreCheckSettings")
    public Object getStoreCheckSettings(@PathVariable("enterprise-id") String eId) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(eId);
    }

    /**
     * 保存企业巡店设置
     *
     * @param eId    企业id
     * @param entity 巡店设置详细信息
     */
    @PostMapping("/{enterprise-id}/saveStoreCheckSettings")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "保存企业巡店设置")
    public Object saveStoreCheckSettings(@PathVariable("enterprise-id") String eId,
                                         @RequestBody EnterpriseStoreCheckDTO entity) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.saveOrUpdateStoreCheckSetting(eId, entity);
    }

    /**
     * 获取企业巡店设置
     *
     * @param eId 企业id
     * @return
     */
    @GetMapping("/{enterprise-id}/getStoreCheckSettings/new")
    public EnterpriseStoreCheckSettingVO getStoreCheckSettingsNew(@PathVariable("enterprise-id") String eId) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.queryEnterpriseStoreCheckSettingVO(eId);
    }

    /**
     * 保存企业巡店设置
     *
     * @param eId    企业id
     * @param entity 巡店设置详细信息
     */
    @PostMapping("/{enterprise-id}/saveStoreCheckSettings/new")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "保存企业巡店设置")
    public Object saveStoreCheckSettingsNew(@PathVariable("enterprise-id") String eId,
                                            @RequestBody EnterpriseStoreCheckNewDTO entity) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.saveOrUpdateStoreCheckSettingNew(eId, entity);
    }

    @PostMapping("/{enterprise-id}/saveEnterpriseNoticeSetting")
    @OperateLog(operateModule = CommonConstant.Function.PASSENGER_FLOW_MANAGEMENT, operateType = CommonConstant.LOG_UPDATE, operateDesc = "保存企业巡店设置")
    public ResponseResult saveEnterpriseNoticeSetting(@PathVariable("enterprise-id") String eid,
                                                      @RequestBody List<EnterpriseNoticeSettingRequest> requestList) {
        DataSourceHelper.reset();
        String enterpriseId = UserHolder.getUser().getEnterpriseId();
        //检查参数，需要切换数据源不能在事务中进行
        checkGroupRequest(eid, requestList);
        boolean result = enterpriseNoticeSettingService.saveOrUpdateEnterpriseNotice(enterpriseId, requestList);
        return ResponseResult.success(result);
    }

    private void checkGroupRequest(String eid, List<EnterpriseNoticeSettingRequest> requestList) {
        Map<String, Long> personGroupIdGroupMap = ListUtils.emptyIfNull(requestList)
                .stream()
                .collect(Collectors.groupingBy(EnterpriseNoticeSettingRequest::getPersonGroupId, Collectors.counting()));
        if (MapUtils.isNotEmpty(personGroupIdGroupMap)) {
            List<String> duplicateGroupIdList = new ArrayList<>();
            personGroupIdGroupMap.forEach((personGroupId, count) -> {
                if (count > 1) {
                    duplicateGroupIdList.add(personGroupId);
                }
            });
            if (CollectionUtils.isNotEmpty(duplicateGroupIdList)) {
                DataSourceHelper.changeToMy();
                List<AliyunPersonGroupDO> aliyunPersonGroupDOList = aliyunPersonGroupMapper.listAliyunPersonGroupById(eid, duplicateGroupIdList);
                String duplicateGroupNameStr = ListUtils.emptyIfNull(aliyunPersonGroupDOList)
                        .stream()
                        .map(AliyunPersonGroupDO::getPersonGroupName)
                        .collect(Collectors.joining(","));
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能重复配置人员分类(" + duplicateGroupNameStr + ")！");
            }
        }
    }

    @GetMapping("/{enterprise-id}/getEnterpriseNoticeSetting")
    public ResponseResult getEnterpriseNoticeSetting() {
        DataSourceHelper.reset();
        String enterpriseId = UserHolder.getUser().getEnterpriseId();
        List<EnterpriseNoticeSettingVO> settingList = enterpriseNoticeSettingService.listEnterpriseNotice(enterpriseId);
        return ResponseResult.success(settingList);
    }

    /**
     * 获取企业管理设置
     *
     * @param eId
     * @return
     */
    @GetMapping("/{enterprise-id}/getBussinessManagement")
    public Object getBussinessManagement(@PathVariable("enterprise-id") String eId) {
        DataSourceHelper.reset();
        return enterpriseService.getBusinessManagement(eId);
    }

    /**
     * 保存企业管理设置
     *
     * @return
     */
    @PostMapping("/{enterprise-id}/saveBussinessManagement")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "保存企业管理设置")
    public Object saveBussinessManagement(@PathVariable("enterprise-id") String eId, @RequestBody EnterpriseDTO entity) {
        DataSourceHelper.reset();
        return enterpriseService.saveBussinessManagement(eId, entity);
    }

    /**
     * 获取门店基础信息设置
     *
     * @param eId 企业id
     * @return
     */
    @GetMapping("/{enterprise-id}/getStoreBaseInfoSetting")
    public Object getStoreBaseInfoSetting(@PathVariable("enterprise-id") String eId) {
        DataSourceHelper.reset();
        return enterpriseService.getStoreBaseInfoSetting(eId);
    }

    /**
     * 保存门店基础信息设置
     *
     * @param eId 企业id
     * @return
     */
    @PostMapping("/{enterprise-id}/saveStoreBaseInfoSetting")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "保存门店基础信息设置")
    public Object saveStoreBaseInfoSetting(@PathVariable("enterprise-id") String eId, @RequestBody @Valid StoreBaseInfoSettingDTO entity) {
        DataSourceHelper.reset();
        return enterpriseService.saveStoreBaseInfoSetting(eId, entity);
    }

    /**
     * 判断门店信息是否完善
     *
     * @param eid 企业id
     * @return
     */
    @GetMapping("/{enterprise-id}/getIsPerfect")
    public ResponseResult getIsPerfect(@PathVariable("enterprise-id") String eid, @RequestParam("storeId") String storeId) {
        DataSourceHelper.changeToMy();
        StoreDTO store = storeService.getStoreByStoreId(eid, storeId);
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO storeSetting = enterpriseStoreSettingService.getEnterpriseStoreSetting(eid);
        String isPerfect = enterpriseStoreSettingService.getStorePerfection(store, storeSetting.getPerfectionField());
        return ResponseResult.success(isPerfect);
    }

    /**
     * 获取钉钉同步设置
     *
     * @param eId
     * @return
     */
    @GetMapping("/{enterprise-id}/getDingSync")
    public Object getDingSync(@PathVariable("enterprise-id") String eId) {
        DataSourceHelper.reset();
        return enterpriseService.getDingSync(eId);
    }

    /**
     * 保存钉钉同步设置
     *
     * @param eId
     * @param entity
     * @return
     */
    @PostMapping("/{enterprise-id}/saveDingSync")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "保存钉钉同步设置")
    public Boolean saveDingSync(@PathVariable("enterprise-id") String eId, @RequestBody EnterpriseSettingDO entity) {
        DataSourceHelper.reset();
        return enterpriseService.saveDingSync(eId, entity);
    }


    /**
     * 企业注册申请
     *
     * @param request
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody @Valid RegisterApplyDTO request) {
        DataSourceHelper.reset();
        String smsCodeKey = SmsCodeTypeEnum.ENTERPRISE_REGISTER + ":" + request.getMobile();
        String smsCode = request.getSmsCode();
        String codeValue = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!smsCode.equals(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        EnterpriseAuditInfoDO audit = new EnterpriseAuditInfoDO();
        audit.setApplyUserName(request.getApplyUserName());
        audit.setEnterpriseName(request.getEnterpriseName());
        audit.setEmail(request.getEmail());
        audit.setMobile(request.getMobile());
        audit.setPassword(MD5Util.md5(request.getPassword() + Constants.USER_AUTH_KEY));
        audit.setAuditStatus(EnterpriseAuditStatusEnum.AUDIT_PENDING.getValue());
        audit.setAppType(request.getAppType());
        enterpriseAuditInfoService.save(audit);
        redisUtilPool.delKey(smsCodeKey);
        return ResponseResult.success();
    }

    @GetMapping("/{enterprise-id}/getStoreSetting")
    public ResponseResult<EnterpriseStoreSettingVO> getStoreSetting(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseStoreSettingService.getEnterpriseStoreSettingVO(enterpriseId));
    }

    @PostMapping("/{enterprise-id}/updateStoreSetting")
    public ResponseResult updateStoreSetting(@PathVariable("enterprise-id") String enterpriseId,
                                             @Validated @RequestBody EnterpriseStoreSettingRequest enterpriseStoreSettingRequest) {
        DataSourceHelper.reset();
        enterpriseStoreSettingService.updateStoreTimeSetting(enterpriseId, enterpriseStoreSettingRequest);
        return ResponseResult.success();
    }

    @ApiOperation("获取企业是否在白名单，是否是历史企业 用于组织架构  返回true表示在白名单中，否则不在")
    @GetMapping("/{enterprise-id}/isHistoryEnterprise")
    public ResponseResult<Boolean> isHistoryEnterprise(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseService.isHistoryEnterprise(enterpriseId));
    }

    @ApiModelProperty("更新企业证照设置")
    @PostMapping("/{enterprise-id}/updateLicenseSetting")
    public ResponseResult updateLicenseSetting(@PathVariable("enterprise-id") String enterpriseId,
                                             @RequestBody EnterpriseLicenseSettingRequest enterpriseStoreSettingRequest) {
        DataSourceHelper.reset();
        enterpriseStoreSettingService.updateLicenseSetting(enterpriseId, enterpriseStoreSettingRequest);
        return ResponseResult.success();
    }
}
