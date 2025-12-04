package com.coolcollege.intelligent.facade.enterprise.init;

import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.common.util.ScriptUtil;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDepartmentDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.rpc.license.LicenseApiService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.taobao.api.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * @author zhangnan
 * @description:
 * @date 2022/5/18 3:18 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class EnterpriseInitBaseServiceTest {

    public static String corpId = "wpayJeDAAAlLcN6lX10fkTy0X0Hjp1wA";
    public static String appType = "qw2";
    public static String eid = "8aebcbf770c24506a9b73f49cb24a0ce";
    public static String authUserId = "woayJeDAAASFs7Ezn5Dz86e2Ic1u0qsA";
    public static String dbName = "coolcollege_intelligent_2";

    @InjectMocks
    private EnterpriseInitBaseService enterpriseInitBaseService = new DingEnterpriseInitService();

    @InjectMocks
    private EnterpriseInitBaseService onePartyEnterpriseInitService = new DingOnePartyEnterpriseInitService();
    @Mock
    private EnterpriseUserDepartmentDao enterpriseUserDepartmentDao;
    @Mock
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Mock
    private SysDepartmentMapper sysDepartmentMapper;
    @Mock
    private SysDepartmentService sysDepartmentService;
    @Mock
    private RegionService regionService;
    @Mock
    private EnterpriseUserService enterpriseUserService;
    @Mock
    private EnterpriseUserMappingService enterpriseUserMappingService;
    @Mock
    private SysRoleService sysRoleService;
    @Mock
    private UserRegionMappingDAO userRegionMappingDAO;
    @Mock
    private SubordinateMappingDAO subordinateMappingDAO;
    @Mock
    private DingUserSyncService dingUserSyncService;
    @Mock
    private ConvertFactory convertFactory;
    @Mock
    private TbMetaTableMapper tbMetaTableMapper;
    @Mock
    private EnterpriseUserMapper enterpriseUserMapper;
    @Mock
    private SyncFacade syncFacade;
    @Mock
    protected ScriptUtil scriptUtil;
    @Mock
    private LicenseApiService licenseApiService;
    @Mock
    private UnifyTaskFcade unifyTaskFcade;
    @Mock
    private DingDeptSyncService dingDeptSyncService;
    @Mock
    private SimpleMessageService simpleMessageService;

    @Test
    public void runEnterpriseScriptTest() throws ApiException {
        EnterpriseOpenMsg msg = new EnterpriseOpenMsg(eid, corpId, appType, authUserId, dbName);
        Mockito.when(sysRoleService.getRoleIdByRoleEnum(any(), eq(Role.MASTER.getRoleEnum()))).thenReturn(20000000L);
        Mockito.when(sysRoleService.getRoleIdByRoleEnum(any(), eq(Role.SHOPOWNER.getRoleEnum()))).thenReturn(50000000L);
        EnterpriseUserDTO userDTO = new EnterpriseUserDTO();
        userDTO.setActive(true);
        userDTO.setAvatar("https://rescdn.qqmail.com/node/wwmng/wwmng/style/images/independent/DefaultAvatar$73ba92b5.png");
        userDTO.setCreateTime(new Date());
        userDTO.setUserId("wpayJeDAAAlLcN6lX10fkTy0X0Hjp1wA_woayJeDAAASFs7Ezn5Dz86e2Ic1u0qsA");
        Mockito.when(enterpriseInitConfigApiService.getUserDetailByUserId(any(), any(), any())).thenReturn(userDTO);

        TbMetaTableDO tableDO = new TbMetaTableDO();
        tableDO.setId(1L);
        tableDO.setTableName("name");
        Mockito.when(tbMetaTableMapper.getInitTable(eid, UnifyTaskConstant.FormType.STANDARD)).thenReturn(tableDO);
        Mockito.when(enterpriseUserMapper.getUserByAdmin(eid, true)).thenReturn(Collections.singletonList(enterpriseInitBaseService.transUserDtoToDo(userDTO)));

        enterpriseInitBaseService.runEnterpriseScript(msg);
        onePartyEnterpriseInitService.runEnterpriseScript(msg);
    }

    @Test
    public void enterpriseInitTest(){
        onePartyEnterpriseInitService.enterpriseInit("123","123", "123", "123", "123");
    }
}
