//package net.coolcollege.cms.test.service;
//
//import com.coolcollege.intelligent.common.enums.AppTypeEnum;
//import com.coolcollege.intelligent.common.sync.conf.Role;
//import com.coolcollege.intelligent.common.sync.vo.AuthScope;
//import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
//import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
//import com.coolcollege.intelligent.model.impoetexcel.dto.WeComUserImportDTO;
//import com.coolcollege.intelligent.service.importexcel.UserImportService;
//import com.coolcollege.intelligent.service.login.LoginService;
//import com.coolcollege.intelligent.service.qywx.ChatService;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//
//@Slf4j
//public class QywxServiceTest extends IntelligentMainTest {
//
//    @Autowired
//    private ChatService chatService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private UserImportService userImportService;
//
//    private static final ExecutorService EXECUTOR_SERVICE =
//            new ThreadPoolExecutor(20, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testGetPyAccessToken() {
//        String aa = null;
//        try {
//            aa = chatService.getPyAccessToken("ww708ebbb8488dda51", AppTypeEnum.WX_APP2.getValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("aaa==" + aa);
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testGetUserDetail() {
//        EnterpriseUserDO aa = null;
//        try {
//            aa = chatService.getUserDetail("ww708ebbb8488dda51", "ceshijiaren2","Twr5dbOD1po6kcvgToimyNIHxa78yc0RsdGVoyply-mTHn__hZczWxgq4qfK7i_ZBTLn7ai-hP-sTkJl7VP-OMxfys0It5dc6rVfvKfFY13m1TAC7ePFr9TY4oedqYW1RKaNorouyNoAJPjZbIxZVo83Ln-TjlYmi6F3cVHHgsaB0CbS267MtHWNN-uVodlXcMDyA2s2UpG-zBWoBd5LRA",
//                    false, Role.EMPLOYEE.getId(), AppTypeEnum.WX_APP.getValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("aaa==" + aa);
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testGetAuthScope() {
//        AuthScope aa = null;
//        try {
//            aa = chatService.getAuthScope("E6v4ujAUSVwmSWumqXi2fi2qwqKkDDq1wxmB4EDeRKBQ33hkFv6rVyOFNnpg_2F6WI_c6llPRQvyOtzxkxHb_GzzRxpzncLeiINXRr39RgoMtGWm1m--15heast5z9wJzCs5Ec8SJwXrFr3R50clGmif44OkWQwOJ4Lz_HLI9mikca23eSVFty_4R8yUf4R2oAx7MAmY_7kkiWDSTZbK2A");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("aaa==" + aa.toString());
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testWxIsvLogin() {
//        Object aa = null;
//        HttpServletRequest httpServletRequest = null;
//        try {
//
//            aa = loginService.wxIsvLogin("OOyGMjRDNgowuHtDbkBL82vWGS4WO7ja58LdGNIKWMc", AppTypeEnum.WX_APP2.getValue(),
//                    httpServletRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info("aaa==" + aa.toString());
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testImportWeComData() {
//        String eid = "dad36af7d8804db39a33c722ead566de";
//        String dbName = "coolcollege_intelligent_10";
//        Future<List<WeComUserImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> initWeComUserImportDTO());
//        ImportTaskDO taskInfo = new ImportTaskDO("wxp的测试百货店通讯录(1).xlsx", "user", true,
//                1, "a100000000", "a100000000", 1623811185843L);
//        userImportService.importWeComData(eid, null, dbName, importTask, null, taskInfo);
//        log.info("testImportWeComData end");
//    }
//
//    private List<WeComUserImportDTO> initWeComUserImportDTO() {
//        List<WeComUserImportDTO> list = new ArrayList<>();
//        WeComUserImportDTO weComUserImportDTO = new WeComUserImportDTO();
//        weComUserImportDTO.setUserId("WangXiaoPeng");
//        weComUserImportDTO.setDepartment("wxp的测试百货店;wxp的测试百货店/研发部/研发一部");
//        weComUserImportDTO.setName("王晓鹏");
//        weComUserImportDTO.setMobile("13868121405");
//        weComUserImportDTO.setStatus("已激活");
//        weComUserImportDTO.setGender("男");
//        list.add(weComUserImportDTO);
//        return list;
//    }
//
//
//
//
//}
