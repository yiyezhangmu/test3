//package net.coolcollege.cms.test.service.task;
//
//import cn.hutool.json.JSONUtil;
//import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
//import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
//import com.coolcollege.intelligent.model.enums.UnifyStatus;
//import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
//import com.coolcollege.intelligent.model.unifytask.dto.ReallocateStoreTaskDTO;
//import com.coolcollege.intelligent.model.unifytask.dto.StorePersonDto;
//import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
//import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
//import com.coolcollege.intelligent.model.userholder.UserHolder;
//import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
//import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
//import com.google.common.collect.Lists;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//
//@Slf4j
//public class UnifyTaskStoreServiceTest extends IntelligentMainTest {
//
//
//    @Autowired
//    private UnifyTaskStoreService unifyTaskStoreService;
//
//    @Resource
//    private TaskStoreMapper taskStoreMapper;
//
//    @Resource
//    private TaskSubMapper taskSubMapper;
//
//    @Autowired
//    private UnifyTaskService unifyTaskService;
//
//    public final static Long taskId = 6314L;
//
//    public final static String storeId = "fe384be4a85a4bc9a84573e73491d793";
//
//    public final static Long loopCount = 1L;
//
//    public final static Long taskStoreId = 77870L;
//
//
//    @Test
//    public void testGetPersonChangeMap() {
//        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(eid, taskStoreId);
//        unifyTaskStoreService.fillSingleTaskStoreExtendAndCcInfo(eid, taskStoreDO);
//        List<String> handerUserList = new ArrayList<>();
//        handerUserList.add("111111");
//        Map<String, List<String>> personChangeMap =  unifyTaskStoreService.getCurrentNodePersonChangeMap(eid, taskStoreDO, handerUserList, null, null);
//        log.info(JSONUtil.toJsonStr(personChangeMap));
//    }
//
//    @Test
//    public void testSelectTaskStorAllNodePerson() {
//        Map<String, List<String>> allNodePerson =  unifyTaskStoreService.selectTaskStorAllNodePerson(eid, taskId, storeId, loopCount);
//        log.info(JSONUtil.toJsonStr(allNodePerson));
//    }
//
//    @Test
//    public void testReplaceTaskStoreNodePerson() {
//        String node = "1";
//        String fromUserId = "090633140926401153";
//        String toUserId = "090633140926401154";
//        unifyTaskStoreService.replaceTaskStoreNodePerson(eid, taskId, storeId, loopCount, node, fromUserId, toUserId);
//    }
//
//    @Test
//    public void testSelectCcPersonInfoByTaskList() {
//        List<String> ccUserIdList = unifyTaskStoreService.selectCcPersonInfoByTaskList(eid, taskId, storeId, loopCount);
//        log.info(JSONUtil.toJsonStr(ccUserIdList));
//    }
//
//    @Test
//    public void testSelectAuditUserIdList() {
//        List<String> auditUserIdList = unifyTaskStoreService.selectAuditUserIdList(eid, taskId, storeId, loopCount);
//        log.info(JSONUtil.toJsonStr(auditUserIdList));
//    }
//
//    @Test
//    public void testSelectTaskPersonByTaskAndStore() {
//        List<String> storeIdList = Lists.newArrayList();
//        storeIdList.add(storeId);
//        List<StorePersonDto> storePersonDtoList = unifyTaskStoreService.selectTaskPersonByTaskAndStore(eid, storeIdList, taskId);
//        log.info(JSONUtil.toJsonStr(storePersonDtoList));
//    }
//
//    @Test
//    public void testSelectALLNodeUserInfoList() {
//        List<Long> taskIdList = Lists.newArrayList();
//        taskIdList.add(0L);
//        List<String> storeIdList = Lists.newArrayList();
//        storeIdList.add(storeId);
//        List<UnifyPersonDTO> unifyPersonDTOList = unifyTaskStoreService.selectALLNodeUserInfoList(eid, taskIdList, storeIdList, 0L);
//        log.info(JSONUtil.toJsonStr(unifyPersonDTOList));
//    }
//
//    @Test
//    public void testUpdateReallocateNodePerson() {
//
//        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(eid, 78394L);
//        TaskSubVO taskSubVO = taskSubMapper.getLatestSubId(eid, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), null, UnifyStatus.ONGOING.getCode(), taskStoreDO.getNodeNo());
//
//        unifyTaskStoreService.fillSingleTaskStoreExtendAndCcInfo(eid, taskStoreDO);
//        List<String> handerUserList = new ArrayList<>();
//        handerUserList.add("111111");
//        List<String> approveUserList = new ArrayList<>();
//        approveUserList.add("01150353342033189336");
//        approveUserList.add("1058184329277991");
//        List<String> recheckUserList = new ArrayList<>();
//        recheckUserList.add("333333");
//        unifyTaskStoreService.updateReallocateNodePerson(eid, taskStoreDO, handerUserList, approveUserList, recheckUserList, taskSubVO, null);
//        List<TaskStoreDO> taskStoreHasExtendCcInfoList = taskStoreMapper.selectExtendAndCcInfoByTaskStoreIds(eid, Collections.singletonList(taskStoreDO.getId()));
//        log.info(JSONUtil.toJsonStr(taskStoreHasExtendCcInfoList));
//    }
//
//    /**
//     * 门店任务重新分配
//     */
//    @Test
//    public void testReallocateStoreTask() {
//        String dingCorpId = "dingef2502a50df74ccc35c2f4657eb6378f";
//        ReallocateStoreTaskDTO task = new  ReallocateStoreTaskDTO();
//        task.setTaskStoreId(taskStoreId);
//        List<String> handerUserList = new ArrayList<>();
//        handerUserList.add("1058184329277991");
//        handerUserList.add("01150353342033189336");
//        task.setHanderUserList(handerUserList);
//        unifyTaskService.reallocateStoreTask(eid, task, dingCorpId, UserHolder.getUser());
//        List<TaskStoreDO> taskStoreHasExtendCcInfoList = taskStoreMapper.selectExtendAndCcInfoByTaskStoreIds(eid, Collections.singletonList(taskStoreId));
//        log.info(JSONUtil.toJsonStr(taskStoreHasExtendCcInfoList));
//    }
//
//}
