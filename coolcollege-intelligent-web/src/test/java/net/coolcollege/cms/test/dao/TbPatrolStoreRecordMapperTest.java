//package net.coolcollege.cms.test.dao;
//
//import com.coolcollege.intelligent.common.util.DateUtils;
//import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
//import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
//import com.github.pagehelper.PageHelper;
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
// * @author Admin
// * @date 2021-07-14 9:59
// */
//@Slf4j
//public class TbPatrolStoreRecordMapperTest extends IntelligentMainTest {
//
//    @Resource
//    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
//
//    @Test
//    public void statisticsUser(){
//        Date beginDate = DateUtils.datePlusSeconds(new Date(), -120000L);
//        Date endDate =  new Date();
//        List<String> userIdList = new ArrayList<>();
//        userIdList.add("ssss");
//        PageHelper.startPage(1, 10);
//        tbPatrolStoreRecordMapper.statisticsUser(eid, userIdList, beginDate, endDate);
//    }
//
//    @Test
//    public void regionPatrolNumRank(){
//        Date beginDate = DateUtils.datePlusSeconds(new Date(), -120000L);
//        Date endDate =  new Date();
//        tbPatrolStoreRecordMapper.regionPatrolNumRank(eid, beginDate, endDate, "");
//    }
//}
