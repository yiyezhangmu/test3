package com.coolcollege.intelligent.facade.enterprise.init;

import com.coolcollege.intelligent.common.util.ScriptUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.facade.enterprise.init.impl.EnterpriseInitBaseServiceImpl;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.rpc.license.LicenseApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolstore.base.enums.AppTypeEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

/**
 * @author ：xugangkun
 * @date ：2022/2/18 11:10
 */
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class EnterpriseInitServiceTest {

    @InjectMocks
    private EnterpriseInitBaseServiceImpl enterpriseInitService;

    @Mock
    private SimpleMessageService simpleMessageService;

    @Mock
    private ScriptUtil scriptUtil;

    @Mock
    private SysRoleService sysRoleService;

    @Mock
    private EnterpriseUserService enterpriseUserService;

    @Mock
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Mock
    private EnterpriseUserMapper enterpriseUserMapper;

    @Mock
    private TbMetaTableMapper tbMetaTableMapper;

    @Mock
    private UnifyTaskFcade unifyTaskFcade;

    @Mock
    private SyncFacade syncFacade;

    @Mock
    private LicenseApiService licenseApiService;

    @Mock
    private SysDepartmentMapper sysDepartmentMapper;

    @Mock
    private RegionMapper regionMapper;

    public static String corpId = "wpayJeDAAAlLcN6lX10fkTy0X0Hjp1wA";
    public static String appType = "qw2";
    public static String eid = "8aebcbf770c24506a9b73f49cb24a0ce";
    public static String authUserId = "woayJeDAAASFs7Ezn5Dz86e2Ic1u0qsA";
    public static String dbName = "coolcollege_intelligent_2";


//    @Test
//    public void enterpriseInitDeptOrderTest() throws ApiException {
//        SysDepartmentDTO sysDepartmentDTO = new SysDepartmentDTO();
//        sysDepartmentDTO.setId(4L);
//        sysDepartmentDTO.setDepartOrder(1);
//        Mockito.when(enterpriseInitConfigApiService.getDepartmentDetail(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(sysDepartmentDTO);
//        enterpriseInitService.enterpriseInitDeptOrder(corpId, AppTypeEnum.DING_DING2, eid, dbName, Arrays.asList(2L, 3L));
//    }
}
