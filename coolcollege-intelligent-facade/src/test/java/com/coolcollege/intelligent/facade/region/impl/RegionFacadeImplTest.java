package com.coolcollege.intelligent.facade.region.impl;

import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.facade.dto.CorrectRegionPathRequest;
import com.coolcollege.intelligent.facade.dto.CorrectRegionStoreNumRequest;
import com.coolcollege.intelligent.facade.dto.RegionPathDTO;
import com.coolcollege.intelligent.facade.dto.RegionStoreNumDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RegionFacadeImplTest {

    @InjectMocks
    RegionFacadeImpl regionFacade;

    @Mock
    StoreMapper storeMapper;

    @Mock
    RegionMapper regionMapper;

    @Test
    public void updateRegionPath() {
        List<CorrectRegionPathRequest> correctRegionPathRequests = new ArrayList<CorrectRegionPathRequest>(){{
            add(new CorrectRegionPathRequest(){{
                setRegionPathDTOS(new ArrayList<RegionPathDTO>(){{
                    add(new RegionPathDTO());
                }});
            }});
        }};
        Assert.assertNotNull(regionFacade.updateRegionPath(correctRegionPathRequests));
        Assert.assertNotNull(regionFacade.updateRegionPath(null));
    }

    @Test
    public void updateStoreRegionPath() {
        List<CorrectRegionPathRequest> correctRegionPathRequests = new ArrayList<CorrectRegionPathRequest>(){{
            add(new CorrectRegionPathRequest());
        }};
        Assert.assertNotNull(regionFacade.updateStoreRegionPath(correctRegionPathRequests));
        Assert.assertNotNull(regionFacade.updateStoreRegionPath(null));

    }

    @Test
    public void updateRegionStoreNum() {
        List<CorrectRegionStoreNumRequest> correctRegionPathRequests = new ArrayList<CorrectRegionStoreNumRequest>(){{
            add(new CorrectRegionStoreNumRequest(){{
                setRegionStoreNumDTOS(new ArrayList<RegionStoreNumDTO>(){{
                    add(new RegionStoreNumDTO());
                }});
            }});
        }};
        Assert.assertNotNull(regionFacade.updateRegionStoreNum(correctRegionPathRequests));
        Assert.assertNotNull(regionFacade.updateRegionStoreNum(null));

    }
}