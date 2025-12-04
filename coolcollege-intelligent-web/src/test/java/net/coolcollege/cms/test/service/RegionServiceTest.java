//package net.coolcollege.cms.test.service;
//
//import com.coolcollege.intelligent.common.constant.Constants;
//import com.coolcollege.intelligent.common.exception.ServiceException;
//import com.coolcollege.intelligent.common.response.ErrorCode;
//import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
//import com.coolcollege.intelligent.facade.SyncUserFacade;
//import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
//import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
//import com.coolcollege.intelligent.data.correction.DataCorrectionService;
//import com.coolcollege.intelligent.model.region.RegionDO;
//import com.coolcollege.intelligent.model.region.dto.RegionNode;
//import com.coolcollege.intelligent.model.userholder.CurrentUser;
//import com.coolcollege.intelligent.model.userholder.UserHolder;
//import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
//import com.coolcollege.intelligent.service.region.RegionService;
//import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.util.Collections;
//import java.util.List;
//
//
//@Slf4j
//public class RegionServiceTest extends IntelligentMainTest {
//
//    @Autowired
//    private RegionService regionService;
//
//    @Autowired
//    private SyncUserFacade syncUserFacade;
//
//    @Resource
//    private EnterpriseConfigMapper enterpriseConfigMapper;
//
//
//    @Autowired
//    private EnterpriseSettingService enterpriseSettingService;
//
//    @Autowired
//    private DataCorrectionService dataCorrectionService;
//
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testRegionPath() {
//
//        CurrentUser currentUser = UserHolder.getUser();
//
//        log.info("用户userId:" + currentUser.getUserId());
//
//        String regionPath = regionService.getRegionPath(eid, "1");
//
//        Assert.assertEquals("[1]", regionPath);
//
//        String regionPath2 = regionService.getRegionPath(eid, "20003");
//
//        Assert.assertEquals("[1/20001/20003]", regionPath2);
//
//        String regionPath3 = regionService.getRegionPath(eid, "20005");
//
//        Assert.assertEquals("[1/20001/20002/20005]", regionPath3);
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void test() {
//
//        CurrentUser currentUser = UserHolder.getUser();
//        log.info("用户userId:" + currentUser.getUserId());
//        RegionDO newADD=new RegionDO();
//        newADD.setRegionPath("/1/");
//        newADD.setCreateName("system");
//        newADD.setName("测试区域");
//        newADD.setUpdateTime(System.currentTimeMillis());
//        newADD.setRegionType("path");
//        newADD.setRegionId("");
//        newADD.setParentId("1");
//        newADD.setDeleted(false);
//
//        regionService.insertOrUpdate(newADD, eid);
//        Assert.assertNotNull("==================regionPath  CREATE SUCCESS",newADD.getId());
//        RegionNode newADD2= regionService.getRegionById(eid, String.valueOf(newADD.getId()));
//        Assert.assertEquals(newADD.getId(),   newADD2.getId());
//        regionService.removeRegions(eid, Collections.singletonList(newADD.getId()));
//        List<RegionDO> newDel= regionService.getRegionDOsByRegionIds(eid, Collections.singletonList(String.valueOf(newADD2.getId())));
//        Assert.assertEquals(0, newDel.size());
//
//        newADD.setRegionPath("/1/");
//        newADD.setCreateName("systemeeee");
//        newADD.setName("测试区域3333");
//        newADD.setUpdateTime(System.currentTimeMillis());
//        newADD.setRegionType("path");
//        newADD.setRegionId("");
//        newADD.setParentId("1");
//        newADD.setDeleted(false);
//        regionService.batchUpdate(Collections.singletonList(newADD), eid);
//
//        RegionNode newADD3 = regionService.getRegionById(eid, String.valueOf(newADD.getId()));
//        Assert.assertEquals(newADD.getName(), newADD3.getName());
//    }
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void correctRegionPath() {
//        regionService.updateRegionPathAll("dce6071204284e6995d65058ecca4030",1L);
//        dataCorrectionService.syncStoreRegionPath("dce6071204284e6995d65058ecca4030", null);
//        dataCorrectionService.syncRegionPath("dce6071204284e6995d65058ecca4030", null);
//    }
//
//    @Test
//    public void testSyncRegion() {
//        DataSourceHelper.reset();
//        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
//        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
//        if (enterpriseSettingVO.getEnableDingSync() == Constants.ENABLE_DING_SYNC_NOT_OPEN) {
//            throw new ServiceException(ErrorCode.SUCCESS, "未开启同步");
//        }
//        syncUserFacade.syncAll("93ad648955c7446d93a6aca32660d7ea", "system", "system",
//                enterpriseSettingVO, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getDbName());
//    }
//
//}
