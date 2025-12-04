//package net.coolcollege.cms.test.service.achievement;
//
//import com.coolcollege.intelligent.dao.achievement.AchievementTypeMapper;
//import com.coolcollege.intelligent.dao.store.StoreMapper;
//import com.coolcollege.intelligent.model.achievement.entity.AchievementDetailDO;
//import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
//import com.coolcollege.intelligent.model.achievement.request.AchievementDetailListRequest;
//import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
//import com.coolcollege.intelligent.model.store.StoreDO;
//import com.coolcollege.intelligent.model.userholder.CurrentUser;
//import com.coolcollege.intelligent.model.userholder.UserHolder;
//import com.coolcollege.intelligent.service.achievement.AchievementService;
//import com.coolcollege.intelligent.service.jms.JmsMessageSendService;
//import com.github.pagehelper.PageInfo;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Collections;
//import java.util.Date;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//
///**
// * @author shuchang.wei
// * @date 2021/5/20 16:43
// */
//@Slf4j
//public class AchievementServiceTest extends IntelligentMainTest {
//    @Resource
//    private AchievementService achievementService;
//    @MockBean
//    private StoreMapper storeMapper;
//    @MockBean
//    private AchievementTypeMapper achievementTypeMapper;
//    @MockBean
//    private JmsMessageSendService jmsMessageSendService;
//
//    @Test
//    @Transactional
//    @Rollback
//    public void testUploadAchievementDetail() throws ParseException {
//
//        CurrentUser user = UserHolder.getUser();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String s = sdf.format(new Date(System.currentTimeMillis()));
//        Date date = sdf.parse(s);
//        AchievementDetailDO achievementDetailDO = new AchievementDetailDO();
//        achievementDetailDO.setAchievementAmount(new BigDecimal(1000));
//        achievementDetailDO.setAchievementTypeId(1L);
//        achievementDetailDO.setProduceTime(date);
//        achievementDetailDO.setDeleted(Boolean.FALSE);
//        achievementDetailDO.setStoreId("12345");
//        achievementDetailDO.setStoreName("测试");
//        achievementDetailDO.setRegionId(1L);
//        achievementDetailDO.setRegionPath("/1/");
//        achievementDetailDO.setProduceUserId(user.getUserId());
//        achievementDetailDO.setProduceUserName("业绩完成人");
//        achievementDetailDO.setCreateUserId(user.getUserId());
//        achievementDetailDO.setCreateUserName("业绩上传人");
//
//        StoreDO storeDO = new StoreDO();
//        storeDO.setStoreId("12345");
//        storeDO.setStoreName("测试门店");
//        when(storeMapper.getByStoreId(eid, achievementDetailDO.getStoreId())).thenReturn(storeDO);
//        AchievementTypeDO achievementTypeDO = new AchievementTypeDO();
//        achievementTypeDO.setId(1L);
//        achievementTypeDO.setName("测试");
//        achievementTypeDO.setLocked(0);
//        when(achievementTypeMapper.getTypeById(eid, achievementDetailDO.getAchievementTypeId())).thenReturn(achievementTypeDO);
//        when(achievementTypeMapper.getListById(eid, Collections.singletonList(achievementDetailDO.getAchievementTypeId()))).thenReturn(Collections.singletonList(achievementTypeDO));
//        //新增业绩详情
//        AchievementDetailDO uploadSuccess = achievementService.uploadAchievementDetail(eid, achievementDetailDO, user);
//        Assert.assertEquals(Boolean.TRUE, uploadSuccess != null);
//        AchievementDetailListRequest request = new AchievementDetailListRequest();
//        request.setPageNum(1);
//        request.setPageSize(10);
//        request.setAchievementTypeIds(Collections.singletonList(1L));
//        request.setProduceUserIds(Collections.singletonList(user.getUserId()));
//        request.setBeginDate(date);
//        request.setEndDate(date);
//        request.setStoreIds(Collections.singletonList("12345"));
//        request.setType("month");
//        //移动端查询业绩详情列表
//        PageInfo appPageInfo = achievementService.listAchievementDetail(eid, request, user);
//        Assert.assertEquals(Boolean.TRUE, appPageInfo.getList().size() > 0);
//
//        //pc端查询业绩详情列表
//        PageInfo pcPageInfo = achievementService.achievementDetailList(eid, request);
//        Assert.assertEquals(Boolean.TRUE, pcPageInfo.getList().size() > 0);
//
//        //业绩详情列表导出
//        when(jmsMessageSendService.sendMessage(anyString(), any())).thenReturn(1);
//        ImportTaskDO taskDO = achievementService.achievementDetailListExport(eid, request, user);
//        Assert.assertEquals(Boolean.TRUE, taskDO != null);
//        //删除业绩详情
//        Boolean deleteSuccess = achievementService.deleteAchievementDetail(eid, 1L, user);
//        Assert.assertEquals(Boolean.TRUE, deleteSuccess);
//
//    }
//}
