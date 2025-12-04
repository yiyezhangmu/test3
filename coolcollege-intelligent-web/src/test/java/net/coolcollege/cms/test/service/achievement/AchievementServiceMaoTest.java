//package net.coolcollege.cms.test.service.achievement;
//
//import cn.hutool.core.util.ArrayUtil;
//import com.coolcollege.intelligent.common.page.DataGridResult;
//import com.coolcollege.intelligent.common.util.DateUtil;
//import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetDTO;
//import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
//import com.coolcollege.intelligent.model.achievement.vo.*;
//import com.coolcollege.intelligent.model.userholder.CurrentUser;
//import com.coolcollege.intelligent.model.userholder.UserHolder;
//import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
//import com.coolcollege.intelligent.service.achievement.AchievementTargetService;
//import com.coolcollege.intelligent.service.achievement.AchievementTypeService;
//import lombok.RequiredArgsConstructor;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.apache.commons.collections4.ListUtils;
//import org.checkerframework.checker.units.qual.A;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class AchievementServiceMaoTest extends IntelligentMainTest {
//    @Autowired
//    private AchievementTypeService achievementTypeService;
//    @Autowired
//    private AchievementTargetService achievementTargetService;
//    @Autowired
//    private AchievementStatisticsService achievementStatisticsService;
//
//    /**
//     * 测试业绩类型
//     *
//     * @param
//     * @return void
//     * @author mao
//     * @date 2021/5/27 15:57
//     */
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testAchievementType() {
//        CurrentUser user = UserHolder.getUser();
//        user.setName("凡人韩立");
//        // 新增
//        AchievementTypeReqVO req = new AchievementTypeReqVO();
//        String typeName = "合体期韩立" + new Random(1).nextInt(100);
//        req.setName(typeName);
//        AchievementTypeResVO typeAddRes = achievementTypeService.insertAchievementType(eid, req, user);
//        Assert.assertNotNull(typeAddRes.getId());
//        // 查询
//        List<AchievementTypeResVO> typeResList = achievementTypeService.listAchievementTypes(eid);
//        Assert.assertNotNull(typeResList);
//        // 修改
//        req.setId(typeAddRes.getId());
//        req.setName("大乘期韩立" + new Random(1).nextInt(100));
//        AchievementTypeResVO typeUpdateRes = achievementTypeService.updateType(eid, req, user);
//        Assert.assertEquals(req.getName(), typeUpdateRes.getName());
//        // 删除
//        achievementTypeService.deleteType(eid, req);
//        typeResList = achievementTypeService.listAchievementTypes(eid);
//        Assert.assertNotNull(typeResList);
//    }
//
//    /**
//     * 测试门店目标
//     *
//     * @param
//     * @return void
//     * @author mao
//     * @date 2021/5/27 15:59
//     */
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testAchievementTarget() {
//        CurrentUser user = UserHolder.getUser();
//        user.setName("凡人韩立");
//        // 新增门店目标
//        AchievementTargetVO req = new AchievementTargetVO();
//        req.setStoreId("1943adc1ed954300a0c806ae4ca64ecd");
//        req.setAchievementYear(2022);
//        req.setYearAchievementTarget(new BigDecimal(5211314.22));
//        AchievementTargetTimeReqVO detail = new AchievementTargetTimeReqVO();
//        detail.setAchievementTarget(new BigDecimal(1314.88));
//        detail.setBeginDate(DateUtil.getFirstOfDayMonth(new Date()));
//        req.setTargetDetail(Arrays.asList(detail));
//        AchievementTargetVO resTarget = achievementTargetService.saveAchievementTarget(eid, req, user);
//        Assert.assertNotNull(resTarget.getId());
//        // 查询门店目标
//        req.setPageNum(1);
//        req.setPageSize(10);
//        req.setStoreIds(Arrays.asList(req.getStoreId()));
//        DataGridResult page = achievementTargetService.listTargetPages(eid, req);
//        Assert.assertNotNull(page);
//        // 修改门店
//        AchievementTargetVO updateRes = achievementTargetService.updateAchievementTarget(eid, resTarget, user);
//        Assert.assertNotNull(updateRes);
//        // 删除门店
//        achievementTargetService.deleteTarget(eid, resTarget, user);
//        Assert.assertNotNull(resTarget);
//    }
//
//    /**
//     * 测试报表统计
//     *
//     * @param
//     * @return void
//     * @author mao
//     * @date 2021/5/27 16:58
//     */
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testAchievementStatics() {
//        CurrentUser user = UserHolder.getUser();
//        user.setName("凡人韩立");
//        AchievementStatisticsReqVO req = new AchievementStatisticsReqVO();
//        req.setBeginDate(DateUtil.getFirstOfDayMonth(new Date()));
//        req.setEndDate(new Date());
//        req.setRegionIds(Arrays.asList(37136l));
//        AchievementStatisticsRegionListVO tableRes =
//            achievementStatisticsService.getRegionStatisticsTable(eid, req, user);
//        Assert.assertNotNull(tableRes);
//        AchievementStatisticsRegionSeriesVO chartRes =
//            achievementStatisticsService.getRegionStatisticsChart(eid, req, user);
//        Assert.assertNotNull(chartRes);
//        AchievementStatisticsStoreTableVO storeRes = achievementStatisticsService.getStoreStatistics(eid,
//            "1943adc1ed954300a0c806ae4ca64ecd", DateUtil.getFirstOfDayMonth(new Date()));
//        Assert.assertNotNull(storeRes);
//    }
//
//}
