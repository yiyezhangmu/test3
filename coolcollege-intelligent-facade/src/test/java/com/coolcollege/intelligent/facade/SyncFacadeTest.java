package com.coolcollege.intelligent.facade;

import com.coolcollege.intelligent.common.sync.vo.AuthCorpInfo;
import com.coolcollege.intelligent.common.sync.vo.AuthInfo;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class SyncFacadeTest {

    @InjectMocks
    SyncFacade syncFacade;
    @Mock
    EnterpriseConfigService enterpriseConfigService;

    @Mock
    EnterpriseSettingService enterpriseSettingService;
    @Value("${boss.send.message.url}")
    private String bossSendMessageUrl;
    @Mock
    EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Mock
    SyncDeptFacade syncDeptFacade;

    @Mock
    EnterpriseInitService enterpriseInitService;

    @Mock
    RedisUtilPool redisUtil;
    @Mock
    SimpleMessageService simpleMessageService;

    @Test
    public void sendBossMessage() {
        ReflectionTestUtils.setField(syncFacade, "bossSendMessageUrl", "123");
        AuthInfo authInfo = new AuthInfo(){{
            setAuth_corp_info(new AuthCorpInfo());
        }};
        //syncFacade.sendBossMessage("dingding",authInfo);
    }

    @Test
    public void scopeChangeTest() throws Exception {
        Mockito.when(enterpriseConfigService.selectByCorpId(Mockito.anyString(), Mockito.any())).thenReturn(null);
        syncFacade.scopeChange("1", "dingding", true, null);
        EnterpriseConfigDO enterpriseConfig = new EnterpriseConfigDO();
        enterpriseConfig.setEnterpriseId("1");
        enterpriseConfig.setDbName("ss");
        Mockito.when(enterpriseConfigService.selectByCorpId(Mockito.anyString(), Mockito.any())).thenReturn(enterpriseConfig);
//        Mockito.when(enterpriseSettingService.getEnterpriseSettingVOByEid(Mockito.any())).thenReturn(null);
//        AuthScopeDTO authScope = new AuthScopeDTO();
//        authScope.setDeptIdList(Arrays.asList(2L));
//        Mockito.when(enterpriseInitConfigApiService.getAuthScope(Mockito.any(), Mockito.any())).thenReturn(authScope);
        syncFacade.scopeChange("1", "dingding", true,null);
    }
}