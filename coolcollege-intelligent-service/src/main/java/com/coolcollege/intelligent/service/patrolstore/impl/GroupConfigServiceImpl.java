package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.WXMessageTypeEnum;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbWxGroupConfigDao;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbWxGroupConfigDetailDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.patrolstore.dto.SendWXGroupMessageDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.WXGroupMessageDTO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDetailDO;
import com.coolcollege.intelligent.model.patrolstore.request.AddGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.request.DeleteGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.request.UpdateGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.GroupConfigDetailVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.patrolstore.GroupConfigService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: huhu
 * @Date: 2024/9/6 11:37
 * @Description:
 */
@Slf4j
@Service
public class GroupConfigServiceImpl implements GroupConfigService {

    @Resource
    private TbWxGroupConfigDao tbWxGroupConfigDao;

    @Resource
    private TbWxGroupConfigDetailDao tbWxGroupConfigDetailDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private StoreDao storeDao;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private HttpRestTemplateService httpRestTemplateService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addGroupConfig(String enterpriseId, String userId, AddGroupConfigRequest param) {
        // 新增配置
        TbWxGroupConfigDO tbWxGroupConfigDO = param.convert(userId);
        tbWxGroupConfigDao.insert(tbWxGroupConfigDO, enterpriseId);
        // 新增配置明细
        List<TbWxGroupConfigDetailDO> list = param.convertListDetail(tbWxGroupConfigDO.getId(), userId);
        tbWxGroupConfigDetailDao.insertBatch(list, enterpriseId);
        return tbWxGroupConfigDO.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateGroupConfig(String enterpriseId, String userId, UpdateGroupConfigRequest param) {
        // 1.更新群组配置
        TbWxGroupConfigDO tbWxGroupConfigDO = param.convert(userId);
        int updateFlag = tbWxGroupConfigDao.update(tbWxGroupConfigDO, enterpriseId);
        if (updateFlag != 1) {
            return Boolean.FALSE;
        }
        // 2.更新群组明细
        List<String> userIds = param.getUserIds();
        List<TbWxGroupConfigDetailDO> oldList = tbWxGroupConfigDetailDao.getListByGroupId(param.getGroupId(), enterpriseId);
        // 2.1 删除对应明细数据
        List<Long> deleteIds = oldList.stream().filter(u -> !userIds.contains(u.getUserId())).map(TbWxGroupConfigDetailDO::getId).collect(Collectors.toList());
        tbWxGroupConfigDetailDao.removeByIds(deleteIds, userId, enterpriseId);
        // 2.2 更新明细数据
        tbWxGroupConfigDetailDao.updateByGroupId(param.getGroupId(), userId, param.getPushAddress(), enterpriseId);
        // 2.3 新增明细数据
        List<String> oldUserIds = oldList.stream().map(TbWxGroupConfigDetailDO::getUserId).collect(Collectors.toList());
        List<String> addUserIds = userIds.stream().filter(u -> !oldUserIds.contains(u)).collect(Collectors.toList());
        List<TbWxGroupConfigDetailDO> list = param.convertListDetail(tbWxGroupConfigDO.getId(), userId, addUserIds);
        tbWxGroupConfigDetailDao.insertBatch(list, enterpriseId);
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteGroupConfig(String enterpriseId, String userId, DeleteGroupConfigRequest param) {
        Boolean flag = tbWxGroupConfigDao.remove(enterpriseId, userId, param.getGroupId()) == 1;
        if (flag) {
            tbWxGroupConfigDetailDao.removeByGroupId(enterpriseId, userId, param.getGroupId());
        }
        return flag;
    }

    @Override
    public GroupConfigDetailVO getGroupConfigDetail(String enterpriseId, Long groupId) {
        TbWxGroupConfigDO tbWxGroupConfigDO = tbWxGroupConfigDao.getById(groupId, enterpriseId);
        if (Objects.isNull(tbWxGroupConfigDO)) {
            return new GroupConfigDetailVO();
        }
        List<TbWxGroupConfigDetailDO> detailList = tbWxGroupConfigDetailDao.getListByGroupId(groupId, enterpriseId);
        List<String> userIds = detailList.stream().map(TbWxGroupConfigDetailDO::getUserId).collect(Collectors.toList());
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
        return GroupConfigDetailVO.covert(tbWxGroupConfigDO, userNameMap, detailList);
    }

    @Override
    public PageInfo<GroupConfigDetailVO> getGroupConfigPage(String enterpriseId, PageBaseRequest param) {
        Page<TbWxGroupConfigDO> groupPage = tbWxGroupConfigDao.getGroupConfigList(enterpriseId, param);
        if (groupPage.size() == 0) {
            return new PageInfo<>();
        }
        // 查询群组对应明细
        List<Long> groupIds = groupPage.stream().map(TbWxGroupConfigDO::getId).collect(Collectors.toList());
        List<TbWxGroupConfigDetailDO> detailList = tbWxGroupConfigDetailDao.getListByGroupIds(groupIds, enterpriseId);

        // 查询所用用户名称
        List<String> userIds = detailList.stream().map(TbWxGroupConfigDetailDO::getUserId).collect(Collectors.toList());
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);

        Map<Long, List<TbWxGroupConfigDetailDO>> detailMap = detailList.stream().collect(Collectors.groupingBy(TbWxGroupConfigDetailDO::getGroupId));

        // 数据封装
        List<GroupConfigDetailVO> pageList = new ArrayList<>(groupPage.size());
        groupPage.forEach(g -> {
            List<TbWxGroupConfigDetailDO> groupConfigDetailList = detailMap.get(g.getId());

            GroupConfigDetailVO detailVO = GroupConfigDetailVO.covert(g, userNameMap, groupConfigDetailList);
            pageList.add(detailVO);
        });
        return new PageInfo<>(pageList);
    }

    @Override
    public void sendWXGroupMessage(SendWXGroupMessageDTO param) {
        if(Objects.isNull(param) || StringUtils.isAnyBlank(param.getEnterpriseId(), param.getSupervisorId()) || Objects.isNull(param.getMsgType())){
            return;
        }
        String enterpriseId = param.getEnterpriseId();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String supervisorId = param.getSupervisorId();
        List<TbWxGroupConfigDetailDO> groupList = tbWxGroupConfigDetailDao.getDetailByUserId(enterpriseId, supervisorId);
        if(CollectionUtils.isEmpty(groupList)){
            return;
        }
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(enterpriseId, supervisorId);
        String userName = Optional.ofNullable(enterpriseUser).map(o->o.getName()).orElse(null);
        WXGroupMessageDTO message = null;
        switch (param.getMsgType()){
            case IMAGE:
                JSONObject imgObj = new JSONObject();
                byte[] imageByte = FileUtil.getImageFromNetByUrl(param.getSignInImg());
                imageByte = FileUtil.compressImage(imageByte);
                String base64Binary = DatatypeConverter.printBase64Binary(imageByte);
                imgObj.put("base64", base64Binary);
                imgObj.put("md5", getMd5(imageByte));
                message = WXGroupMessageDTO.builder().msgtype(WXMessageTypeEnum.IMAGE.getType()).image(imgObj).build();
                break;
            case MARKDOWN:
                StoreDO storeInfo = storeDao.getByStoreId(enterpriseId, param.getStoreId());
                String storeName = Optional.ofNullable(storeInfo).map(o->o.getStoreName()).orElse(null);
                String storeNum = Optional.ofNullable(storeInfo).map(o->o.getStoreNum()).orElse(null);
                String signOutDate = DateUtil.format(param.getSignOutTime(), DatePattern.NORM_DATE_PATTERN);
                String signInTime = DateUtil.format(param.getSignInTime(), "HH:mm");
                String signOutTime = DateUtil.format(param.getSignOutTime(), "HH:mm");
                JSONObject contentObj = new JSONObject();
                String content = MessageFormat.format(WXGroupMessageDTO.MARKDOWN_CONTENT, signOutDate, userName, storeNum, storeName, signInTime, signOutTime, param.getSummary());
                contentObj.put("content", content);
                message = WXGroupMessageDTO.builder().msgtype(WXMessageTypeEnum.MARKDOWN.getType()).markdown(contentObj).build();
                break;
            default:
                return;
        }
        if(Objects.isNull(message)){
            return;
        }
        for (TbWxGroupConfigDetailDO tbWxGroupConfigDetail : groupList) {
            String pushAddress = tbWxGroupConfigDetail.getPushAddress();
            if(StringUtils.isBlank(pushAddress)){
                continue;
            }
            String result = httpRestTemplateService.postForObject(pushAddress, message, String.class);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(Objects.nonNull(jsonObject)){
                Integer errcode = jsonObject.getInteger("errcode");
                if(Objects.nonNull(errcode) && errcode != Constants.ZERO){
                    int tryCount = Constants.ONE;
                    boolean isTry = true;
                    while (tryCount < Constants.INDEX_THREE && isTry){
                        log.info("发送微信群消息重试：{}次",tryCount);
                        String tryResult = httpRestTemplateService.postForObject(pushAddress, message, String.class);
                        JSONObject tryJsonObject = JSONObject.parseObject(tryResult);
                        isTry = Objects.nonNull(tryJsonObject) && tryJsonObject.getInteger("errcode") != Constants.ZERO;
                        tryCount++;
                    }
                }
            }
        }
    }

    private static String getMd5(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to compute MD5", e);
        }
    }
}
