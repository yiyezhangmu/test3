package com.coolcollege.intelligent.facade.store.impl;

import com.coolcollege.intelligent.facade.request.StoreSettingRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class StoreSettingFacadeImplTest {

    @InjectMocks
    private StoreSettingFacadeImpl storeSettingFacadeImpl;

    @Test
    public void getStoreLicenseSetting() {
        StoreSettingRequest request = new StoreSettingRequest();
        storeSettingFacadeImpl.getStoreLicenseSetting(request);
    }
}
