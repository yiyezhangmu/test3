//package net.coolcollege.cms.test.service;
//
//import cn.hutool.json.JSONUtil;
//import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
//import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
//import com.github.pagehelper.PageInfo;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@Slf4j
//public class TaskQuestionTest extends IntelligentMainTest {
//
//    @Autowired
//    private UnifyTaskService unifyTaskService;
//
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void taskQuestionReportList() {
//        TaskQuestionQuery query = new TaskQuestionQuery();
//        query.setPageNumber(1);
//        query.setPageSize(10);
//        PageInfo pageInfo = unifyTaskService.taskQuestionReportList(eid, query.getUserIdList(), query.getBeginTime(), query.getEndTime(),
//                query.getPageNumber(), query.getPageSize());
//
//        log.info("pageInfo :{}", JSONUtil.toJsonStr(pageInfo));
//    }
//
//
//}
