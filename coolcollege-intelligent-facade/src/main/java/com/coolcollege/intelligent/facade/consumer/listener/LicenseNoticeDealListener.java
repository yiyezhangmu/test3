package com.coolcollege.intelligent.facade.consumer.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.license.LicenseNoticeDTO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.FormPickerEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: LicenseNoticeDealListener
 * @Description: 证照通知
 * @date 2022-11-08 15:22
 */
@Service
@Slf4j
public class LicenseNoticeDealListener implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Resource
    private JmsTaskService jmsTaskService;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private RegionService regionService;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    private final static String LICENSE_STORE_IMAGE = "https://oss-cool.coolstore.cn/notice_pic/store_license_notice.png";

    private final static String LICENSE_USER_IMAGE = "https://oss-cool.coolstore.cn/notice_pic/user_license_notice.png";

    private final static String LICENSE_NOTICE_KEY = "license_notice_key:{0}:{1}:{2}_{3}_{4}";

    private final static String TITLE = "证照到期提醒";

    private final static String CONTENT = "您管辖范围有{0}即将到期";

    private final static String USER_TITLE = "员工证照到期提醒";

    private final static String USER_CONTENT = "您的证照即将到期，请尽快办理";

    private final static String USER = "user";

    private final static String STORE = "store";



    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        log.info("证照预警通知：{}", text);
        String lockKey = "LicenseNoticeDealListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(!lock){
            return Action.ReconsumeLater;
        }
        try {
            LicenseNoticeDTO licenseNotice = JSONObject.parseObject(text, LicenseNoticeDTO.class);
            if(Objects.isNull(licenseNotice)){
                return Action.CommitMessage;
            }
            String enterpriseId = licenseNotice.getEnterpriseId();
            EnterpriseConfigDTO enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(licenseNotice.getNoticeTarget()) || Objects.isNull(enterpriseConfig)){
                return Action.CommitMessage;
            }
            //处理发送人 和 发消息
            dealSendMessageUsers(licenseNotice, enterpriseConfig);
        } catch (ApiException e) {
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR);
        } finally {
            redisUtilPool.delKey(lockKey);
        }
        log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
        return Action.CommitMessage;
    }

    /**
     * 处理发送消息
     * @param licenseNotice
     * @param enterpriseConfig
     */
    private void dealSendMessageUsers (LicenseNoticeDTO licenseNotice, EnterpriseConfigDTO enterpriseConfig){
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String enterpriseId = licenseNotice.getEnterpriseId();
        String dingCorpId = enterpriseConfig.getDingCorpId();
        String appType = enterpriseConfig.getAppType();
        Long noticeSettingId = licenseNotice.getNoticeSettingId();
        //证照过期的门店
        List<String> storeIds = licenseNotice.getStoreIds();
        //证照过期的用户
        List<String> userIds = licenseNotice.getUserIds();
        //通知对象
        List<GeneralDTO> noticeTargets = licenseNotice.getNoticeTarget();
        //通知对象中配置的角色
        List<String> roleIds = noticeTargets.stream().filter(o -> FormPickerEnum.POSITION.getCode().equals(o.getType())).map(GeneralDTO::getValue).collect(Collectors.toList());
        //通知对象中 配置的人员
        List<String> noticeSettingUserIds = noticeTargets.stream().filter(o -> FormPickerEnum.PERSON.getCode().equals(o.getType())).map(GeneralDTO::getValue).collect(Collectors.toList());
        //管理员
        List<String> masterUserIds = enterpriseUserRoleDao.selectUserIdsByRoleIdList(enterpriseId, Arrays.asList(Long.valueOf(Role.MASTER.getId())));
        //门店类型的证照
        if(CollectionUtils.isNotEmpty(storeIds)){
            String licenseTypeStr = StringUtils.join(licenseNotice.getStoreLicenseTypeMap().values(), Constants.PAUSE);
            //获取拥有门店权限的人
            List<String> storeAuthUserIds = authVisualService.getStoreAuthUserIds(enterpriseId, storeIds);
            List<String> sendUserIds = new ArrayList<>();;
            if(CollectionUtils.isNotEmpty(roleIds)){
                sendUserIds = enterpriseUserRoleDao.getUserIdsByRoleIds(enterpriseId, roleIds, storeAuthUserIds);
            }
            //当配了角色  同时又配了人的时候  筛选出为管理员的人
            if(CollectionUtils.isNotEmpty(noticeSettingUserIds)){
                List<String> statusNormalUserIds = enterpriseUserService.selectByUserIdsAndStatus(enterpriseId, noticeSettingUserIds, UserStatusEnum.NORMAL.getCode());
                List<String> configUserIds = ListUtils.emptyIfNull(statusNormalUserIds).stream().filter(o -> masterUserIds.contains(o) || storeAuthUserIds.contains(o)).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(configUserIds)){
                    sendUserIds.addAll(configUserIds);
                }
            }
            sendMessage(enterpriseId, dingCorpId, appType, sendUserIds, TITLE, MessageFormat.format(CONTENT, licenseTypeStr), STORE, noticeSettingId);
        }
        //员工类型的证照
        if(CollectionUtils.isNotEmpty(userIds)){
            String licenseTypeStr = StringUtils.join(licenseNotice.getUserLicenseTypeMap().values(), Constants.PAUSE);
            //获取用户所在的部门
            List<UserRegionMappingDO> userRegionList = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, userIds);
            //区域 有权限的用户  key->regionId   value->userIds
            Map<String, List<String>> regionUserMap = ListUtils.emptyIfNull(userRegionList).stream().collect(Collectors.groupingBy(UserRegionMappingDO::getRegionId, Collectors.mapping(k->k.getUserId(), Collectors.toList())));

            List<String> regionIds = ListUtils.emptyIfNull(userRegionList).stream().map(UserRegionMappingDO::getRegionId).distinct().collect(Collectors.toList());
            //获取这些区域上的所有节点
            Map<String, List<String>> allRegionIdsMap = regionService.getParentIdsMapByRegionIds(enterpriseId, regionIds);
            //所有的区域
            List<String> allRegionIds = allRegionIdsMap.values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
            //获取节点的所有子节点
            Map<String, List<String>> subIdsMapByRegionIds = regionService.getSubIdsMapByRegionIds(enterpriseId, allRegionIds);
            //区域有哪些人管
            List<UserAuthMappingDO> userAuthRegionLists = userAuthMappingMapper.getUserAuthByMappingIds(enterpriseId, allRegionIds);
            //人管哪些区域  需要把区域中的之区域全部换算出来
            Map<String, List<String>> userRegionIdsMap = ListUtils.emptyIfNull(userAuthRegionLists).stream().collect(Collectors.groupingBy(k -> k.getUserId(), Collectors.mapping(k->k.getMappingId(), Collectors.toList())));
            //所有的人
            List<String> allUserIds = ListUtils.emptyIfNull(userAuthRegionLists).stream().map(o -> o.getUserId()).distinct().collect(Collectors.toList());
            List<String> adminUserIds = enterpriseUserRoleDao.selectUserIdsByRoleIdList(enterpriseId, Arrays.asList(Long.valueOf(Role.MASTER.getId())));
            if(CollectionUtils.isNotEmpty(allUserIds) && CollectionUtils.isNotEmpty(adminUserIds)){
                allUserIds.addAll(adminUserIds);
            }
            List<String> sendUserIds = new ArrayList<>();;
            if(CollectionUtils.isNotEmpty(roleIds)){
                sendUserIds = enterpriseUserRoleDao.getUserIdsByRoleIds(enterpriseId, roleIds, allUserIds);
            }
            //当配了角色  同时又配了人的时候  筛选出为管理员的人  或者  有管辖范围存在交集的人
            if(CollectionUtils.isNotEmpty(noticeSettingUserIds)){
                List<String> statusNormalUserIds = enterpriseUserService.selectByUserIdsAndStatus(enterpriseId, noticeSettingUserIds, UserStatusEnum.NORMAL.getCode());
                List<UserRegionMappingDO> userRegionIds = userRegionMappingDAO.listByUserIdsAndRegionIds(enterpriseId, statusNormalUserIds, null);
                Map<String, List<UserRegionMappingDO>> userRegionMap = userRegionIds.stream().collect(Collectors.groupingBy(k -> k.getUserId()));
                for (String statusNormalUserId : statusNormalUserIds) {
                    if(sendUserIds.contains(statusNormalUserId)){
                        continue;
                    }
                    if(masterUserIds.contains(statusNormalUserId)){
                        sendUserIds.add(statusNormalUserId);
                        continue;
                    }
                    List<UserRegionMappingDO> regionUserIds = userRegionMap.get(statusNormalUserId);
                    List<String> noticeUserRegionIds = ListUtils.emptyIfNull(regionUserIds).stream().map(UserRegionMappingDO::getRegionId).distinct().collect(Collectors.toList());
                    boolean isInRegionUser = Collections.disjoint(allRegionIds, noticeUserRegionIds);
                    if(!isInRegionUser){
                        sendUserIds.add(statusNormalUserId);
                    }
                }
            }
            //是否存在 过期的人员 同时也是消息接收人
            List<String> reUserIds = sendUserIds.stream().filter(o -> userIds.contains(o)).distinct().collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(reUserIds)){
                //判断这些人 是否所管辖的区域内只存在自己
                for (String reUserId : reUserIds) {
                    //人所在的区域
                    List<String> reUserRegionIds = userRegionIdsMap.get(reUserId);
                    List<String> userAuthAllRegionIds = new ArrayList<>();
                    ListUtils.emptyIfNull(reUserRegionIds).forEach(k->Optional.ofNullable(subIdsMapByRegionIds.get(k)).ifPresent(userAuthAllRegionIds::addAll));
                    List<String> users = new ArrayList<>();
                    ListUtils.emptyIfNull(userAuthAllRegionIds).forEach(k->Optional.ofNullable(regionUserMap.get(k)).ifPresent(users::addAll));
                    if(CollectionUtils.isNotEmpty(users)){
                        List<String> list = users.stream().filter(o -> userIds.contains(o)).distinct().collect(Collectors.toList());
                        //管辖区域内 仅自己证照过期
                        if(CollectionUtils.isNotEmpty(list) && list.size() == Constants.INDEX_ONE){
                            //首先给员工发送通知
                            sendMessage(enterpriseId, dingCorpId, appType, Arrays.asList(reUserId), USER_TITLE, USER_CONTENT, USER, noticeSettingId);
                        }else{
                            //发管辖范围的通知  自己的通知将接受不到
                            sendMessage(enterpriseId, dingCorpId, appType, Arrays.asList(reUserId), TITLE, MessageFormat.format(CONTENT, licenseTypeStr), USER, noticeSettingId);
                        }
                    }
                    //区域下有那些人
                }
            }
            sendMessage(enterpriseId, dingCorpId, appType, sendUserIds, TITLE, MessageFormat.format(CONTENT, licenseTypeStr), USER, noticeSettingId);
            //首先给员工发送通知
            sendMessage(enterpriseId, dingCorpId, appType, userIds, USER_TITLE, USER_CONTENT, USER, noticeSettingId);
        }
    }


    /**
     * 发送消息
     * @param enterpriseId
     * @param dingCorpId
     * @param appType
     * @param sendUserIds
     * @param title
     * @param content
     * @param noticeType
     */
    public void sendMessage(String enterpriseId, String dingCorpId, String appType, List<String> sendUserIds, String title, String content, String noticeType, Long noticeSettingId){
        if(CollectionUtils.isEmpty(sendUserIds)){
            return;
        }
        List<String> finalSendUserIds = new ArrayList<>();
        for (String userId : sendUserIds) {
            /*String key = MessageFormat.format(LICENSE_NOTICE_KEY, LocalDate.now(), enterpriseId, userId, noticeType, noticeSettingId);
            boolean result = redisUtilPool.setNxExpire(key, Constants.ONE_VALUE_STRING, RedisConstant.ONE_DAY_SECONDS * 1000);*/
            if(true){
                finalSendUserIds.add(userId);
            }
        }
        String imageUrl = USER.equals(noticeType) ? LICENSE_USER_IMAGE : LICENSE_STORE_IMAGE;
        jmsTaskService.sendLicenseMessage(enterpriseId, dingCorpId, appType, finalSendUserIds, title, content, imageUrl, noticeSettingId, noticeType);
    }
}
