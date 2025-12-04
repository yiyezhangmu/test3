package com.coolcollege.intelligent.facade;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dto.OpRoleDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.dingSync.DingRoleSyncService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.dingtalk.api.response.OapiRoleListResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 钉钉角色同步
 *
 * @ClassName: SyncRoleFacade
 * @Author: xugangkun
 * @Date: 2021/3/26 14:52
 */
@Service
@Slf4j
public class SyncRoleFacade {

    @Autowired
    private DingService dingService;

    @Autowired
    private DingRoleSyncService dingRoleSyncService;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    /**
     * 同步企业角色信息
     * @param eid 企业id
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/22 15:29
     */
    public void syncDingRoles(String eid) throws ApiException {
        //开始处理：1.请求返回的钉钉角色id以及角色名，封装成一个列表
        //封装请求体，获得钉钉的角色列表
        log.info("开始同步角色eid:{}", eid);
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String dbName = config.getDbName();
        EnterpriseSettingVO vo = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        Integer syncRule = vo.getDingSyncRoleRule();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        log.info("角色同步前执行预设职位优先级初始化--------------------------------------------:{}", eid);
        sysRoleService.initDefaultRolePriority(eid);
        if (Objects.equals(vo.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN)) {
            return;
        }

        if(BailiEnterpriseEnum.bailiAffiliatedCompany(eid)){
            log.info("bailiAffiliatedCompany:{}, 不在同步角色", eid);
            return;
        }

        //只开启角色同步，需要删除所有的职位以及对应的用户映射关系
        if (SyncConfig.ONE.equals(syncRule)) {
            dingRoleSyncService.deleteDingPosition(eid,vo);
        }
        //只开启职位同步，需要删除所有的角色以及对应的用户映射关系
        if (SyncConfig.TWO.equals(syncRule)) {
            log.info("开始删除角色eid:{}", eid);
            dingRoleSyncService.deleteDingRole(eid,vo);
            return;
        }
        String accessToken = dingService.getAccessToken(config.getDingCorpId(), config.getAppType());
        //钉钉接口返回数据
        List<OapiRoleListResponse.OpenRoleGroup> openRoleList = dingService.getRoleList(accessToken);
        dingRoleSyncService.syncDingRoles(eid, openRoleList,vo);
    }

    /**
     * 门店通-同步角色
     * @param eid
     * @throws ApiException
     */
    public void syncDingOnePartyRoles(String eid) throws ApiException {
        //开始处理：1.请求返回的钉钉角色id以及角色名，封装成一个列表
        //封装请求体，获得钉钉的角色列表
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String dbName = config.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //钉钉接口返回数据
        List<OpRoleDTO> openRoleList = enterpriseInitConfigApiService.getRoles(config.getDingCorpId(), config.getAppType());
        dingRoleSyncService.syncDingOnePartyRoles(eid, openRoleList, null);
    }

}
