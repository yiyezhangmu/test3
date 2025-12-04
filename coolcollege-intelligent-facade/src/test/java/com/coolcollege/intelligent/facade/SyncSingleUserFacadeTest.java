package com.coolcollege.intelligent.facade;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywxSync.QywxUserSyncService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import java.util.ArrayList;
import java.util.List;


/**
 * 用户同步单测类
 * @author ：xugangkun
 * @date ：2021/10/28 16:49
 */
@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class SyncSingleUserFacadeTest {

    @InjectMocks
    private SyncSingleUserFacade syncSingleUserFacade;

    @Mock
    private DingTalkClientService dingTalkClientService;

    @Mock
    private DingUserSyncService dingUserSyncService;

    @Mock
    private EnterpriseUserService enterpriseUserService;
    private QywxUserSyncService qywxUserSyncService;

    @Mock
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Mock
    private ChatService chatService;

    @Mock
    private SimpleMessageService simpleMessageService;


    private static String eid = "632b0d436e364debb37e51bc19344631";

    private static String corpId = "ww708ebbb8488dda51";

    private static String dbName = "coolcollege_intelligent_20";

    private static String appType = "qw";

    private static String accessToken1 = "access_token1";

    private static String accessToken2 = "access_token1";
    List<EntUserRoleDTO> entUserRoleDTOS;

//    @Test
//    public void syncUserTest() throws ApiException {
//        EnterpriseSettingVO setting = new EnterpriseSettingVO();
//        setting.setEnableDingSync(0);
//        OapiV2UserGetResponse.UserGetResponse response = new OapiV2UserGetResponse.UserGetResponse();
//        when(dingTalkClientService.getUserDetail(any(), any())).thenReturn(null);
//        syncSingleUserFacade.syncUser("1", "1", "1", "1", setting,
//                false);
//        response.setDeptIdList(Arrays.asList(1L));
//        when(dingTalkClientService.getUserDetail(any(), any())).thenReturn(response);
//        EnterpriseUserDO dingEnterpriseUser = new EnterpriseUserDO();
//        dingEnterpriseUser.setUserId("1");
//        dingEnterpriseUser.setUserStatus(1);
//        dingEnterpriseUser.setThirdOaUniqueFlag("sss");
//        when(dingUserSyncService.initEnterpriseUser(any(), any(), any())).thenReturn(dingEnterpriseUser);
//        List<String> mainAdminUserIds = new ArrayList<>();
//        mainAdminUserIds.add("1");
//        when(dingTalkClientService.getMainAdmin(any())).thenReturn(mainAdminUserIds);
//        when(enterpriseUserService.selectByUserIdIgnoreActive(any(), any())).thenReturn(dingEnterpriseUser);
//        syncSingleUserFacade.syncUser("1", "1", "1", "1", setting,
//                false);
//        when(enterpriseUserService.selectConfigUserByUnionid(any())).thenReturn(dingEnterpriseUser);
//        syncSingleUserFacade.syncUser("1", "1", "1", "1", setting,
//                false);
//        setting.setEnableDingSync(1);
//        setting.setDingSyncRoleRule(2);
//        setting.setDingSyncStoreRule("{\"code\":\"allLeaf\"}");
//
//        EnterpriseUserDO user1 = new EnterpriseUserDO();
//        user1.setUserId("userId1");
//        user1.setName("用户1");
//        user1.setActive(true);
//        user1.setPosition("职位1");
//        user1.setIsAdmin(true);
//        user1.setIsLeaderInDepts("[0]");
//        EnterpriseUserDO user2 = new EnterpriseUserDO();
//        user2.setUserId("userId2");
//        user2.setName("用户2");
//        user2.setActive(true);
//        user2.setPosition("职位2");
//        user2.setIsAdmin(true);
//        user2.setIsLeaderInDepts("[0,1]");
//        Mockito.when(enterpriseUserService.selectByUserIdIgnoreActive(eid, user2.getUserId())).thenReturn(user2);
//
//        //组装管理员列表
//        List<String> adminList = new ArrayList<>();
//        adminList.add("userId2");
//        Mockito.when(chatService.getWxAdminList(corpId, appType)).thenReturn(adminList);
//        Mockito.when(enterpriseUserService.selectByUserIdIgnoreActive(eid, user2.getUserId())).thenReturn(user2);
//        syncSingleUserFacade.syncQwUser(corpId, eid, dbName, user2, setting, appType);
//    }

    @Before
    public void setUp() throws Exception {
        entUserRoleDTOS =new ArrayList<>();
        EntUserRoleDTO entUserRoleDTO=new EntUserRoleDTO();
        entUserRoleDTO.setUserRoleId(0L);
        entUserRoleDTO.setRoleId(0L);
        entUserRoleDTO.setRoleName("");
        entUserRoleDTO.setRoleEnum("");
        entUserRoleDTO.setSource("");

    }

    @Test
    public void syncThirdUserAuth() {
        Mockito.when(enterpriseUserRoleMapper.selectUserRoleByUserId("1", "1")).thenReturn(entUserRoleDTOS);
        syncSingleUserFacade.syncThirdUserAuth("1","1","1","1","1");
    }
}
