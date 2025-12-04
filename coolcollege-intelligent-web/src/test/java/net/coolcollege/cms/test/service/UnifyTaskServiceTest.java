//package net.coolcollege.cms.test.service;
//
//import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
//import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
//import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
//import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
//import com.google.common.collect.Sets;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.util.Set;
//
//
//@Slf4j
//public class UnifyTaskServiceTest extends IntelligentMainTest {
//
//    @Autowired
//    private UnifyTaskService unifyTaskService;
//
//    @Resource
//    private TaskParentMapper taskParentMapper;
//
//    public final static String enterpriseId = "dce6071204284e6995d65058ecca4030";
//    public final static String storeId = "2a2a707364a94870bf404bf76e7baa87";
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testBuildSubTaskBySingleStore() {
//        Set<String> userSet = Sets.newHashSet();
//        userSet.add("1058184329277991");
//        Long parentTaskId = 269L;
//        Long newLoopCount = 6L;
//        Long createTime = System.currentTimeMillis();
//        TaskParentDO parentDO = taskParentMapper.selectTaskById(enterpriseId, parentTaskId);
//        TaskMessageDTO taskMessageDTO = unifyTaskService.buildSubTaskBySingleStore(
//                enterpriseId, storeId, userSet, parentTaskId, newLoopCount, createTime, parentDO,null, null);
//        Assert.assertNotNull("==================subTask  CREATE SUCCESS",taskMessageDTO);
//    }
//
//}
