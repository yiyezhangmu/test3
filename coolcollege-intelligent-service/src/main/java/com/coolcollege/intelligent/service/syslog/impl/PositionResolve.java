package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.SysLogConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.HomeTemplateRoleMappingDTO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.SysRoleQueryDTO;
import com.coolcollege.intelligent.model.system.request.RoleDeleteRequest;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyAuthRequest;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyBaseRequest;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
* describe: 职位管理操作内容处理
*
* @author wangff
* @date 2025-02-14
*/
@Service
@Slf4j
public class PositionResolve extends AbstractOpContentResolve {
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Override
    protected void init() {
        super.init();
        funcMap.put(EDIT_DATA_FUNC_AUTH, this::editDataFuncAuth);
        funcMap.put(EDIT_HOME_TEMPLATE, this::editHomeTemplate);
        funcMap.put(CONFIG_PERSON, this::configPerson);
        funcMap.put(REMOVE_PERSON, this::removePerson);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SETTING_POSITION;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        JSONObject request = jsonObject.getJSONObject("request");
        String roleName = request.getString("roleName");
        JSONObject response = JSONObject.parseObject(sysLogDO.getRespParams());
        Long roleId = response.getLong("data");
        return SysLogHelper.buildContent(INSERT_TEMPLATE2, "职位", roleName, roleId.toString());
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        SysRoleModifyBaseRequest request = jsonObject.getObject("request", SysRoleModifyBaseRequest.class);
        return SysLogHelper.buildContent(POSITION_EDIT_TEMPLATE, request.getRoleName(), request.getRoleId().toString(), "基础信息");
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }


    /**
     * 数据可见范围权限/功能权限
     */
    private String editDataFuncAuth(String enterpriseId, SysLogDO sysLogDO) {
        String resultStr = SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
        JSONObject resultJSONObject = JSONObject.parseObject(resultStr);
        String result = resultJSONObject.getString("result");
        String type = resultJSONObject.getString("type");
        if (type.equals("function")) {
            sysLogDO.setSubFunc("功能权限");
        } else {
            sysLogDO.setSubFunc("数据可见范围权限");
        }
        return result;
    }

    /**
     * 首页模板
     */
    private String editHomeTemplate(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        HomeTemplateRoleMappingDTO request = jsonObject.getObject("homeTemplateRoleMappingDTO", HomeTemplateRoleMappingDTO.class);
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, request.getRoleId());
        if (Objects.isNull(role)) {
            log.info("editHomeTemplate#职位不存在");
            return null;
        }
        return SysLogHelper.buildContent(POSITION_EDIT_TEMPLATE, role.getRoleName(), role.getId().toString(), "首页模板");
    }

    /**
     * 配置人员
     */
    private String configPerson(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        SysRoleQueryDTO request = jsonObject.getObject("sysRoleQueryDTO", SysRoleQueryDTO.class);
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, Long.valueOf(request.getRoleId()));
        if (Objects.isNull(role)) {
            log.info("configPerson#职位不存在");
            return null;
        }
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, request.getUserIds());
        String items = SysLogHelper.buildBatchContentItem(userList, EnterpriseUserDO::getName, EnterpriseUserDO::getMobile);
        return SysLogHelper.buildContent(POSITION_EDIT_PERSON_TEMPLATE, SysLogConstant.INSERT, role.getRoleName(), role.getId().toString(), "新增人员", items);
    }

    /**
     * 移除人员
     */
    private String removePerson(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        JSONObject request = jsonObject.getJSONObject("request");
        Long roleId = request.getLong("roleId");
        List<String> userIdList = request.getJSONArray("userIdList").toJavaList(String.class);
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, roleId);
        if (Objects.isNull(role)) {
            log.info("removePerson#职位不存在");
            return null;
        }
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        String items = SysLogHelper.buildBatchContentItem(userList, EnterpriseUserDO::getName, EnterpriseUserDO::getMobile);
        return SysLogHelper.buildContent(POSITION_EDIT_PERSON_TEMPLATE, SysLogConstant.REMOVE, role.getRoleName(), role.getId().toString(), "移除人员", items);
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        switch (typeEnum) {
            case DELETE:
                return deletePreprocess(enterpriseId, reqParams);
            case EDIT_DATA_FUNC_AUTH:
                return editDataFuncAuthPreprocess(enterpriseId, reqParams);
        }
        return null;
    }

    /**
     * DELETE前置操作逻辑
     */

    private String deletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        RoleDeleteRequest request = jsonObject.getObject("request", RoleDeleteRequest.class);
        List<SysRoleDO> roleList = sysRoleMapper.getRoleList(enterpriseId, request.getRoleIdList());
        String items = SysLogHelper.buildBatchContentItem(roleList, SysRoleDO::getRoleName, SysRoleDO::getId);
        return SysLogHelper.buildContent(DELETE_TEMPLATE2, "职位", items);
    }

    /**
     * EDIT_DATA_FUNC_AUTH前置操作逻辑
     */
    private String editDataFuncAuthPreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        SysRoleModifyAuthRequest request = jsonObject.getObject("request", SysRoleModifyAuthRequest.class);
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, request.getRoleId());
        if (Objects.isNull(role)) {
            log.info("editDataFuncAuthPreprocess#职位不存在");
            return null;
        }
        String result, type;
        if (role.getRoleAuth().equals(request.getRoleAuth())) {
            type = "function";
            result =  SysLogHelper.buildContent(POSITION_EDIT_TEMPLATE, role.getRoleName(), role.getId().toString(), "菜单功能权限");
        } else {
            type = "data";
            result =  SysLogHelper.buildContent(POSITION_EDIT_TEMPLATE, role.getRoleName(), role.getId().toString(), "数据可见范围权限");
        }
        JSONObject resultJSONObject = new JSONObject();
        resultJSONObject.put("type", type);
        resultJSONObject.put("result", result);
        return resultJSONObject.toJSONString();
    }

}
