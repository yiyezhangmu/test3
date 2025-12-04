//package net.coolcollege.cms.test.service.erh;
//
//import cn.hutool.json.JSONUtil;
//import com.coolcollege.intelligent.facade.SyncDeptFacade;
//import com.coolcollege.intelligent.model.baili.request.BailiOrgRequest;
//import com.coolcollege.intelligent.service.baili.ThirdOaDeptSyncService;
//import com.coolcollege.intelligent.service.baili.EhrService;
//import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
//import com.taobao.api.ApiException;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Slf4j
//public class ErhServiceTest extends IntelligentMainTest {
//
//    @Autowired
//    private EhrService ehrService;
//
//    @Autowired
//    private ThirdOaDeptSyncService thirdOaDeptSyncService;
//
//    @Autowired
//    private DingDeptSyncService dingDeptSyncService;
//
//    @Autowired
//    private SyncDeptFacade syncDeptFacade;
//
//    @Test
//    public void tetx() {
//        BailiOrgRequest bailiOrgRequest = new BailiOrgRequest();
//        bailiOrgRequest.setPageSize(100);
//        log.info(JSONUtil.toJsonStr(ehrService.listOrg(bailiOrgRequest)));
//    }
//
//    @Test
//    public void deptSync() throws ApiException {
////        syncDeptFacade.sync("1ad10f0ee1234be3a5ce7256ad794ffd", "1", "1");
////        ehrDeptSyncService.syncOrgAll("1ad10f0ee1234be3a5ce7256ad794ffd", 38768);
////        dingDeptSyncService.syncDingDepartmentAll("96e761e5ab8a4fdba19b63b231f87c6c", "ding960d5e815706e0adacaaa37764f94726");
//    }
//
//}