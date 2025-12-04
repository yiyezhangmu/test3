//package net.coolcollege.cms.test.service.qywxSync;
//
//import com.coolcollege.intelligent.common.enums.AppTypeEnum;
//import com.coolcollege.intelligent.common.sync.conf.Role;
//import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
//import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMappingMapper;
//import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
//import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
//import com.coolcollege.intelligent.service.qywx.ChatService;
//import com.coolcollege.intelligent.service.qywxSync.QywxUserSyncService;
//import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.mockito.Mockito.when;
//
///**
// * @author ：xugangkun
// * @date ：2021/6/24 14:18
// */
//@Slf4j
//public class QywxUserSyncServiceTest  extends IntelligentMainTest {
//
//    @Autowired
//    private QywxUserSyncService qywxUserSyncService;
//
//    @MockBean
//    private ChatService chatService;
//
//    @MockBean
//    private EnterpriseUserService enterpriseUserService;
//
//    @MockBean
//    private EnterpriseUserMapper enterpriseUserMapper;
//
//    @MockBean
//    private EnterpriseUserMappingMapper enterpriseUserMappingMapper;
//
//    private static String corpId;
//    private static String userId;
//    private static String accessToken;
//    private static String eid;
//    private static String dbName;
//
//    private static EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
//
//    static {
//        corpId = "ww708ebbb8488dda51";
//        userId = "WangXiaoPeng";
//        accessToken = "accessToken";
//        eid = "dad36af7d8804db39a33c722ead566de";
//        dbName = "coolcollege_intelligent_10";
//        DataSourceHelper.changeToSpecificDataSource(dbName);
//
//        enterpriseUserDO.setId("f8d897fdc9cf4d7281a4251482599bb2");
//        enterpriseUserDO.setUserId(userId);
//        enterpriseUserDO.setName("WangXiaoPeng");
//        enterpriseUserDO.setDepartment("[4, 1]");
//        enterpriseUserDO.setAppType(AppTypeEnum.WX_APP.getValue());
//        enterpriseUserDO.setRemark(enterpriseUserDO.getUserId());
//        enterpriseUserDO.setIsAdmin(false);
//        enterpriseUserDO.setMainAdmin(false);
//        enterpriseUserDO.setRoles(Role.EMPLOYEE.getId());
//        enterpriseUserDO.setActive(Boolean.TRUE);
//        enterpriseUserDO.setUnionid("woQ-Q5EAAAG3DfytGAzOS7O_HMxIZLrg");
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testSyncEntUser() throws Exception {
//
//        when(chatService.getUserDetail(corpId, userId, accessToken, false,"30000000", AppTypeEnum.WX_APP.getValue())).thenReturn(enterpriseUserDO);
//
//        String qyUserId = corpId + "_" + userId;
//        when(enterpriseUserService.selectConfigUserByUserId(qyUserId)).thenReturn(enterpriseUserDO);
//        when(enterpriseUserService.selectByUserIdIgnoreActive(eid, qyUserId)).thenReturn(enterpriseUserDO);
//
//        when(enterpriseUserMapper.selectByUserIdIgnoreActive(eid, qyUserId)).thenReturn(enterpriseUserDO);
//        qywxUserSyncService.syncWeComUser(corpId, userId, accessToken, eid, dbName, false, AppTypeEnum.WX_APP.getValue());
//
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testSyncDeleteWeComUser() throws Exception {
//        String qwUserId = corpId + "_" + userId;
//        when(enterpriseUserService.selectByUserId(eid, qwUserId)).thenReturn(enterpriseUserDO);
//        qywxUserSyncService.syncDeleteWeComUser(eid, qwUserId, dbName);
//    }
//
//}
