package com.coolcollege.intelligent.facade.enterprise.impl;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * 企业配置RPC接口实现
 *
 * @author zhangnan
 * @date 2021-11-25 15:22
 */
@RunWith(MockitoJUnitRunner.class)
public class EnterpriseConfigFacadeImplTest{
    @InjectMocks
    private EnterpriseConfigFacadeImpl enterpriseConfigFacade;
    @Mock
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Test
    public void getConfigByEnterpriseTest() {

        when(enterpriseConfigMapper.selectByEnterpriseId(any())).thenReturn(new EnterpriseConfigDO());
        // null
        enterpriseConfigFacade.getConfigByEnterprise(null);
        assertNotNull(enterpriseConfigFacade.getConfigByEnterprise("1"));
    }

}
