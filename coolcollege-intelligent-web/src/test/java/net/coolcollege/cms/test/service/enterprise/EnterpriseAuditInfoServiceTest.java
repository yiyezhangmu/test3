//package net.coolcollege.cms.test.service.enterprise;
//
//import com.alibaba.fastjson.JSON;
//import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseAuditStatusEnum;
//import com.coolcollege.intelligent.model.enterprise.EnterpriseAuditInfoDO;
//import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseAuditVO;
//import com.coolcollege.intelligent.service.enterprise.EnterpriseAuditInfoService;
//import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * 企业审核测试类
// * @author ：xugangkun
// * @date ：2021/7/28 15:39
// */
//@Slf4j
//public class EnterpriseAuditInfoServiceTest extends IntelligentMainTest {
//
//    @Resource
//    private EnterpriseAuditInfoService enterpriseAuditInfoService;
//
//    static {
//        DataSourceHelper.reset();
//    }
//
//    @Test
//    @Transactional
//    @Rollback(true)
//    public void testEnterpriseSetting() {
//        EnterpriseAuditInfoDO enterpriseAuditInfoDO = new EnterpriseAuditInfoDO();
//        enterpriseAuditInfoDO.setEnterpriseName("测试申请企业");
//        enterpriseAuditInfoDO.setAuditStatus(EnterpriseAuditStatusEnum.AUDIT_PENDING.getValue());
//        enterpriseAuditInfoDO.setMobile("123456789");
//        enterpriseAuditInfoDO.setEmail("123@123.com");
//        enterpriseAuditInfoDO.setPassword("123456");
//        enterpriseAuditInfoDO.setApplyUserName("申请人");
//        enterpriseAuditInfoDO.setRemark("备注");
//        enterpriseAuditInfoService.save(enterpriseAuditInfoDO);
//        log.info("save{}", JSON.toJSONString(enterpriseAuditInfoDO));
//        List<EnterpriseAuditVO> auditList = enterpriseAuditInfoService.enterpriseAuditList(1, 10, null, null);
//        log.info("enterpriseAuditList result:{}", JSON.toJSONString(auditList));
//        enterpriseAuditInfoDO.setRemark("备注2");
//        enterpriseAuditInfoService.updateById(enterpriseAuditInfoDO);
//        EnterpriseAuditInfoDO select = enterpriseAuditInfoService.selectById(enterpriseAuditInfoDO.getId());
//        log.info("selectById id:{} result:{}", enterpriseAuditInfoDO.getId(), JSON.toJSONString(select));
//        select.setAuditStatus(EnterpriseAuditStatusEnum.AUDIT_PASSED.getValue());
//        enterpriseAuditInfoService.auditEnterprise(select);
//        enterpriseAuditInfoService.deleteById(select.getId());
//
//
//    }
//
//}
