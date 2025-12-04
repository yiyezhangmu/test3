//package net.coolcollege.cms.test.service.user;
//
//import com.alibaba.fastjson.JSONObject;
//import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
//import com.coolcollege.intelligent.common.response.ResponseResult;
//import com.coolcollege.intelligent.common.util.UUIDUtils;
//import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
//import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMappingMapper;
//import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
//import com.coolcollege.intelligent.model.enums.LoginTypeEnum;
//import com.coolcollege.intelligent.model.enums.SmsCodeTypeEnum;
//import com.coolcollege.intelligent.model.login.UserLoginDTO;
//import com.coolcollege.intelligent.model.user.*;
//import com.coolcollege.intelligent.model.userholder.UserHolder;
//import com.coolcollege.intelligent.service.aliyun.AliyunSmsService;
//import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
//import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
//import com.coolcollege.intelligent.service.login.strategy.LoginStrategy;
//import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
//import lombok.extern.slf4j.Slf4j;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Test;
//
//import javax.annotation.Resource;
//import java.util.Arrays;
//import java.util.Date;
//
///**
// * @author zhangchenbiao
// * @FileName: UserLoginTest
// * @Description:用户登录单测
// * @date 2021-07-23 15:15
// */
//@Slf4j
//public class UserLoginTest  extends IntelligentMainTest {
//
//    @Resource
//    private AliyunSmsService aliyunSmsService;
//    @Resource
//    private EnterpriseUserService enterpriseUserService;
//    @Resource
//    private EnterpriseUserMappingService enterpriseUserMappingService;
//    @Resource
//    private EnterpriseUserMappingMapper enterpriseUserMappingMapper;
//    @Test
//    public void passwordLogin(){
//        UserLoginDTO param = new UserLoginDTO();
//        param.setPassword("1234567");
//        param.setMobile("17681878615");
//        param.setLoginType(LoginTypeEnum.PASSWORD);
//        ResponseResult login = SpringContextUtil.getBean(param.getLoginType().getClazzName(), LoginStrategy.class).login(param);
//        log.info("login:{}", JSONObject.toJSONString(login));
//    }
//
//    @Test
//    public void smsLogin(){
//        UserLoginDTO param = new UserLoginDTO();
//        param.setMobile("17681878615");
//        param.setSmsCode("693292");
//        param.setLoginType(LoginTypeEnum.SMS);
//        ResponseResult login = SpringContextUtil.getBean(param.getLoginType().getClazzName(), LoginStrategy.class).login(param);
//        log.info("login:{}", JSONObject.toJSONString(login));
//    }
//
//    @Test
//    public void getSmsCode(){
//        ResponseResult responseResult = aliyunSmsService.sendSmsCode("17681878615", SmsCodeTypeEnum.IMPROVE_INFO);
//    }
//
//    @Test
//    public void improveUserInfo(){
//        ImproveUserInfoDTO param = new ImproveUserInfoDTO();
//        param.setName("BBB");
//        param.setPassword("123456789");
//        param.setMobile("17681878615");
//        param.setAvatar("avatr");
//        param.setSmsCode("858610");
//        enterpriseUserService.improveUserInfo(param, UserHolder.getUser());
//    }
//
//    @Test
//    public void modifyPassword(){
//        ModifyPasswordDTO param = new ModifyPasswordDTO();
//        param.setPassword("123456");
//        param.setMobile("17681878615");
//        param.setSmsCode("844952");
//        enterpriseUserService.modifyPassword(param, "52bC052yiShz0UF4xuIsp0giEiE");
//    }
//
//    @Test
//    public void forgetPassword(){
//        ModifyPasswordDTO param = new ModifyPasswordDTO();
//        param.setPassword("1234567");
//        param.setMobile("17681878615");
//        param.setSmsCode("214733");
//        enterpriseUserService.forgetPassword(param);
//    }
//
//
//    @Test
//    public void addUser(){
//        UserAddDTO param = new UserAddDTO();
//        param.setUserStatus(UserStatusEnum.NORMAL.getCode());
//        param.setAvatar("sfdfd");
//        param.setMobile("17681870016");
//        param.setName("哈哈哈哈哈哈");
//        param.setEmail("222@qq.com");
//        param.setJobnumber(UUIDUtils.get8UUID());
//        param.setRemark("add");
//        enterpriseUserService.addUser(param, "45f92210375346858b6b6694967f44de", "coolcollege_intelligent_2");
//    }
//
//    @Test
//    public void batchUpdateUserStatus(){
//        BatchUserStatusDTO param = new BatchUserStatusDTO();
//        param.setUserStatus(UserStatusEnum.FREEZE.getCode());
//        param.setUnionids(Arrays.asList("app297abffc3d674addaece47a9aabcfd90","ylk2XwHHiSbD1Hb1ii29GzwQiEiE","r3X1MG3rOmsiE","R3WqT19PCl0rn3fKqbCTnAiEiE","8f70cMaSlmb0UF4xuIsp0giEiE"));
//        enterpriseUserService.batchUpdateUserStatus(param,"45f92210375346858b6b6694967f44de","coolcollege_intelligent_2");
//    }
//
//    @Test
//    public void updateUserCenterInfo(){
//        UpdateUserCenterDTO param = new UpdateUserCenterDTO();
//        param.setAvatar("jsjjs");
//        param.setName("章臣彪");
//        param.setEmail("1111@qq.com");
//        enterpriseUserService.updateUserCenterInfo(param, UserHolder.getUser());
//    }
//
//    @Test
//    public void modifyUserMobile(){
//        ModifyUserMobileDTO param = new ModifyUserMobileDTO();
//        param.setMobile("17681878615");
//        param.setSmsCode("338185");
//        enterpriseUserService.modifyUserMobile(param,UserHolder.getUser());
//    }
//
//    @Test
//    public void inviteRegister(){
//        InviteUserRegisterDTO param = new InviteUserRegisterDTO();
//        param.setMobile("17681878600");
//        param.setEmail("1234@qq.com");
//        param.setEnterpriseId("45f92210375346858b6b6694967f44de");
//        param.setPassword("1234567");
//        param.setName("NNNN");
//        param.setSmsCode("748041");
//        param.setShareKey("95a3b4dda76a4fe58e00451c1f8fa580");
//        enterpriseUserService.inviteRegister(param);
//    }
//
//    @Test
//    public void testUserMapping(){
//        EnterpriseUserMappingDO param = new EnterpriseUserMappingDO();
//        param.setId(UUIDUtils.get32UUID());
//        param.setUserId("95e0f8b0bfa948ffaa7ecd18c1dd756a");
//        param.setEnterpriseId("c5d853edf4024575af79dc412d7c6e97");
//        param.setUserStatus(2);
//        param.setUnionid("Rq3jqmhqy7vdzyviPVNaNSgiEiE");
//        enterpriseUserMappingService.saveEnterpriseUserMapping(param);
//    }
//
//    @Test
//    public void test(){
//        DataSourceHelper.reset();
//        EnterpriseUserMappingDO test = new EnterpriseUserMappingDO();
//        test.setId(UUIDUtils.get32UUID());
//        test.setUserId(UUIDUtils.get32UUID());
//        test.setEnterpriseId(UUIDUtils.get32UUID());
//        test.setEnterpriseName(UUIDUtils.get32UUID());
//        test.setAdmin(true);
//        test.setUpdateTime(new Date());
//        test.setCreateTime(new Date());
//        test.setUserStatus(2);
//        test.setUnionid("fsdfjhsdfjhsjfhjsdf");
//        enterpriseUserMappingMapper.save(test);
//    }
//}
