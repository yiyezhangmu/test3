//package net.coolcollege.cms.test.dao;
//
//import com.coolcollege.intelligent.common.util.DateUtils;
//import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
//import com.github.pagehelper.PageHelper;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.apache.ibatis.annotations.Param;
//import org.junit.Test;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author Admin
// * @date 2021-07-14 9:44
// */
//@Slf4j
//public class TbDataStaTableColumnMapperTest extends IntelligentMainTest {
//
//    @Resource
//    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
//
//    @Test
//    public void statisticsColumnPerTable(){
//        Date beginDate = DateUtils.datePlusSeconds(new Date(), -120000L);
//        Date endDate =  new Date();
//        List<Long> columnIdList = new ArrayList<>();
//        columnIdList.add(2059L);
//        columnIdList.add(2060L);
//        columnIdList.add(2063L);
//        PageHelper.startPage(1,10);
//        tbDataStaTableColumnMapper.statisticsColumnPerTable(eid, columnIdList, beginDate, endDate);
//    }
//
//
//    @Test
//    public void patrolStoreStatisticsRegionRecord(){
//        String regionPath = "/1/";
//        boolean isRoot = false;
//        Date beginDate = DateUtils.datePlusSeconds(new Date(), -120000L);
//        Date endDate =  new Date();
//        List<String> storeIds = new ArrayList<>();
//        storeIds.add("ssss");
//        tbDataStaTableColumnMapper.patrolStoreStatisticsRegionRecord(eid, regionPath, isRoot, beginDate, endDate, storeIds);
//    }
//
//}
