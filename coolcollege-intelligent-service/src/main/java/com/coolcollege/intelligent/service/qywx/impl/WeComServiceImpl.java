package com.coolcollege.intelligent.service.qywx.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.qywx.dto.ImportUserDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.qywx.WeComService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author ：xugangkun
 * @date ：2021/6/9 15:12
 */
@Slf4j
@Service
public class WeComServiceImpl implements WeComService {

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private SysDepartmentService sysDepartmentService;

    @Resource
    private DingUserSyncService dingUserSyncService;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private EnterpriseUserMappingService enterpriseUserMappingService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private EnterpriseSettingService enterpriseSettingService;

    @Override
    public void importWoComUser(ImportUserDTO importUser, String eid) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        String dbName = enterpriseConfigDO.getDbName();
        String userId = enterpriseConfigDO.getDingCorpId() + "_" + importUser.getUserId();
        importUser.setUserId(userId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //TODO 订正部门名称
        sysDepartmentService.initWeComDepartment(eid, importUser);
        //TODO 初始化企业用户信息
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
        if (enterpriseUser == null) {
//            log.info("无当前用户，userId:{}, eid:{}", importUser.getUserId(), eid);
            throw new ServiceException("企业无当前用户，userId:" + importUser.getUserId() + "企业id:" + eid);
        }
        //设置用户基本信息
        initEnterpriseUser(enterpriseUser , importUser);
        List<EnterpriseUserDO> users = new ArrayList<>();
        users.add(enterpriseUser);
        enterpriseUserService.batchInsertOrUpdate(users, eid);
       //TODO 初始化平台用户信息
         DataSourceHelper.reset();
        EnterpriseUserDO coolConfigUser = enterpriseUserService.selectConfigUserByUnionid(enterpriseUser.getUnionid());
        if (coolConfigUser == null) {
//            log.info("无当前用户，userId:{}, eid:{}", importUser.getUserId(), eid);
            throw new ServiceException("平台无当前用户，userId:" + importUser.getUserId() + "企业id:" + eid);
        }
        String configUserId = coolConfigUser.getId();
        enterpriseUser.setId(configUserId);
        //一个用户可能关联多个企业，不能因为用户在一个企业中的激活状态影响平台用户的状态
        enterpriseUser.setActive(null);
        //需要订正平台用户来源
        enterpriseUser.setAppType(enterpriseConfigDO.getAppType());
        dingUserSyncService.syncConfigUser(null, enterpriseUser, eid, false);
    }

    @Override
    public void initFirstUser(String userId, String corpId, String openUserid, String appType, String name) {
        DynamicDataSourceContextHolder.clearDataSourceType();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        if (enterpriseConfigDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业不存在");
        }

        try {
            String eid = enterpriseConfigDO.getEnterpriseId();
            //添加平台库的用户信息
            EnterpriseUserDO configUser = enterpriseUserDao.selectConfigUserByUserIdIgnoreActive(userId);
            if (configUser == null) {
                EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
                enterpriseUserDO.setId(UUIDUtils.get32UUID());
                enterpriseUserDO.setUserId(userId);
                enterpriseUserDO.setUnionid(openUserid);
                enterpriseUserDO.setAppType(appType);
                if(AppTypeEnum.isWxSelfAndPrivateType(appType)){
                    enterpriseUserDO.setName(name);
                }
                enterpriseUserDO.setLanguage("zh_cn");
                enterpriseUserDO.setUserStatus(UserStatusEnum.NORMAL.getCode());
                List<EnterpriseUserDO> insertUsers = new ArrayList<>();
                insertUsers.add(enterpriseUserDO);
                enterpriseUserDao.batchInsertPlatformUsers(insertUsers);
                //添加用户企业关联信息
                EnterpriseUserMappingDO enterpriseUserMappingDO = new EnterpriseUserMappingDO();
                enterpriseUserMappingDO.setId(UUIDUtils.get32UUID());
                enterpriseUserMappingDO.setUserId(enterpriseUserDO.getId());
                enterpriseUserMappingDO.setEnterpriseId(eid);
                enterpriseUserMappingDO.setUnionid(enterpriseUserDO.getUnionid());
                enterpriseUserMappingDO.setUserStatus(enterpriseUserDO.getUserStatus());
                enterpriseUserMappingDO.setCreateTime(new Date());
                /*EnterpriseUserMappingDO checkDo = enterpriseUserMappingMapper.selectByEidAndUserId(eid, enterpriseUserDO.getId());
                if (checkDo == null) {
                    enterpriseUserMappingMapper.save(enterpriseUserMappingDO);
                }*/
                enterpriseUserMappingService.saveEnterpriseUserMapping(enterpriseUserMappingDO);
            }
            // 切到企业库
            EnterpriseSettingVO setting = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            //判断企业用户信息
            EnterpriseUserDO entUser = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
            if (entUser == null) {
                EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
                enterpriseUserDO.setId(UUIDUtils.get32UUID());
                enterpriseUserDO.setUserId(userId);
                enterpriseUserDO.setUnionid(openUserid);
                enterpriseUserDO.setSubordinateRange(setting.getManageUser());
                enterpriseUserService.insertEnterpriseUser(eid, enterpriseUserDO);
            }
            Integer userNum = enterpriseUserService.countUserAll(eid);
            if (userNum == 0 || (userNum == 1 && entUser != null)) {
                //设置第一个用户为管理员
                Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
                enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(roleIdByRoleEnum.toString(), userId));
            }
        } catch (Exception e) {
            log.error("WeComServiceImpl initFirstUser 初始化企业第一个用户异常", e);
        }

    }

    @Override
    public void initFirstUser(String enterpriseId, String userId, String openUserid, String appType, String name, String mobile, String initRoleId) {
        DynamicDataSourceContextHolder.clearDataSourceType();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if (enterpriseConfigDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业不存在");
        }

        try {
            String eid = enterpriseConfigDO.getEnterpriseId();
            //添加平台库的用户信息
            EnterpriseUserDO configUser = enterpriseUserDao.selectConfigUserByUserIdIgnoreActive(userId);
            if (configUser == null) {
                EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
                enterpriseUserDO.setId(UUIDUtils.get32UUID());
                enterpriseUserDO.setUserId(userId);
                enterpriseUserDO.setUnionid(openUserid);
                enterpriseUserDO.setAppType(appType);
                enterpriseUserDO.setName(name);
                enterpriseUserDO.setMobile(mobile);
                enterpriseUserDO.setLanguage("zh_cn");
                enterpriseUserDO.setUserStatus(UserStatusEnum.NORMAL.getCode());
                List<EnterpriseUserDO> insertUsers = new ArrayList<>();
                insertUsers.add(enterpriseUserDO);
                enterpriseUserDao.batchInsertPlatformUsers(insertUsers);
                //添加用户企业关联信息
                EnterpriseUserMappingDO enterpriseUserMappingDO = new EnterpriseUserMappingDO();
                enterpriseUserMappingDO.setId(UUIDUtils.get32UUID());
                enterpriseUserMappingDO.setUserId(enterpriseUserDO.getId());
                enterpriseUserMappingDO.setEnterpriseId(eid);
                enterpriseUserMappingDO.setUnionid(enterpriseUserDO.getUnionid());
                enterpriseUserMappingDO.setUserStatus(enterpriseUserDO.getUserStatus());
                enterpriseUserMappingDO.setCreateTime(new Date());
                /*EnterpriseUserMappingDO checkDo = enterpriseUserMappingMapper.selectByEidAndUserId(eid, enterpriseUserDO.getId());
                if (checkDo == null) {
                    enterpriseUserMappingMapper.save(enterpriseUserMappingDO);
                }*/
                enterpriseUserMappingService.saveEnterpriseUserMapping(enterpriseUserMappingDO);
            }
            // 切到企业库
            EnterpriseSettingVO setting = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            //判断企业用户信息
            EnterpriseUserDO entUser = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
            if (entUser == null) {
                EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
                enterpriseUserDO.setId(UUIDUtils.get32UUID());
                enterpriseUserDO.setUserId(userId);
                enterpriseUserDO.setUnionid(openUserid);
                enterpriseUserDO.setSubordinateRange(setting.getManageUser());
                enterpriseUserDO.setMobile(mobile);
                enterpriseUserDO.setName(name);
                enterpriseUserService.insertEnterpriseUser(eid, enterpriseUserDO);
                // 初始化职位
                if (Objects.nonNull(initRoleId)) {
                    enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(initRoleId.toString(), userId));
                }
            }
            Integer userNum = enterpriseUserService.countUserAll(eid);
            if (userNum == 0 || (userNum == 1 && entUser != null)) {
                //设置第一个用户为管理员
                Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
                enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(roleIdByRoleEnum.toString(), userId));
            }
        } catch (Exception e) {
            log.error("WeComServiceImpl initFirstUser 初始化企业第一个用户异常", e);
        }
    }

    @Override
    public void sendOpenSucceededMsg(String eid) {
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
            if (!AppTypeEnum.isQwType(config.getAppType())) {
                return;
            }
            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            List<String> userList = enterpriseUserMapper.selectUserAll(eid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userList", userList);
            jsonObject.put("appType", config.getAppType());
            jsonObject.put("corpId", config.getDingCorpId());
            simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.OPEN_SUCCEEDED_MSG_QUEUE);
        } catch (Exception e) {
            log.error("sendOpenSucceededMsg error", e);
        }
    }

    private EnterpriseUserDO initEnterpriseUser(EnterpriseUserDO enterpriseUser, ImportUserDTO importUser) {
        enterpriseUser.setName(importUser.getName());
        enterpriseUser.setUserId(importUser.getUserId());
        enterpriseUser.setMobile(importUser.getMobile());
        enterpriseUser.setTel(importUser.getTelephone());
        enterpriseUser.setEmail(importUser.getEmail());
        enterpriseUser.setActive(Boolean.TRUE);
        enterpriseUser.setUserStatus(UserStatusEnum.NORMAL.getCode());
        return enterpriseUser;
    }

}
