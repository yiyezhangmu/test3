//package net.coolcollege.cms.test.dao;
//
//import cn.hutool.json.JSONUtil;
//import com.coolcollege.intelligent.common.util.DateUtils;
//import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
//import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsProblemRankDTO;
//import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionDTO;
//import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author byd
// * @date 2021-05-31 14:46
// */
//@Slf4j
//public class TaskStoreMapperTest extends IntelligentMainTest {
//
//    @Resource
//    private TaskStoreMapper taskStoreMapper;
//
//    @Test
//    public void testPatrolStoreStatisticsRegionColumn(){
//        PatrolStoreStatisticsRegionDTO dto =
//                taskStoreMapper.patrolStoreStatisticsRegionColumn(eid, "/1/", false, null, null, null);
//        log.info("testPatrolStoreStatisticsRegionColumn1 :{}", JSONUtil.toJsonStr(dto));
//        PatrolStoreStatisticsRegionDTO dto2 =
//                taskStoreMapper.patrolStoreStatisticsRegionColumn(eid, "/1/20001/", false, null, null, null);
//        log.info("testPatrolStoreStatisticsRegionColumn2 :{}", JSONUtil.toJsonStr(dto2));
//    }
//
//    @Test
//    public void testRegionQuestionNumRank(){
//        List<PatrolStoreStatisticsProblemRankDTO> dto =
//                taskStoreMapper.regionQuestionNumRank(eid, null, null, "/1/");
//        log.info("testPatrolStoreStatisticsRegionColumn :{}", JSONUtil.toJsonStr(dto));
//        List<PatrolStoreStatisticsProblemRankDTO> dto2 =
//                taskStoreMapper.regionQuestionNumRank(eid, null, null, "/1/20001/");
//        log.info("testPatrolStoreStatisticsRegionColumn2 :{}", JSONUtil.toJsonStr(dto2));
//    }
//
//    @Test
//    public void testTaskStoreByRegionPathOrStoreId(){
//        List<TaskStoreDO> dto =
//                taskStoreMapper.getTaskStoreByRegionPathOrStoreId(eid, "/1/", null, null, null);
//        log.info("testPatrolStoreStatisticsRegionColumn :{}", JSONUtil.toJsonStr(dto));
//        List<TaskStoreDO> dto2 =
//                taskStoreMapper.getTaskStoreByRegionPathOrStoreId(eid, "/1/20001/", null, null, null);
//        log.info("testPatrolStoreStatisticsRegionColumn2 :{}", JSONUtil.toJsonStr(dto2));
//    }
//
//    @Test
//    public void patrolStoreStatisticsRegionColumn(){
//        String regionPath = "aa";
//        boolean isRoot = true;
//        Date beginDate = DateUtils.datePlusSeconds(new Date(), -1200L);
//        Date endDate =  new Date();
//        List<String> storeIds = new ArrayList<>();
//        storeIds.add("aaaa");
//        taskStoreMapper.patrolStoreStatisticsRegionColumn(eid, regionPath, isRoot, beginDate, endDate, storeIds);
//    }
//}
