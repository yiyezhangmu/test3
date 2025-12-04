package com.coolcollege.intelligent.service.sync;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.ErrConstants;
import com.coolcollege.intelligent.common.constant.ErrContext;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dto.AuthInfoDTO;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.FsService;
import com.coolcollege.intelligent.service.enterprise.impl.EnterpriseConfigServiceImpl;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 钉钉同步上下文
 */
@Slf4j
public class SyncContext {


    private ErrContext errCtx;
    private String corpId;
    private String eid;
    private String appType;
    /**
     * 企业名称，app端注册企业时使用
     */
    private String enterpriseName;
    /**
     * 开通人的userId，app端注册企业时使用。钉钉和企业微信的企业通过对应的开放接口获得
     */
    private String authUserId;

    private String dbName;
    private String accessToken;
    /**
     * 购买的商品规格名称
     */
    private String itemName;
    /**
     * 授权信息
     */
    private AuthInfoDTO authInfo;
    /**
     * 是否是第一次开通
     */
    private Boolean firstOpen;
    /**
     * 钉钉通讯录授权范围
     */
    private AuthScopeDTO authScope;
    /**
     * 同步从钉钉获取的所有用户id
     */
    private Set<String> userIdSet;
    /**
     * 同步从钉钉获取的所有部门
     */
    private List<SysDepartmentDO> sysDepartments;

    /**
     * 所有部门id的集合
     */
    private Set<String> deptIdSet = null;

    /**
     * 部门和父级部门的映射
     */
    private Map<String, String> deptIdMap = null;

    /**
     * 部门id和部门下的人数的映射
     */
    private Map<Long, Integer> deptUserCountMap = Maps.newHashMap();

    public SyncContext(String corpId, String appType) {
        this.corpId = corpId;
        this.appType = appType;
    }

    public ErrContext getErrCtx() {
        return errCtx;
    }

    public void setErrCtx(ErrContext errCtx) {
        this.errCtx = errCtx;
    }

    public String getCorpId() {
        return corpId;
    }


    public Boolean isFirstOpen() {
        if (this.errCtx == null && this.firstOpen == null) {
            EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
            log.info("isFirstOpen corpId:{}, serviceInstance={}, appType={}", corpId, enterpriseConfigService.getClass(), getAppType());
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, getAppType());
            if (enterpriseConfig == null) {
                log.info("enterpriseConfig is null");
            }

            this.firstOpen = enterpriseConfig == null;
        }
        return this.firstOpen;
    }

    public String getEid() {
        if (this.errCtx == null && this.eid == null) {
            DataSourceHelper.reset();
            EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
            log.info("isFirstOpen corpId:{}, serviceInstance={}, appType={}", corpId, enterpriseConfigService.getClass(), getAppType());
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, getAppType());
            if (enterpriseConfig == null) {
                log.info("enterpriseConfig is null");
                throw new ServiceException("企业不存在");
            }
            this.eid = enterpriseConfig.getEnterpriseId();
        }
        return this.eid;
    }

    public String getDbName() {
        if (this.errCtx == null && this.dbName == null) {
            DataSourceHelper.reset();
            EnterpriseConfigServiceImpl enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigServiceImpl.class);
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(eid);
            this.dbName = enterpriseConfig.getDbName();
        }
        return this.dbName;
    }

    public Set<String> getUserIdSet() throws ApiException {
        AuthScopeDTO authScope = getAuthScope();
        List<String> userIdList = authScope.getUserIdList();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            this.userIdSet = Sets.newHashSet(userIdList);
        } else {
            this.userIdSet = Sets.newHashSetWithExpectedSize(0);
        }
        return this.userIdSet;
    }

    public List<SysDepartmentDO> getSysDepartments() throws ApiException {

        //部门至少会有一个, 企业根部门(无论授权范围没有部门)
        if (this.errCtx == null && CollectionUtils.isEmpty(this.sysDepartments)) {

            this.sysDepartments = Lists.newArrayList();

            AuthInfoDTO authInfo = getAuthInfo();
            if (this.errCtx == null && Objects.nonNull(authInfo) && Objects.nonNull(authInfo.getAuthCorpInfo())) {
                String corpName = authInfo.getAuthCorpInfo().getCorpName();
                SysDepartmentDO rootSysDepartment = getRootSysDepartment(corpName);
                this.sysDepartments.add(rootSysDepartment);
                AuthScopeDTO authScope = getAuthScope();
                if (this.errCtx == null) {
                    List<String> deptIdList = authScope.getDeptIdList();
                    if (CollectionUtils.isNotEmpty(deptIdList)) {
                        DingService dingService = SpringContextUtil.getBean("dingService", DingService.class);
                        String accessToken = getAccessToken();
                        if (this.errCtx == null) {
                            List<SysDepartmentDO> allDepts = null;
                            try {
                                allDepts = dingService.getAllDepts(deptIdList, accessToken, this.corpId, getAppType());
                            } catch (ApiException e) {
                                this.errCtx = ErrConstants.ErrorGetDepts;
                                log.info("getAllDepts error, corpId={}", this.corpId, e);
                            }
                            if (CollectionUtils.isNotEmpty(allDepts)) {
                                this.sysDepartments.addAll(allDepts);
                            }
                        }
                    }
                }
            }
        }

        return this.sysDepartments;
    }

    /**
     * 企业微信获取部门及子部门
     * @return
     */
    public List<SysDepartmentDO> getPySysDepartmentsV2() {

        if (this.errCtx == null && CollectionUtils.isEmpty(this.sysDepartments)) {
            ChatService chatService = SpringContextUtil.getBean("chatService", ChatService.class);
            String accessToken = getPyAccessToken();
            AuthInfoDTO authInfo = getAuthInfo();
            String corpName = authInfo.getAuthCorpInfo().getCorpName();
            SysDepartmentDO rootSysDepartment = getRootSysDepartment(corpName);
            log.info("getPySysDepartmentsV2:{}", rootSysDepartment.toString());
            this.sysDepartments = Lists.newArrayList();
            this.sysDepartments.add(rootSysDepartment);
            List<String> deptIdList = getAuthScope().getDeptIdList();
            if (CollectionUtils.isNotEmpty(deptIdList)) {
                if (this.errCtx == null) {
                    List<SysDepartmentDO> allDepts = chatService.getAllDepts(deptIdList, accessToken);
                    if (CollectionUtils.isNotEmpty(allDepts)) {
                        this.sysDepartments.addAll(allDepts);
                    }
                }
            }
        }
        return this.sysDepartments;
    }

    public String getAccessToken() {
        if (this.errCtx == null && StringUtils.isBlank(this.accessToken)) {
            DingService dingService = SpringContextUtil.getBean("dingService", DingService.class);
            try {
                this.accessToken = dingService.getAccessToken(this.corpId, getAppType());
            } catch (Exception e) {
                this.errCtx = ErrConstants.ErrorGetAccessToken;
                log.info("getAccessToken error, corpId={}", this.corpId);
            }
        }
        return this.accessToken;
    }
    public String getMainCorpId(String userId){
        DingService dingService = SpringContextUtil.getBean("dingService", DingService.class);
        return dingService.getManiCorpId(getAccessToken(),userId);
    }

    public String getPyAccessToken() {

        ChatService chatService = SpringContextUtil.getBean("chatService", ChatService.class);
        try {
            this.accessToken = chatService.getPyAccessToken(this.corpId, getAppType());
        } catch (Exception e) {
            this.errCtx = ErrConstants.ErrorGetAccessToken;
            log.error("getPyAccessToken error, corpId={}", this.corpId);
        }
        //}
        return this.accessToken;
    }

    public void clearAccessToken() {
        this.accessToken = null;
    }

    public AuthInfoDTO getAuthInfo() {
        EnterpriseInitConfigApiService configApiService = SpringContextUtil.getBean("enterpriseInitConfigApiService", EnterpriseInitConfigApiService.class);
        try {
            AuthInfoDTO authInfo = configApiService.getAuthInfo(this.corpId, this.appType);
            this.authInfo = authInfo;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return this.authInfo;
    }

    public AuthScopeDTO getAuthScope() {
        EnterpriseInitConfigApiService configApiService = SpringContextUtil.getBean("enterpriseInitConfigApiService", EnterpriseInitConfigApiService.class);
        try {
            AuthScopeDTO authScope = configApiService.getAuthScope(this.corpId, this.appType);
            this.authScope = authScope;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return this.authScope;
    }

    public Set<String> getDeptIdSet() throws ApiException {

        if (CollectionUtils.isNotEmpty(this.deptIdSet)) {
            return this.deptIdSet;
        }
        if (AppTypeEnum.WX_APP.getValue().equals(this.getAppType()) || AppTypeEnum.WX_APP2.getValue().equals(this.getAppType())) {
            this.deptIdSet = getPySysDepartmentsV2().stream().map(d -> d.getId()).collect(Collectors.toSet());
            log.info("deptIdSet====：{}",deptIdSet.toString());
        } else {
            this.deptIdSet = getSysDepartments().stream().map(d -> d.getId()).collect(Collectors.toSet());
        }

        return this.deptIdSet;

    }

    public Map<String, String> getDeptIdMap() throws ApiException {
        if (this.deptIdMap != null && this.deptIdMap.size() > 0) {
            return this.deptIdMap;
        }

        this.deptIdMap = Maps.newHashMap();
        if (AppTypeEnum.WX_APP.getValue().equals(this.getAppType()) || AppTypeEnum.WX_APP2.getValue().equals(this.getAppType())) {
            getPySysDepartmentsV2().forEach(d -> {
                this.deptIdMap.put(d.getId(), d.getParentId());
            });
            log.info("deptIdMap====：{}",deptIdMap.toString());
        } else {
            getSysDepartments().forEach(d -> {
                this.deptIdMap.put(d.getId(), d.getParentId());
            });
        }
        return this.deptIdMap;

    }


    /**
     * 企业微信获取部门及子部门
     * @return
     */
    public List<SysDepartmentDO> getFsDepartmentsV1() {

        if (this.errCtx == null && CollectionUtils.isEmpty(this.sysDepartments)) {
            FsService fsService = SpringContextUtil.getBean("fsService", FsService.class);
            AuthInfoDTO authInfo = fsService.getAuthInfo(this.getCorpId(), this.getAppType());
            String corpName = authInfo.getAuthCorpInfo().getCorpName();
            SysDepartmentDO rootSysDepartment = getRootSysDepartment(corpName);
            log.info("getPySysDepartmentsV2:{}", JSONObject.toJSONString(rootSysDepartment));
            this.sysDepartments = Lists.newArrayList();
            this.sysDepartments.add(rootSysDepartment);
            List<String> deptIdList = fsService.getAuthScope(this.getCorpId(),this.getAppType()).getDeptIdList();
            if (CollectionUtils.isNotEmpty(deptIdList)) {
                //特殊处理，全部授权时，拿到第一级节点部门
                if (deptIdList.size()==1&&deptIdList.contains(Constants.ROOT_DEPT_ID_STR)){
                    deptIdList =  Lists.newArrayList() ;
                }
                if (this.errCtx == null) {
                    List<SysDepartmentDO> allDepts = fsService.getAuthScopeAllDepts(deptIdList, this.getCorpId(),this.getAppType());
                    if (CollectionUtils.isNotEmpty(allDepts)) {
                        this.sysDepartments.addAll(allDepts);
                    }
                }
            }
        }
        return this.sysDepartments;
    }

    private SysDepartmentDO getRootSysDepartment(String corpName) {
        SysDepartmentDO rootDepartment = new SysDepartmentDO();
        rootDepartment.setId(SyncConfig.ROOT_DEPT_ID_STR);
        rootDepartment.setName(corpName);
        return rootDepartment;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }
}
