//package net.coolcollege.cms.test.service;
//
//import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//
///**
// * @author byd
// * @date 2021-05-21 19:36
// */
//@Slf4j
//public class ReissueDingTest extends IntelligentMainTest {
//
//    @Resource
//    private UnifyTaskService unifyTaskService;
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void reissueDingNoticeTest() {
//        unifyTaskService.reissueDingNotice(eid, 3072L, "2ae5a19ab0f346bdb0475f8e85ffe379", 1L, true);
//    }
//
//}
