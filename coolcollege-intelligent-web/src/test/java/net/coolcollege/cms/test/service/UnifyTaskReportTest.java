//package net.coolcollege.cms.test.service;
//
//import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
//import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
//import com.coolcollege.intelligent.model.userholder.CurrentUser;
//import com.coolcollege.intelligent.model.userholder.UserHolder;
//import com.coolcollege.intelligent.service.unifytask.UnifyTaskReportService;
//import com.github.pagehelper.PageInfo;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@Slf4j
//public class UnifyTaskReportTest extends IntelligentMainTest {
//
//
//    @Autowired
//    private UnifyTaskReportService unifyTaskReportService;
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testPatrolStoreReport() {
//
//        CurrentUser currentUser = UserHolder.getUser();
//        TaskReportQuery query = new TaskReportQuery();
//        query.setTaskType("PATROL_STORE");
//        PageInfo pageInfo = unifyTaskReportService.listTaskReport(eid, query);
//        Assert.assertNotNull("==================listTaskReport SUCCESS",pageInfo);
//
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testTbDisplayReport() {
//
//        CurrentUser currentUser = UserHolder.getUser();
//        TaskReportQuery query = new TaskReportQuery();
//        query.setTaskType(TaskTypeEnum.TB_DISPLAY_TASK.getCode());
//        PageInfo pageInfo = unifyTaskReportService.listTaskReport(eid, query);
//        Assert.assertNotNull("==================listTaskReport SUCCESS",pageInfo);
//
//    }
//
//
//}
