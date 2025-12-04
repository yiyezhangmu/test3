package com.coolcollege.intelligent.facade.open.api.organization;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.OpenApiParamCheckUtils;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.login.LoginService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2022-11-01 15:45
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = UserApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class UserApiImpl implements UserApi {

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private LoginService loginService;

    @Resource
    private EnterpriseUserRoleMapper userRoleMapper;

    @ShenyuSofaClient(path = "/user/getUserInfo")
    @Override
    public OpenApiResponseVO<OpenUserVO> getUserInfo(OpenApiUserDTO openApiUserDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/getUserInfo,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(openApiUserDTO));
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(enterpriseUserService.getOpenUserInfo(enterpriseId, openApiUserDTO));
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/getUserInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/updateUserRole")
    @Override
    public OpenApiResponseVO<Boolean> updateUserRole(OpenApiUserDTO openApiUserDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/updateUserRole,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(openApiUserDTO));
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            enterpriseUserService.updateUserRole(enterpriseId, openApiUserDTO);
            return OpenApiResponseVO.success(Boolean.TRUE);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateUserRole,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }


    @ShenyuSofaClient(path = "/user/updateRoleAndAuth")
    @Override
    public OpenApiResponseVO<Boolean> updateUseRoleAndAuth(OpenApiUpdateUserAuthDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/updateUseRoleAndAuth,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return OpenApiResponseVO.fail();
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            OpenApiUpdateUserAuthDTO openApiUpdateUserAuth = JSONObject.parseObject(JSONObject.toJSONString(param), OpenApiUpdateUserAuthDTO.class);
            enterpriseUserService.updateUseRoleAndAuth(enterpriseConfig.getDingCorpId(), enterpriseId, openApiUpdateUserAuth);
            return OpenApiResponseVO.success(Boolean.TRUE);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateRoleAndAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/updateUseRoleAndRegionAuth")
    @Override
    public OpenApiResponseVO<Boolean> updateUseRoleAndRegionAuth(OpenApiUpdateUserRoleAndAuthDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/updateUseRoleAndRegionAuth,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return OpenApiResponseVO.fail();
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            OpenApiUpdateUserRoleAndAuthDTO updateUserRoleAndAuthDTO = JSONObject.parseObject(JSONObject.toJSONString(param), OpenApiUpdateUserRoleAndAuthDTO.class);
            enterpriseUserService.updateUseRoleAndRegionAuth(enterpriseConfig.getDingCorpId(), enterpriseId, updateUserRoleAndAuthDTO);
            return OpenApiResponseVO.success(Boolean.TRUE);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateUseRoleAndRegionAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/add")
    @Override
    public OpenApiResponseVO<Boolean> addUser(OpenApiAddUserDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/updateUseRoleAndRegionAuth,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            if(!param.check()){
                return OpenApiResponseVO.fail(60000, "参数缺失");
            }
            if(!param.checkMobile()){
                return OpenApiResponseVO.fail(60000, "手机号格式错误");
            }
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return OpenApiResponseVO.fail();
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            enterpriseUserService.addUser(enterpriseId, param);
            return OpenApiResponseVO.success(Boolean.TRUE);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateUseRoleAndRegionAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/delete")
    @Override
    public OpenApiResponseVO<Boolean> deleteUser(OpenApiDeleteUserDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        if(!param.check()){
            return OpenApiResponseVO.fail(60000, "用户id列表参数为空");
        }
        log.info("openApi#user/updateUseRoleAndRegionAuth,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return OpenApiResponseVO.fail();
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            enterpriseUserService.deleteUser(enterpriseId, param.getUserIdList(),null);
            return OpenApiResponseVO.success(Boolean.TRUE);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateUseRoleAndRegionAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/getUserAccessToken")
    @Override
    public OpenApiResponseVO<UserAccessTokenVO> getUserAccessToken(OpenApiGetUserAccessTokenDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        if(StringUtils.isBlank(param.getUserAccount())){
            return OpenApiResponseVO.fail(60000, "用户账号不能为空");
        }
        log.info("openApi#user/getUserAccessToken,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            UserAccessTokenVO userAccessToken = loginService.getUserAccessToken(enterpriseId, param);
            return OpenApiResponseVO.success(userAccessToken);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/updateUseRoleAndRegionAuth,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/list")
    @Override
    public OpenApiResponseVO<PageDTO<UserInfoVO>> getUserPage(OpenApiUserQueryDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/getUserPage,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            OpenApiParamCheckUtils.checkNecessaryParam(param.getPageNum(), param.getPageSize());
            OpenApiParamCheckUtils.checkParamLimit(param.getPageSize(), 0, 100);

            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return OpenApiResponseVO.fail();
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

            return OpenApiResponseVO.success(enterpriseUserService.getUserPage(enterpriseId, param));
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/getUserPage,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/detail/list")
    @Override
    public OpenApiResponseVO<UserListVO> getUserByIds(OpenApiUserQueryDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/getUserByIds,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            OpenApiParamCheckUtils.checkNecessaryParam(param.getUserIds());
            if (param.getUserIds().size() > Constants.PAGE_SIZE) {
                throw new ServiceException(ErrorCodeEnum.PAGE_SIZE_MAX);
            }

            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return OpenApiResponseVO.fail();
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

            List<UserInfoVO> list = enterpriseUserService.getUserList(enterpriseId, param);
            UserListVO result = new UserListVO();
            result.setList(list);
            return OpenApiResponseVO.success(result);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/getUserByIds,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @ShenyuSofaClient(path = "/user/adminList")
    @Override
    public OpenApiResponseVO<PageDTO<String>> getAdminUserIds(PageQueryDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("openApi#user/getAdminUserIds,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        try {
            OpenApiParamCheckUtils.checkNecessaryParam(param.getPageNum(), param.getPageSize());
            OpenApiParamCheckUtils.checkParamLimit(param.getPageSize(), 0, 100);

            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return OpenApiResponseVO.fail();
            }
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

            PageHelper.startPage(param.getPageNum(), param.getPageSize());
            List<String> userIds = userRoleMapper.selectUserIdsByRoleId(enterpriseId, Role.MASTER.getId());
            PageInfo<String> page = new PageInfo<>(userIds);

            PageDTO<String> result = new PageDTO<>();
            result.setTotal(page.getTotal());
            result.setPageNum(page.getPageNum());
            result.setPageSize(page.getPageSize());
            result.setList(userIds);
            return OpenApiResponseVO.success(result);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#user/getAdminUserIds,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }
}
