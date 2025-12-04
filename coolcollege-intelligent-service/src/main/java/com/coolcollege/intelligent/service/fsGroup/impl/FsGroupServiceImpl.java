package com.coolcollege.intelligent.service.fsGroup.impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.google.common.collect.Maps;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.fsGroup.FsGroupTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.StringUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.UserRegionMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.fsGroup.*;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.fsGroup.*;
import com.coolcollege.intelligent.model.fsGroup.query.*;
import com.coolcollege.intelligent.model.fsGroup.request.*;
import com.coolcollege.intelligent.model.fsGroup.vo.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.FsService;
import com.coolcollege.intelligent.service.fsGroup.FsGroupService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

;

/**
 * (FsGroup)表服务实现类
 *
 * @author CFJ
 * @since 2024-04-23 09:46:04
 */
@Service("fsGroupService")
@Slf4j
public class FsGroupServiceImpl implements FsGroupService {
    @Resource
    private FsGroupMapper fsGroupMapper;

    @Resource
    private FsService fsService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private HttpRestTemplateService httpRestTemplateService;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private UserRegionMappingMapper userRegionMappingMapper;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private FsGroupTopMenuMapper fsGroupTopMenuMapper;

    @Resource
    private FsGroupTopMenuMappingMapper fsGroupTopMenuMappingMapper;

    @Resource
    private FsGroupNoticeMapper fsGroupNoticeMapper;

    @Resource
    private FsGroupSceneMapper fsGroupSceneMapper;

    @Resource
    private FsGroupSceneMappingMapper fsGroupSceneMappingMapper;

    @Resource
    private FsGroupNoticeMappingMapper fsGroupNoticeMappingMapper;


    @Resource
    private FsGroupMenuMapper fsGroupMenuMapper;

    @Resource
    private FsGroupMenuMappingMapper fsGroupMenuMappingMapper;


    @Resource
    private FsGroupMappingMapper fsGroupMappingMapper;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;



    @Override
    public FsGroupDO queryById(String enterpriseId,Long id) {
        return fsGroupMapper.queryById(enterpriseId,id);
    }

    private final RateLimiter rateLimiter = RateLimiter.create(15);

    @Override
    public FsGroupDO insert(String enterpriseId,FsGroupDO fsGroupDO) {
        fsGroupMapper.insert(enterpriseId,fsGroupDO);
        return fsGroupDO;
    }

    @Override
    public FsGroupDO update(String enterpriseId,FsGroupDO fsGroupDO) {
        fsGroupMapper.update(enterpriseId,fsGroupDO);
        return queryById(enterpriseId,fsGroupDO.getId());
    }


    @Override
    public boolean deleteById(String enterpriseId,Long id) {
        return fsGroupMapper.deleteById(enterpriseId,id) > 0;
    }

    @Override
    @Async
    public void addFsGroup(String eid, FsGroupAddRequest request,CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<StoreWorkCommonDTO> regionIds = request.getRegionIds();
        if (CollectionUtils.isEmpty(regionIds)){
            log.error("群绑定部门区域不能为空");
            return;
        }
        List<String> regionIdList= Lists.newArrayList();
        List<String> storeIds = regionIds.stream().filter(c -> FsGroupTypeEnum.STORE.getCode().equals(c.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeIds)){
            List<RegionDO> regionIdByStoreIds = regionMapper.getRegionIdByStoreIds(eid, storeIds);
            regionIdList.addAll(regionIdByStoreIds.stream().map(RegionDO::getRegionId).map(String::valueOf).collect(Collectors.toList()));
        }
        List<String> regionIdPart = regionIds.stream().filter(c -> FsGroupTypeEnum.REGION.getCode().equals(c.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(regionIdPart)){
            regionIdList.addAll(regionIdPart);
        }
        if (CollectionUtils.isEmpty(regionIdList)){
            log.error("没有门店，无法创建群组");
            return;
        }
        //获取飞书token
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        //根据群类型选择创建群范围
        if (FsGroupTypeEnum.STORE.getCode().equals(request.getType())){
            //查出regionIds下的所有门店
            List<RegionDO> stores=regionMapper.getStoresByRegionIdList(eid, regionIdList);
            List<String> ids = stores.stream().map(c -> c.getId().toString()).collect(Collectors.toList());
            //根据id分组value为name
            Map<Long, String> idNameMap = stores.stream().collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));
            //查询所有区域下所有人员
            List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingMapper.listByUserIdsAndRegionIds(eid, null, ids);
            if (CollectionUtils.isEmpty(userRegionMappingDOS)){
                log.error("没有门店人员，无法创建群组");
                return;
            }
            //根据regionId分组
            Map<String, List<UserRegionMappingDO>> regionIdUserMap = userRegionMappingDOS.stream().collect(Collectors.groupingBy(UserRegionMappingDO::getRegionId));

            for (Map.Entry<String, List<UserRegionMappingDO>> entry : regionIdUserMap.entrySet()) {
                try {
                    List<UserRegionMappingDO> users = entry.getValue();
                    if (CollectionUtils.isEmpty(users)) {
                        log.info("区域下没有人员，无法创建群组:{}",entry.getKey());
                        continue;
                    }
                    if (users.size()>50){
                        users=users.stream().limit(50).collect(Collectors.toList());
                    }
                    List<String> userIds = users.stream().map(UserRegionMappingDO::getUserId).collect(Collectors.toList());
                    //查询当前门店下是否有店长角色
                    List<String> shopOwnerIds = enterpriseUserRoleMapper.selectUserIdByRoleId(eid, userIds, Role.SHOPOWNER.getId());
                    String shopOwnerId=null;
                    if (CollectionUtils.isEmpty(shopOwnerIds)){
                        shopOwnerId=userIds.get(0);
                    }else {
                        shopOwnerId=shopOwnerIds.get(0);
                    }
                    EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(eid, shopOwnerId);
                    request.setGroupOwnerId(shopOwnerId);
                    request.setGroupOwnerName(userDO.getName());
                    request.setName(idNameMap.get(Long.valueOf(entry.getKey())));
                    //为门店创建飞书群
                    String chatId = createFsGroup(eid, token,request,userIds,entry.getKey(), user);
                    log.info("创建门店群成功:{}",entry.getKey());
                    handleConfig(eid,token,request,chatId,user);
                }catch (Exception e){
                    log.error("创建门店群组失败:{}:{}",entry.getKey(),e);
                }
            }
            return;
        }
        //区域群或者其他群 只创建一次
        List<String> userIds = enterpriseUserDao.getUserIdsByRegionIdList(eid, regionIdList);
        if (CollectionUtils.isEmpty(userIds)){
            log.error("区域下没有人员，无法创建群组");
            return;
        }
        if (userIds.size()> 50){
            userIds = userIds.stream().limit(50).collect(Collectors.toList());
        }
        String chatId = createFsGroup(eid, token, request, userIds, null,user);
        handleConfig(eid, token, request, chatId, user);
        log.info("创建区域群组成功:{}",regionIds);
    }

    //处理创建群的配置项
    private void handleConfig(String eid,String token,FsGroupAddRequest request,String chatId,CurrentUser user){
        //判断是否有群应用配置项
        Date createTime = new Date();
        if (CollectionUtils.isNotEmpty(request.getSceneIds())){
            List<Long> sceneIds = fsGroupSceneMapper.queryByIds(eid, request.getSceneIds()).stream().map(c -> c.getId()).collect(Collectors.toList());
            //查当前已有配置项
            List<Long> curSceneIds=fsGroupSceneMappingMapper.getGroupSceneIdsConfig(eid,chatId);
            //取差集
            List<Long> difference = sceneIds.stream().filter(c -> !curSceneIds.contains(c)).collect(Collectors.toList());
            //添加映射
            List<FsGroupSceneMappingDO> dos = Lists.newArrayList();
            difference.stream().forEach(c->{
                FsGroupSceneMappingDO build = FsGroupSceneMappingDO.builder().chatId(chatId).sceneId(c).createTime(createTime).build();
                dos.add(build);
            });
            if (CollectionUtils.isNotEmpty(dos)){
                log.info("添加群配置项:{}配置ids:{}",chatId,request.getSceneIds());
                fsGroupSceneMappingMapper.insertOrUpdateBatch(eid,dos);
            }
        }
        //判断是否有置顶
        if (CollectionUtils.isNotEmpty(request.getTopMenuIds())){
            List<FsGroupTopMenuRequest> requests=Lists.newArrayList();
            //获取已有ids
            List<FsGroupTopMenuMappingDO> mappingDOS = fsGroupTopMenuMappingMapper.queryTopMenuIdsByChatId(eid, chatId);
            List<Long> curTopMenuIds = mappingDOS.stream().map(c -> c.getMenuId()).collect(Collectors.toList());
            //根据id查置顶信息
            List<FsGroupTopMenuDO> fsGroupTopMenuDOS = fsGroupTopMenuMapper.queryByIds(eid, request.getTopMenuIds());
            //取差集
            List<FsGroupTopMenuDO> dif = fsGroupTopMenuDOS.stream().filter(c -> !curTopMenuIds.contains(c.getId())).collect(Collectors.toList());
            log.info("群置顶差集:{}",dif.stream().map(c->c.getId()).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(dif)){
                //创建映射
                List<FsGroupTopMenuMappingDO> dos = Lists.newArrayList();
                dif.stream().forEach(c->{
                    FsGroupTopMenuRequest build = FsGroupTopMenuRequest.builder().topName(c.getTopName()).url(c.getUrl()).build();
                    requests.add(build);
                    FsGroupTopMenuMappingDO aDo = FsGroupTopMenuMappingDO.builder().chatId(chatId).menuId(c.getId()).createUser(user.getUserId()).createTime(createTime).build();
                    dos.add(aDo);
                });
                try {
                    Map<String, String> stringStringMap = addFSGroupTopMenu(token, requests, chatId);
                    Map<Long, String> menuIdAndTabId = dif.stream().collect(Collectors.toMap(c -> c.getId(), c -> stringStringMap.get(c.getTopName()), (a, b) -> a));
                    log.info("差集群置顶ids:{}",menuIdAndTabId.keySet());
                    dos.stream().forEach(c->{
                        c.setFsTabId(menuIdAndTabId.get(c.getMenuId()));
                    });
                    log.info("添加群置顶:{}:{}",chatId,request.getTopMenuIds());
                    fsGroupTopMenuMappingMapper.insertOrUpdateBatch(eid,dos);
                }catch (Exception e){
                    log.error("创建飞书群顶部菜单返回错误:{}",e);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(request.getMenuIds())){
            List<Long> curMenuIds = fsGroupMenuMappingMapper.queryMenuByChatId(eid, chatId);
            List<Long> menuIds = request.getMenuIds();
            if (menuIds.size()>3 || curMenuIds.size()>3){
                log.error("群菜单最多为三个:{}mappingSize:{}requestSize:{}",chatId, menuIds);
                return;
            }
            List<Long> difIds = menuIds.stream().filter(c -> !curMenuIds.contains(c)).collect(Collectors.toList());
            log.info("群菜单差集:{}",difIds);
            if (CollectionUtils.isEmpty(difIds)){
                log.info("没有新菜单创建:{}",menuIds);
                return;
            }
            List<FsGroupMenuDO> fsGroupMenuDOS = fsGroupMenuMapper.queryByIds(eid, difIds);
            Map<Long,String> menuIdAndLevelId= Maps.newHashMap();
            fsGroupMenuDOS.stream().forEach(c->{
                FsGroupMenuRequest build = FsGroupMenuRequest.builder().menuName(c.getMenuName()).url(c.getUrl()).build();
                rateLimiter.acquire();
                String msgId=null;
                try {
                    msgId = addFSGroupMenu(token, build, chatId);
                }catch (Exception e){
                    log.info("创建群菜单失败:{}:{}",chatId,c.getId());
                }
                if (StringUtils.isNotBlank(msgId)){
                    menuIdAndLevelId.put(c.getId(),msgId);
                }
            });
            List<FsGroupMenuMappingDO> dos = Lists.newArrayList();
            menuIdAndLevelId.entrySet().stream().forEach(c->{
                FsGroupMenuMappingDO build = FsGroupMenuMappingDO.builder().menuId(c.getKey()).chatId(chatId).levelId(c.getValue()).createUser(user.getUserId()).createTime(createTime).build();
                dos.add(build);
            });
            if (CollectionUtils.isEmpty(dos)){
                return;
            }
            log.info("添加群菜单:{}:{}",chatId,menuIds);
            fsGroupMenuMappingMapper.insertOrUpdateBatch(eid,dos);
        }
    }

    @Override
    @Async
    public void addGroupTopMsg(String eid, FsGroupTopMenuRequest request, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //获取需要发送的群
        List<String> chatIds = getChatIdsByRegionIds(eid, request.getSendStoreRegionIds(), request.getSendRegionChatIds(), request.getSendOtherChatIds());
        if (CollectionUtils.isEmpty(chatIds)){
            log.info("没有需要发送的群组");
            return;
        }
        //构造消息内容
        String content=	"{\"zh_cn\":{\"title\":\"置顶名称\",\"content\":[[{\"tag\":\"a\",\"href\":\"http://www.feishu.cn\",\"text\":\"链接名字\"}]]}}";
        // 进行替换
        String newContent = content
                .replace("\"title\":\"置顶名称\"", "\"title\":\"" + request.getTopName() + "\"")
                .replace("\"href\":\"http://www.feishu.cn\"", "\"href\":\"" + request.getUrl() + "\"")
                .replace("\"text\":\"链接名字\"", "\"text\":\"" + request.getUrl() + "\"");
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        //获取飞书token
        for (String chatId : chatIds) {
            try {
                //首先发送消息，然后置顶
                String msgId = sendFsMsg(token, "chat_id", chatId, "post", newContent);
                if (StringUtils.isBlank(msgId)) {
                    continue;
                }
                log.info("发送消息成功:{}",msgId);
                //置顶消息
                String msg = putTopMsg(token, chatId, msgId);
                if (StringUtils.isNotBlank(msg)) {
                    log.info("置顶消息成功:{}", msg);
                }
            }catch (Exception e){
                log.error("置顶飞书群消息出错:{}",chatId,e);
            }
        }
        FsGroupTopMenuDO fsGroupTopMenuDO = new FsGroupTopMenuDO();
        fsGroupTopMenuDO.setCreateTime(new Date());
        fsGroupTopMenuDO.setCreateUser(user.getUserId());
        if (CollectionUtils.isNotEmpty(request.getSendStoreRegionIds())){
            List<StoreWorkCommonDTO> sendStoreRegionIds = request.getSendStoreRegionIds();
            fsGroupTopMenuDO.setSendStoreRegionIds(JSONObject.toJSONString(sendStoreRegionIds));
        }
        BeanUtil.copyProperties(request,fsGroupTopMenuDO);
        int insert = fsGroupTopMenuMapper.insert(eid, fsGroupTopMenuDO);
        log.info("飞书置顶卡片发送完毕");
    }

    @Override
    @Async
    public void addGroupNotice(String eid, FsGroupNoticeRequest request, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        if ("2".equals(request.getSendTimeType())){
            //定时发送
            log.info("定时发送公告");
            FsGroupNoticeDO fsGroupNoticeDO = new FsGroupNoticeDO();
            BeanUtil.copyProperties(request,fsGroupNoticeDO);
            fsGroupNoticeDO.setHasSend("0");
            fsGroupNoticeDO.setCreateTime(new Date());
            fsGroupNoticeDO.setCreateUserId(user.getUserId());
            if (CollectionUtils.isNotEmpty(request.getSendStoreRegionIds())){
                fsGroupNoticeDO.setSendStoreRegionIds(JSONObject.toJSONString(request.getSendStoreRegionIds()));
            }
            fsGroupNoticeMapper.insert(eid,fsGroupNoticeDO);
            if ("1".equals(fsGroupNoticeDO.getDetailType())){
                String param = null;
                try {
                    param = URLEncoder.encode("announcement&id="+fsGroupNoticeDO.getId(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                String url="https://applink.feishu.cn/client/web_app/open?appId=cli_a3e8a62169a1500e&path=/notice&target="+param;
                fsGroupNoticeDO.setDetailUrl(url);
            }
            fsGroupNoticeMapper.update(eid,fsGroupNoticeDO);
            return;
        }
        List<String> chatIds = getChatIdsByRegionIds(eid, request.getSendStoreRegionIds(), request.getSendRegionChatIds(), request.getSendOtherChatIds());
        if (CollectionUtils.isEmpty(chatIds)){
            log.info("没有需要发送的群组");
            return;
        }

        FsGroupNoticeDO fsGroupNoticeDO = new FsGroupNoticeDO();
        BeanUtil.copyProperties(request,fsGroupNoticeDO);
        fsGroupNoticeDO.setHasSend("1");
        fsGroupNoticeDO.setSendTime(new Date());
        fsGroupNoticeDO.setCreateTime(new Date());
        fsGroupNoticeDO.setCreateUserId(user.getUserId());
        if (CollectionUtils.isNotEmpty(request.getSendStoreRegionIds())){
            fsGroupNoticeDO.setSendStoreRegionIds(JSONObject.toJSONString(request.getSendStoreRegionIds()));
        }
        int insert = fsGroupNoticeMapper.insert(eid, fsGroupNoticeDO);
        if ("1".equals(fsGroupNoticeDO.getDetailType())){
            String param = null;
            try {
                param = URLEncoder.encode("announcement&id="+fsGroupNoticeDO.getId(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String url="https://applink.feishu.cn/client/web_app/open?appId=cli_a3e8a62169a1500e&path=/notice&target="+param;
            fsGroupNoticeDO.setDetailUrl(url);
        }
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        HashMap<String, String> chatIdAndMsg = Maps.newHashMap();
        for (String chatId : chatIds) {
            try {
                String noticeContent = getNoticeContent(request.getName(),request.getContent(),request.getImg(),request.getDetailUrl());
                String msgId= sendFsMsg(token, "chat_id", chatId, "interactive", noticeContent);
                if (StringUtils.isNotEmpty(msgId)){
                    chatIdAndMsg.put(chatId,msgId);
                }
            }catch (Exception e ){
                log.info("飞书公告发送失败:{}",chatId);
            }
        }
        if (CollectionUtils.isEmpty(chatIdAndMsg.keySet())){
            log.info("没有需要发送的群组");
            return;
        }
        List<String> sendChatIds = chatIdAndMsg.keySet().stream().collect(Collectors.toList());
        List<FsGroupMappingDO> fsGroupMappingDOS = fsGroupMappingMapper.queryByChatIds(eid, sendChatIds);
        fsGroupNoticeDO.setSendUserCount(fsGroupMappingDOS.size());
        int update = fsGroupNoticeMapper.update(eid, fsGroupNoticeDO);
        List<FsGroupNoticeMappingDO> dos = Lists.newArrayList();
        chatIdAndMsg.entrySet().stream().forEach(c->{
            FsGroupNoticeMappingDO build = FsGroupNoticeMappingDO.builder().noticeId(fsGroupNoticeDO.getId()).chatId(c.getKey()).msgId(c.getValue()).createTime(new Date()).build();
            dos.add(build);
        });
        fsGroupNoticeMappingMapper.insertBatch(eid,dos);
        log.info("飞书群公告发送完毕");
    }



    @Override
    public void searchChatNotice(String eid)  {
        log.info("定时扫描未发送群公告：{}", eid);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
        if(Objects.isNull(enterpriseConfig)){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        //当前时间前10分钟 和 后10分钟
        String beginTime = LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().minusMinutes(-10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int pageNum = Constants.INDEX_ONE, pageSize = Constants.MSG_SIZE;
        boolean hasNext = true;
        while (hasNext){
            PageHelper.startPage(pageNum, pageSize);
            List<FsGroupNoticeDO> page=fsGroupNoticeMapper.queryTimedNotice(eid,beginTime,endTime);
            if (CollectionUtils.isEmpty(page) || page.size() < pageSize) {
                hasNext = false;
            }
            sendChatNotice(eid,page,enterpriseConfig);
        }

    }

    @Override
    public FsGroupVO getFsGroupDetail(String eid, Long id) {
        FsGroupDO fsGroupDO = fsGroupMapper.queryById(eid, id);
        if (Objects.isNull(fsGroupDO)){
            throw new ServiceException("没有这个群");
        }
        FsGroupVO vo = new FsGroupVO();
        BeanUtil.copyProperties(fsGroupDO,vo);
        String bindRegionIds = vo.getBindRegionIds();

        if (StringUtils.isNotBlank(bindRegionIds)){
            List<String> regionIds = Lists.newArrayList(StringUtils.split(bindRegionIds, ","));
            List<RegionDO> regionDOS = regionMapper.getRegionByRegionIds(eid,regionIds);
            Map<String, String> idAndName = regionDOS.stream().collect(Collectors.toMap(c -> c.getId().toString(), c -> c.getName(), (a, b) -> a));
            vo.setBindRegionNames(idAndName);
        }
        List<IdAndNameVO> sceneList = fsGroupSceneMappingMapper.queryIdAndNameByChatId(eid, vo.getChatId());
        vo.setSceneList(sceneList);
        List<IdAndNameVO> topMenuList = fsGroupTopMenuMappingMapper.queryIdAndNameByChatId(eid, vo.getChatId());
        vo.setTopMenuList(topMenuList);
        List<IdAndNameVO> menuList = fsGroupMenuMappingMapper.queryIdAndNameByChatId(eid, vo.getChatId());
        vo.setMenuList(menuList);

        List<FsGroupMappingDO> fsGroupMappingDOS = fsGroupMappingMapper.queryByChatId(eid, vo.getChatId());
        vo.setUserCount(fsGroupMappingDOS.size());
        return vo;
    }

    @Override
    public FsGroupNoticeVO getGroupNoticeDetail(String eid, Long noticeId) {
        FsGroupNoticeDO fsGroupNoticeDO = fsGroupNoticeMapper.queryById(eid, noticeId);
        if (Objects.isNull(fsGroupNoticeDO)){
            throw new ServiceException("没有这个公告");
        }
        FsGroupNoticeVO vo = new FsGroupNoticeVO();
        BeanUtil.copyProperties(fsGroupNoticeDO,vo);

        List<IdAndNameVO> regionIdAndName = getRegionIdAndName(eid, fsGroupNoticeDO.getSendStoreRegionIds());
        List<IdAndNameVO> regionChatList = getGroupIdAndName(eid, fsGroupNoticeDO.getSendRegionChatIds());
        List<IdAndNameVO> otherChatList = getGroupIdAndName(eid, fsGroupNoticeDO.getSendOtherChatIds());

        vo.setSendStoreRegionList(regionIdAndName);
        vo.setSendRegionChatList(regionChatList);
        vo.setSendOtherChatList(otherChatList);
        return vo;
    }

    private List<IdAndNameVO> getGroupIdAndName(String eid,String groupIdsStr){
        List<IdAndNameVO> vos = Lists.newArrayList();
        if (StringUtils.isNotEmpty(groupIdsStr)){
            List<String> groupIds = Lists.newArrayList(groupIdsStr.split(","));
            if (CollectionUtils.isNotEmpty(groupIds)){
                List<FsGroupDO> fsGroupDOS = fsGroupMapper.queryByIds(eid, groupIds);
                fsGroupDOS.stream().forEach(c->{
                    IdAndNameVO build = IdAndNameVO.builder().id(c.getId().toString()).name(c.getName()).build();
                    vos.add(build);
                });
            }
        }
        return vos;
    }

    private List<IdAndNameVO> getRegionIdAndName(String eid,String regionIdsStr){
        List<IdAndNameVO> regionVOs = Lists.newArrayList();
        if (StringUtils.isNotBlank(regionIdsStr)){
            List<String> regionIds = Lists.newArrayList(regionIdsStr.split(","));
            if (CollectionUtils.isNotEmpty(regionIds)){
                List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(eid, regionIds);
                regionByRegionIds.stream().forEach(c->{
                    IdAndNameVO build = IdAndNameVO.builder().id(c.getId().toString()).name(c.getName()).build();
                    regionVOs.add(build);
                });
            }
        }
        return regionVOs;
    }

    @Override
    public FsGroupTopMenuVO getGroupTopMenuDetail(String eid, Long topMenuId) {
        FsGroupTopMenuDO fsGroupTopMenuDO = fsGroupTopMenuMapper.queryById(eid, topMenuId);
        if (Objects.isNull(fsGroupTopMenuDO)){
            throw new ServiceException("没有这个置顶");
        }
        FsGroupTopMenuVO vo = new FsGroupTopMenuVO();
        BeanUtil.copyProperties(fsGroupTopMenuDO,vo);

        List<IdAndNameVO> regionIdAndName = getRegionIdAndName(eid, fsGroupTopMenuDO.getSendStoreRegionIds());
        List<IdAndNameVO> regionChatList = getGroupIdAndName(eid, fsGroupTopMenuDO.getSendRegionChatIds());
        List<IdAndNameVO> otherChatList = getGroupIdAndName(eid, fsGroupTopMenuDO.getSendOtherChatIds());

        vo.setSendStoreRegionList(regionIdAndName);
        vo.setSendRegionChatList(regionChatList);
        vo.setSendOtherChatList(otherChatList);

        return vo;
    }

    @Override
    public FsGroupMenuVO getGroupMenuDetail(String eid, Long menuId) {
        FsGroupMenuDO fsGroupMenuDO = fsGroupMenuMapper.queryById(eid, menuId);
        if (Objects.isNull(fsGroupMenuDO)){
            throw new ServiceException("没有这个菜单");
        }
        FsGroupMenuVO vo = new FsGroupMenuVO();
        BeanUtil.copyProperties(fsGroupMenuDO,vo);


        List<IdAndNameVO> regionIdAndName = getRegionIdAndName(eid, fsGroupMenuDO.getSendStoreRegionIds());
        List<IdAndNameVO> regionChatList = getGroupIdAndName(eid, fsGroupMenuDO.getSendRegionChatIds());
        List<IdAndNameVO> otherChatList = getGroupIdAndName(eid, fsGroupMenuDO.getSendOtherChatIds());

        vo.setSendStoreRegionList(regionIdAndName);
        vo.setSendRegionChatList(regionChatList);
        vo.setSendOtherChatList(otherChatList);

        return vo;
    }

    @Override
    public String uploadFsImg(String eid, MultipartFile img) {
        try {
            String token = fsService.getAccessToken(UserHolder.getUser().getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
            Map<String, String> header = Maps.newHashMap();
            header.put("Authorization", "Bearer " + token);
            header.put("Content-Type", "multipart/form-data; boundary=---7MA4YWxkTrZu0gW");
            String url="https://open.feishu.cn/open-apis/im/v1/images";

            MultiValueMap<String, Object> req = new LinkedMultiValueMap<>();
            req.add("image_type","message");
            req.add("image",img.getBytes());
            JSONObject resp = httpRestTemplateService.postForObject(url, req, JSONObject.class, header);
            if (resp.getIntValue("code")!=0){
                log.error("上传飞书图片失败:{}",resp.getString("msg"));
                throw new ServiceException("上传飞书图片失败");
            }
            return resp.getJSONObject("data").getString("image_key");
        }catch (Exception e){
            log.error("上传飞书图片失败:{}",e);
            throw new ServiceException("上传飞书图片失败");
        }
    }

    @Override
    public String getFsToken(String eid) {
        String token = fsService.getAccessToken(UserHolder.getUser().getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        return token;
    }

    @Override
    public void updateGroup(String eid, FsGroupAddRequest request, CurrentUser user) {
        FsGroupDO fsGroupDO = fsGroupMapper.queryById(eid, request.getId());
        if (Objects.isNull(fsGroupDO)){
            log.info("没有这个群:{}",request.getId());
            return;
        }
        String token = fsService.getAccessToken(UserHolder.getUser().getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        handleConfig(eid,token,request,fsGroupDO.getChatId(),user);
    }

    @Override
    public byte[] downloadFsImg(String eid, String imgKey) {
        try {
            String token = fsService.getAccessToken(UserHolder.getUser().getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
            Map<String, String> header = Maps.newHashMap();
            header.put("Authorization", "Bearer " + token);
            String url="https://open.feishu.cn/open-apis/im/v1/images/"+imgKey;
            byte[] forObject = httpRestTemplateService.getForObject(url, byte[].class, null, header);
            return forObject;
        }catch (Exception e){
            log.error("下载飞书图片失败:{}",e);
            throw new ServiceException("下载飞书图片失败");
        }
    }

    @Override
    public Boolean deleteSceneGroup(String eid, List<Long> ids) {
        return fsGroupSceneMappingMapper.deleteByIds(eid,ids)>0;
    }

    @Override
    public Boolean addSceneForGroups(String eid, SceneGroupIdRequest request) {
        if (CollectionUtils.isEmpty(request.getSceneIds()) || CollectionUtils.isEmpty(request.getChatIds())){
            throw new ServiceException("群应用或群不能为空");
        }
        List<FsGroupSceneMappingDO> dos = Lists.newArrayList();
        Date createTime = new Date();
        request.getSceneIds().forEach(sceneId->{
            request.getChatIds().forEach(chatId->{
                FsGroupSceneMappingDO fsGroupSceneMappingDO = new FsGroupSceneMappingDO();
                fsGroupSceneMappingDO.setSceneId(sceneId);
                fsGroupSceneMappingDO.setChatId(chatId);
                fsGroupSceneMappingDO.setCreateTime(createTime);
                dos.add(fsGroupSceneMappingDO);
            });
            }
        );
        ListUtils.partition(dos,200).forEach(c->fsGroupSceneMappingMapper.insertOrUpdateBatch(eid,c));
        return Boolean.TRUE;
    }

    //查询群公告已读消息
    @Override
    public void queryChatNoticeReadNum(String eid) {
        log.info("扫描群公告阅读人数：{}", eid);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
        if(Objects.isNull(enterpriseConfig)){
            log.error("没有这个企业:{}",eid);
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        //拿去所有七天内的群公告消息id
        List<FsGroupNoticeMappingDO> msgDos = fsGroupNoticeMappingMapper.selectNeedQueryMsgId(eid);

        if (CollectionUtils.isEmpty(msgDos)){
            log.info("没有公告需要查询");
            return;
        }
        //获取token
        String token = fsService.getAccessToken(enterpriseConfig.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        //根据noticeId分组
        Map<Long, List<FsGroupNoticeMappingDO>> noticeIdMap = msgDos.stream().collect(Collectors.groupingBy(FsGroupNoticeMappingDO::getNoticeId));
        noticeIdMap.entrySet().stream().forEach(entry->{
            Long noticeId = entry.getKey();
            List<FsGroupNoticeMappingDO> noticeMappingDos = entry.getValue();
            List<String> msgIds = noticeMappingDos.stream().map(item -> item.getMsgId()).collect(Collectors.toList());
            Integer readCount = 0;
            //循环查询消息已读接口
            for (String msgId : msgIds) {
                try {
                    rateLimiter.acquire();
                    Integer hasReadCount = queryChatNoticeReadNum(msgId, token);
                    if (hasReadCount!=null){
                        readCount+=hasReadCount;
                    }
                }catch (Exception e){
                    log.error("查询失败消息已读失败:{}",msgId,e);
                }
            }
            FsGroupNoticeDO build = FsGroupNoticeDO.builder().id(noticeId).hasReadCount(readCount).build();
            int update = fsGroupNoticeMapper.update(eid, build);
            log.info("更新群公告已读人数:{}:{}",noticeId,readCount);
        });
    }

    //查询消息已读方法
    private Integer queryChatNoticeReadNum(String msgId,String token){
        String url="https://open.feishu.cn/open-apis/im/v1/messages/"+msgId+"/read_users";

        Map<String, String> header = Maps.newHashMap();
        header.put("Authorization", "Bearer " + token);

        Map<String, Object> params = Maps.newHashMap();
        params.put("user_id_type", "open_id");
        params.put("page_size", 100);
        JSONObject resp = httpRestTemplateService.getForObject(url, JSONObject.class, params, header);
        if (resp.getIntValue("code")!=0){
            log.error("查询群公告阅读人数失败:{}",resp.getString("msg"));
            return null;
        }
        JSONArray data = resp.getJSONObject("data").getJSONArray("items");
        return data.size();
    }

    private void sendChatNotice(String eid,List<FsGroupNoticeDO> noticeDOs,EnterpriseConfigDO configDO){
        for (FsGroupNoticeDO noticeDO : noticeDOs) {
            String sendStoreRegionIds = noticeDO.getSendStoreRegionIds();
            List<StoreWorkCommonDTO> storeWorkCommonDTOS=Lists.newArrayList();
            if (StringUtils.isNotBlank(sendStoreRegionIds)){
                storeWorkCommonDTOS = JSONObject.parseArray(sendStoreRegionIds, StoreWorkCommonDTO.class);
            }
            List<String> chatIds = getChatIdsByRegionIds(eid,storeWorkCommonDTOS, noticeDO.getSendRegionChatIds(), noticeDO.getSendOtherChatIds());
            if (CollectionUtils.isEmpty(chatIds)){
                log.info("没有需要发送的群组");
                return;
            }
            String token = fsService.getAccessToken(configDO.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
            HashMap<String, String> chatIdAndMsg = Maps.newHashMap();
            for (String chatId : chatIds) {
                try {
                    //公告详情类型1:自定义内容 2:链接地址
                    String noticeContent = getNoticeContent(noticeDO.getName(),noticeDO.getContent(),noticeDO.getImg(),noticeDO.getDetailUrl());
                    String msgId = sendFsMsg(token, "chat_id", chatId, "interactive", noticeContent);
                    if (StringUtils.isNotEmpty(msgId)){
                        chatIdAndMsg.put(chatId,msgId);
                    }
                }catch (Exception e){
                    log.info("飞书公告发送失败:{}",chatId);
                }
            }
            List<String> sendChatIds = chatIdAndMsg.keySet().stream().collect(Collectors.toList());
            List<FsGroupMappingDO> fsGroupMappingDOS = fsGroupMappingMapper.queryByChatIds(eid, sendChatIds);
            noticeDO.setSendUserCount(fsGroupMappingDOS.size());
            noticeDO.setHasSend("1");
            fsGroupNoticeMapper.update(eid, noticeDO);
            List<FsGroupNoticeMappingDO> dos = Lists.newArrayList();
            chatIdAndMsg.entrySet().stream().forEach(c->{
                FsGroupNoticeMappingDO build = FsGroupNoticeMappingDO.builder().noticeId(noticeDO.getId()).chatId(c.getKey()).msgId(c.getValue()).createTime(new Date()).build();
                dos.add(build);
            });
            ListUtils.partition(dos,200).stream().forEach(c->fsGroupNoticeMappingMapper.insertBatch(eid,dos));
            log.info("飞书定时群公告发送完毕");
        }

    }


    @Override
    @Async
    public void addGroupTopMenu(String eid, FsGroupTopMenuRequest request, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<String> chatIds = getChatIdsByRegionIds(eid, request.getSendStoreRegionIds(), request.getSendRegionChatIds(), request.getSendOtherChatIds());
        if (CollectionUtils.isEmpty(chatIds)){
            log.info("没有需要发送的群组");
            return;
        }
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());

        Map<String, Map<String, String>> chatIdAndTabId = Maps.newHashMap();
        List<FsGroupTopMenuMappingDO> dos = Lists.newArrayList();
        for (String chatId : chatIds) {
            try {
                List<FsGroupTopMenuRequest> lists= Lists.newArrayList();
                lists.add(FsGroupTopMenuRequest.builder().url(request.getUrl()).topName(request.getTopName()).build());
                chatIdAndTabId.put(chatId,addFSGroupTopMenu(token, lists, chatId));
                FsGroupTopMenuMappingDO mappingDO = FsGroupTopMenuMappingDO.builder()
                        .chatId(chatId)
                        .build();
                dos.add(mappingDO);
            }catch (Exception e){
                log.error("添加飞书群顶部菜单出错:{}",chatId,e);
            }
        }
        FsGroupTopMenuDO menuDO = FsGroupTopMenuDO.builder().build();
        BeanUtil.copyProperties(request,menuDO);
        menuDO.setCreateTime(new Date());
        menuDO.setCreateUser(user.getUserId());
        if (CollectionUtils.isNotEmpty(request.getSendStoreRegionIds())){
            menuDO.setSendStoreRegionIds(JSONObject.toJSONString(request.getSendStoreRegionIds()));
        }
        fsGroupTopMenuMapper.insert(eid, menuDO);

        dos.stream().forEach(c->{
            String tabId = chatIdAndTabId.get(c.getChatId()).values().stream().collect(Collectors.toList()).get(0);
            c.setMenuId(menuDO.getId());
            c.setFsTabId(tabId);
            c.setCreateUser(user.getUserId());
            c.setCreateTime(new Date());
        });
        ListUtils.partition(dos,100).forEach(c->fsGroupTopMenuMappingMapper.insertBatch(eid,c));
        log.info("飞书群顶部菜单发送完毕");
    }

    @Override
    public PageInfo<FsGroupVO> getFsGroupList(String eid, FsGroupQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FsGroupDO> fsGroupDOS = fsGroupMapper.selectByQuery(eid, query);
        if (CollectionUtils.isEmpty(fsGroupDOS)){
            log.info("没有群");
            return new PageInfo<>(Lists.newArrayList());
        }
        List<String> collect = fsGroupDOS.stream().map(c -> c.getBindRegionIds()).collect(Collectors.toList());
        List<String> regionIds = Lists.newArrayList();
        collect.stream().forEach(c->{
            if (StringUtils.isNotBlank(c)){
                regionIds.addAll(Lists.newArrayList(StringUtils.split(c,",")));
            }
        });
        List<RegionDO> regionDOS = regionMapper.getRegionByRegionIds(eid,regionIds);
        Map<String, String> idAndName = regionDOS.stream().collect(Collectors.toMap(c -> c.getId().toString(), c -> c.getName(), (a, b) -> a));
        List<FsGroupVO> fsGroupVOS = Lists.newArrayList();

        List<String> chatIds = fsGroupDOS.stream().map(c -> c.getChatId()).collect(Collectors.toList());
        List<FsGroupMappingDO> fsGroupMappingDOS = fsGroupMappingMapper.queryByChatIds(eid, chatIds);
        //通过chatId分组,value为size数量
        Map<String, List<FsGroupMappingDO>> chatIdGroupMap = fsGroupMappingDOS.stream().collect(Collectors.groupingBy(FsGroupMappingDO::getChatId));
        fsGroupDOS.stream().forEach(c->{
            FsGroupVO vo = new FsGroupVO();
            BeanUtil.copyProperties(c,vo);
            String bindRegionIds = vo.getBindRegionIds();
            if (StringUtils.isNotBlank(bindRegionIds)){
                List<String> bindRegionIdList = Lists.newArrayList(StringUtils.split(bindRegionIds,","));
                Map<String, String> map = bindRegionIdList.stream().collect(Collectors.toMap(a -> a, a -> idAndName.get(a)));
                vo.setBindRegionNames(map);
            }
            vo.setUserCount(chatIdGroupMap.get(c.getChatId()).size());
            fsGroupVOS.add(vo);
        });
        return new PageInfo<>(fsGroupVOS);

    }

    @Override
    public PageInfo<FsGroupSceneVO> getFsGroupSceneList(String eid, FsGroupSceneQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FsGroupSceneVO> fsGroupSceneVOS = fsGroupSceneMapper.selectByQuery(eid, query);
        return new PageInfo<>(fsGroupSceneVOS);
    }

    @Override
    public PageInfo<FsGroupVO> getFsGroupByScene(String eid, FsGroupSceneMappingQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FsGroupVO> fsGroupSceneDOS = fsGroupSceneMappingMapper.selectGroupByQuery(eid, query);
        return new PageInfo<>(fsGroupSceneDOS);
    }

    @Override
    public PageInfo<FsGroupNoticeVO> getFsGroupNoticeList(String eid, FsGroupNoticeQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FsGroupNoticeVO> vos = fsGroupNoticeMapper.getFsGroupNoticeList(eid,query);
        List<String> ids = vos.stream().map(FsGroupNoticeVO::getCreateUserId).distinct().collect(Collectors.toList());
        Map<String, String> idNameMap = enterpriseUserDao.getUserNameMap(eid, ids);
        vos.stream().forEach(c->c.setCreateUserName(idNameMap.get(c.getCreateUserId())));
        return new PageInfo<>(vos);
    }

    @Override
    public PageInfo<FsGroupTopMenuVO> getFsGroupTopMenuList(String eid, FsGroupTopMenuQuery query) {
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<FsGroupTopMenuVO> vos=fsGroupTopMenuMapper.getFsGroupTopMenuList(eid,query);
        List<String> ids = vos.stream().map(FsGroupTopMenuVO::getCreateUser).distinct().collect(Collectors.toList());
        Map<String, String> idNameMap = enterpriseUserDao.getUserNameMap(eid, ids);
        vos.stream().forEach(c->c.setCreateUserName(idNameMap.get(c.getCreateUser())));
        return new PageInfo<>(vos);
    }

    @Override
    @Async
    public void addGroupMenu(String eid, FsGroupMenuRequest request, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<String> chatIds = getChatIdsByRegionIds(eid, request.getSendStoreRegionIds(), request.getSendRegionChatIds(), request.getSendOtherChatIds());
        if (CollectionUtils.isEmpty(chatIds)){
            log.info("没有需要发送的群组");
            throw new RuntimeException("没有需要发送的群组");
        }
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        List<FsGroupMenuMappingDO> dos= Lists.newArrayList();
        for (String chatId : chatIds) {
            try {
                List<FsGroupMenuMappingDO> mappingDOS = fsGroupMenuMappingMapper.queryByChatId(eid, chatId);
                if (CollectionUtils.isNotEmpty(mappingDOS)&& mappingDOS.size()>3){
                    log.info("群菜单数量已达到限制3个:{}",chatId);
                    continue;
                }
                rateLimiter.acquire();
                String levelId = addFSGroupMenu(token, request, chatId);
                if (StringUtils.isNotBlank(levelId)){
                    FsGroupMenuMappingDO mappingDO = FsGroupMenuMappingDO.builder()
                            .chatId(chatId)
                            .levelId(levelId)
                            .createTime(new Date())
                            .createUser(user.getUserId())
                            .build();
                    dos.add(mappingDO);
                }
            }catch (Exception e){
                log.error("添加飞书群菜单出错:{}",chatId,e);
            }
        }
        FsGroupMenuDO menuDO = FsGroupMenuDO.builder().build();
        BeanUtil.copyProperties(request,menuDO);
        menuDO.setCreateTime(new Date());
        menuDO.setCreateUser(user.getUserId());
        if (CollectionUtils.isNotEmpty(request.getSendStoreRegionIds())){
            menuDO.setSendStoreRegionIds(JSONObject.toJSONString(request.getSendStoreRegionIds()));
        }
        fsGroupMenuMapper.insert(eid, menuDO);
        dos.stream().forEach(c->c.setMenuId(menuDO.getId()));
        ListUtils.partition(dos,100).stream().forEach(c->fsGroupMenuMappingMapper.insertBatch(eid,c));
    }


    public String addFSGroupMenu(String token,FsGroupMenuRequest request,String chatId){
        //请求头
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //拼装参数
        String temp="{\"menu_tree\":{\"chat_menu_top_levels\":[{\"chat_menu_item\":{\"action_type\":\"REDIRECT_LINK\",\"redirect_link\":{\"common_url\":\"${url}\"},\"name\":\"${name}\"}}]}}";
        HashMap<String, String> map = Maps.newHashMap();
        map.put("name",request.getMenuName());
        map.put("url",request.getUrl());
        JSONObject req = JSONObject.parseObject(StringUtil.formatFsCard(temp, map));
        String url = "https://open.feishu.cn/open-apis/im/v1/chats/"+chatId+"/menu_tree";

        log.info("请求飞书添加群菜单:{}",req);
        JSONObject resp = httpRestTemplateService.postForObject(url, req, JSONObject.class, header);
        log.info("飞书添加群菜单返回:{}",resp);
        if(!("0".equals(resp.getString("code")))) {
            log.error(resp.toJSONString());
            return null;
        }
        JSONArray jsonArray = resp.getJSONObject("data").getJSONObject("menu_tree").getJSONArray("chat_menu_top_levels");
        //获取最后面的
        String levelId = jsonArray.getJSONObject(jsonArray.size()-1).getString("chat_menu_top_level_id");
        return levelId;
    }

    private List<String> getChatIdsByRegionIds(String eid,List<StoreWorkCommonDTO> sendStoreRegionIds,String regionChatIds,String otherChatIds){
        List<String> storeRegionIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(sendStoreRegionIds)){
            List<String> storeIds = sendStoreRegionIds.stream().filter(c -> FsGroupTypeEnum.STORE.getCode().equals(c.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storeIds)){
                List<RegionDO> regionDOS = regionMapper.listRegionByStoreIds(eid, storeIds);
                storeRegionIds.addAll(regionDOS.stream().map(c -> c.getRegionId()).collect(Collectors.toList()));
            }
            List<String> regionIdList = sendStoreRegionIds.stream().filter(c -> FsGroupTypeEnum.REGION.getCode().equals(c.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
            storeRegionIds.addAll(regionIdList);
        }
        //根据区域id查询所属区域下的所有门店群
        List<String> regionIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(storeRegionIds)){
            List<RegionDO> stores = regionMapper.getStoresByRegionIdList(eid, storeRegionIds);
            List<String> collect = stores.stream().map(c -> c.getId().toString()).collect(Collectors.toList());
            regionIds.addAll(collect);
        }
        //根据regionId查询是否有对应的群
        List<String> allChatIds = fsGroupMapper.selectChatIdsByRegionId(eid, regionIds);
        if (StringUtils.isNotBlank(regionChatIds)){
            List<String> ids = Arrays.stream(StringUtils.split(otherChatIds, ",")).collect(Collectors.toList());
            List<String> chatIds =fsGroupMapper.queryChatIdByIds(eid,ids);
            allChatIds.addAll(chatIds);
        }else {
            FsGroupQuery query=new FsGroupQuery();
            query.setType(FsGroupTypeEnum.REGION.getCode());
            List<FsGroupDO> fsGroupDOS = fsGroupMapper.selectByQuery(eid, query);
            List<String> chatIds = fsGroupDOS.stream().map(c -> c.getChatId()).collect(Collectors.toList());
            allChatIds.addAll(chatIds);
        }
        if (StringUtils.isNotBlank(otherChatIds)){
            List<String> ids = Arrays.stream(StringUtils.split(otherChatIds, ",")).collect(Collectors.toList());
            List<String> chatIds =fsGroupMapper.queryChatIdByIds(eid,ids);
            allChatIds.addAll(chatIds);
        }else {
            FsGroupQuery query=new FsGroupQuery();
            query.setType(FsGroupTypeEnum.OTHER.getCode());
            List<FsGroupDO> fsGroupDOS = fsGroupMapper.selectByQuery(eid, query);
            List<String> chatIds = fsGroupDOS.stream().map(c -> c.getChatId()).collect(Collectors.toList());
            allChatIds.addAll(chatIds);
        }
        //去重
        allChatIds = allChatIds.stream().distinct().collect(Collectors.toList());
        return allChatIds;
    }


    private String getNoticeContent(String name,String content,String img,String detailUrl){
        String template = "{\"config\":{},\"i18n_elements\":{\"en_us\":[{\"tag\":\"column_set\",\"flex_mode\":\"none\",\"background_style\":\"default\",\"horizontal_spacing\":\"8px\",\"horizontal_align\":\"left\",\"columns\":[{\"tag\":\"column\",\"width\":\"weighted\",\"vertical_align\":\"top\",\"vertical_spacing\":\"8px\",\"background_style\":\"default\",\"elements\":[{\"tag\":\"column_set\",\"flex_mode\":\"none\",\"background_style\":\"default\",\"horizontal_spacing\":\"8px\",\"horizontal_align\":\"left\",\"columns\":[{\"tag\":\"column\",\"width\":\"auto\",\"vertical_align\":\"center\",\"vertical_spacing\":\"8px\",\"background_style\":\"default\",\"elements\":[{\"tag\":\"img\",\"img_key\":\"img_v2_136e4af5-9893-4f96-a52a-60bb9b6347cg\",\"preview\":true,\"scale_type\":\"crop_center\",\"size\":\"tiny\",\"alt\":{\"tag\":\"plain_text\",\"content\":\"\"}}]},{\"tag\":\"column\",\"width\":\"weighted\",\"vertical_align\":\"top\",\"vertical_spacing\":\"8px\",\"background_style\":\"default\",\"elements\":[{\"tag\":\"column_set\",\"flex_mode\":\"none\",\"horizontal_spacing\":\"default\",\"background_style\":\"default\",\"columns\":[{\"tag\":\"column\",\"elements\":[{\"tag\":\"div\",\"text\":{\"tag\":\"plain_text\",\"content\":\"2023 年 11 月 11 日 10:00\"}}],\"width\":\"weighted\",\"weight\":1}]}],\"weight\":1}]},{\"tag\":\"column_set\",\"flex_mode\":\"none\",\"background_style\":\"default\",\"horizontal_spacing\":\"8px\",\"horizontal_align\":\"left\",\"columns\":[{\"tag\":\"column\",\"width\":\"auto\",\"vertical_align\":\"center\",\"vertical_spacing\":\"8px\",\"background_style\":\"default\",\"elements\":[{\"tag\":\"img\",\"img_key\":\"img_v2_f9d63232-61ec-44f0-9fb7-bf8ba82ea92g\",\"preview\":true,\"scale_type\":\"crop_center\",\"size\":\"tiny\",\"alt\":{\"tag\":\"plain_text\",\"content\":\"\"}}]},{\"tag\":\"column\",\"width\":\"weighted\",\"vertical_align\":\"top\",\"vertical_spacing\":\"8px\",\"background_style\":\"default\",\"elements\":[{\"tag\":\"column_set\",\"flex_mode\":\"none\",\"horizontal_spacing\":\"default\",\"background_style\":\"default\",\"columns\":[{\"tag\":\"column\",\"elements\":[{\"tag\":\"div\",\"text\":{\"tag\":\"plain_text\",\"content\":\"北京学清嘉创大厦 B 座 F2-26\"}}],\"width\":\"weighted\",\"weight\":1}]}],\"weight\":1}]}],\"weight\":1},{\"tag\":\"column\",\"width\":\"auto\",\"vertical_align\":\"center\",\"vertical_spacing\":\"8px\",\"background_style\":\"default\",\"elements\":[{\"tag\":\"button\",\"text\":{\"tag\":\"plain_text\",\"content\":\"\uD83D\uDD25 立即报名\"},\"type\":\"primary\",\"complex_interaction\":true}]}]},{\"tag\":\"img\",\"img_key\":\"img_v2_609930d7-21cc-475a-baba-3de5dafe079g\",\"preview\":true,\"scale_type\":\"fit_horizontal\",\"alt\":{\"tag\":\"plain_text\",\"content\":\"\"}}],\"zh_cn\":[{\"tag\":\"img\",\"img_key\":\"${img}\",\"preview\":true,\"transparent\":false,\"scale_type\":\"fit_horizontal\",\"alt\":{\"tag\":\"plain_text\",\"content\":\"\"},\"corner_radius\":\"0%\"},{\"tag\":\"markdown\",\"content\":\"**${title}**\",\"text_align\":\"left\",\"text_size\":\"normal\"},{\"tag\":\"markdown\",\"content\":\"${content}\",\"text_align\":\"left\",\"text_size\":\"normal\"},{\"tag\":\"column_set\",\"flex_mode\":\"none\",\"background_style\":\"default\",\"horizontal_spacing\":\"8px\",\"horizontal_align\":\"left\",\"columns\":[{\"tag\":\"column\",\"width\":\"weighted\",\"vertical_align\":\"top\",\"vertical_spacing\":\"8px\",\"background_style\":\"default\",\"elements\":[{\"tag\":\"button\",\"text\":{\"tag\":\"plain_text\",\"content\":\"查看完整公告\"},\"type\":\"primary_filled\",\"complex_interaction\":true,\"width\":\"fill\",\"size\":\"small\",\"multi_url\":${url}}],\"weight\":1}],\"margin\":\"16px 0px 0px 0px\"}]},\"i18n_header\":{\"en_us\":{\"title\":{\"tag\":\"plain_text\",\"content\":\"十周年庆典\"},\"subtitle\":{\"tag\":\"plain_text\",\"content\":\"\"},\"template\":\"default\",\"icon\":{\"tag\":\"custom_icon\",\"img_key\":\"img_v2_1bfeb6af-dd10-4e49-8fd2-5a392bc86ccg\"}},\"zh_cn\":{\"title\":{\"tag\":\"plain_text\",\"content\":\"通知公告\"},\"subtitle\":{\"tag\":\"plain_text\",\"content\":\"\"},\"template\":\"orange\"}}}";
        Map<String, String> value = Maps.newHashMap();
        value.put("title",name);
        value.put("content",StringUtils.isNotBlank(content)?content:"");
        if (StringUtils.isNotBlank(img)){
            value.put("img",img);
        }
        JSONObject urlObj = new JSONObject();
        urlObj.put("url",detailUrl);
        value.put("url",urlObj.toJSONString());
        return StringUtil.formatFsCard(template,value);
    }
    private Map<String,String> addFSGroupTopMenu(String token,List<FsGroupTopMenuRequest> requests,String chatId){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        // 创建请求对象
        JSONObject req = new JSONObject();
        List<JSONObject> body= Lists.newArrayList();
        for (FsGroupTopMenuRequest request : requests) {
            JSONObject tab = new JSONObject();
            tab.put("tab_name",request.getTopName());
            tab.put("tab_type","url");
            tab.put("tab_content",JSONObject.parseObject("{\"url\":\""+request.getUrl()+"\"}"));
            body.add(tab);
        }
        req.put("chat_tabs",body);
        log.info("创建飞书群顶部菜单参数:{}",JSONObject.toJSONString(req));
        JSONObject resp = httpRestTemplateService.postForObject("https://open.feishu.cn/open-apis/im/v1/chats/" + chatId + "/chat_tabs", req, JSONObject.class, header);
        log.info("创建飞书群顶部菜单返回:{}",JSONObject.toJSONString(resp));
        if(!("0".equals(resp.getString("code")))) {
            log.error(resp.toJSONString());
            return null;
        }
        JSONObject data = resp.getJSONObject("data");
        ArrayList<LinkedHashMap> chatTabs = data.getObject("chat_tabs", ArrayList.class);
        Map<String, String> nameAndTabId= Maps.newHashMap();
        chatTabs.stream().forEach(tab->{
            String type = tab.get("tab_type").toString();
            if ("url".equals(type)){
                String tabName = tab.get("tab_name").toString();
                String tabId = tab.get("tab_id").toString();
                nameAndTabId.put(tabName,tabId);
            }
        });
        return nameAndTabId;
    }


    private String getGroupNoticeVersion(String token,String chatId){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        //发送飞书消息
        log.info("获取飞书群公告版本参数:{}",chatId);
        JSONObject resp = httpRestTemplateService.getForObject("https://open.feishu.cn/open-apis/im/v1/chats/"+chatId+"/announcement", JSONObject.class, header);
        log.info("获取飞书群公告版本返回:{}",resp);
        if (resp.getIntValue("code")!=0){
            log.error("获取飞书群公告版本失败:{}",resp.toJSONString());
            return null;
        }
        return resp.getJSONObject("data").getString("revision");
    }


    @Override
    public String sendFsMsg(String token,String receiveIdType,String receiveId,String msgType,String content){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("receive_id",receiveId);
        params.put("msg_type",msgType);
        params.put("content",content);
        //发送飞书消息
        log.info("发送飞书消息参数:{}",params);
        JSONObject resp = httpRestTemplateService.postForObject("https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type="+receiveIdType, params, JSONObject.class, header);
        log.info("发送飞书消息返回:{}",resp);
        if (resp.getIntValue("code")!=0){
            log.error("发送飞书消息失败:{}",resp.toJSONString());
            throw new RuntimeException("发送飞书消息失败");
        }
        String msgId = resp.getJSONObject("data").getString("message_id");
        return msgId;
    }

    @Override
    @Async
    public void deletedGroup(String eid, String chatId, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        sendDeletedGroupMsg(chatId, token);

        //逻辑删除主表
        fsGroupMapper.deleteByChatId(eid, chatId);
        //物理删除映射表
        fsGroupMappingMapper.deleteByChatId(eid, chatId);
        fsGroupMenuMappingMapper.deleteByChatId(eid, chatId);
        fsGroupNoticeMappingMapper.deleteByChatId(eid, chatId);
        fsGroupSceneMappingMapper.deleteByChatId(eid, chatId);
        fsGroupTopMenuMappingMapper.deleteByChatId(eid, chatId);

    }

    @Override
    public void deletedGroupNotice(String eid, Long noticeId) {
        fsGroupNoticeMapper.deleteById(eid, noticeId);
        fsGroupNoticeMappingMapper.deleteByNoticeId(eid, noticeId);
    }

    @Override
    public void deletedGroupTopMenu(String eid, Long topMenuId, CurrentUser user) {
        //先删除飞书群置顶
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //查出映射群
        List<FsGroupTopMenuMappingDO> mappingDOS = fsGroupTopMenuMappingMapper.queryByMenuId(eid, topMenuId);
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        if (CollectionUtils.isNotEmpty(mappingDOS)){
            mappingDOS.stream().forEach(c->{
                rateLimiter.acquire();
                sendDeletedGroupTopMenuMsg(c.getChatId(), token, c.getFsTabId());
            });
        }
        fsGroupTopMenuMapper.deleteById(eid,topMenuId);
        fsGroupTopMenuMappingMapper.deleteByMenuId(eid, topMenuId);
    }

    @Override
    public PageInfo<FsGroupMenuVO> getGroupMenuList(String eid, FsGroupMenuQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FsGroupMenuVO> vos = fsGroupMenuMapper.selectByQuery(eid, query);
        List<String> ids = vos.stream().map(FsGroupMenuVO::getCreateUser).distinct().collect(Collectors.toList());
        Map<String, String> idNameMap = enterpriseUserDao.getUserNameMap(eid, ids);
        vos.stream().forEach(c->c.setCreateUserName(idNameMap.get(c.getCreateUser())));
        return new PageInfo<>(vos);
    }

    @Override
    public void deletedGroupMenu(String eid, Long menuId, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //查出映射群
        List<FsGroupMenuMappingDO> mappingDOS = fsGroupMenuMappingMapper.queryByMenuId(eid, menuId);
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        if (CollectionUtils.isNotEmpty(mappingDOS)){
            mappingDOS.stream().forEach(c->{
                rateLimiter.acquire();
                try {
                    sendDeletedGroupMenuMsg(c.getChatId(), token, c.getLevelId());
                }catch (Exception e){
                    log.error("删除飞书群菜单失败:{}:{}",c.getChatId(),c.getMenuId());
                }
            });
        }
        fsGroupMenuMapper.deleteById(eid,menuId);
        fsGroupMenuMappingMapper.deleteByMenuId(eid, menuId);
    }

    @Override
    public void updateGroupMenu(String eid, FsGroupMenuRequest request, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        FsGroupMenuDO menuDO = fsGroupMenuMapper.queryById(eid, request.getId());
        if (Objects.isNull(menuDO)){
            log.error("更新飞书群菜单失败，不存在这个菜单:{}",request.getId());
        }
        menuDO.setMenuName(request.getMenuName());
        menuDO.setUrl(request.getUrl());
        menuDO.setUrlType(request.getUrlType());
        menuDO.setUpdateTime(new Date());
        menuDO.setUpdateUser(user.getUserId());
        fsGroupMenuMapper.update(eid, menuDO);

        //根据menuId查出所有映射群和levelId
        List<FsGroupMenuMappingDO> mappingDOS = fsGroupMenuMappingMapper.queryByMenuId(eid, request.getId());
        if (CollectionUtils.isEmpty(mappingDOS)){
            return;
        }
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        mappingDOS.stream().forEach(c->{
            rateLimiter.acquire();
            sendUpdateGroupMenuMsg(c.getChatId(),request, token, c.getLevelId());
        });

    }

    @Override
    public void updateGroupTopMenu(String eid, FsGroupTopMenuRequest request, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        FsGroupTopMenuDO menuDO = fsGroupTopMenuMapper.queryById(eid, request.getId());
        if (Objects.isNull(menuDO)){
            log.error("更新飞书群置顶失败，不存在这个置顶项:{}",request.getId());
        }
        menuDO.setTopName(request.getTopName());
        menuDO.setUrl(request.getUrl());
        menuDO.setUrlType(request.getUrlType());
        menuDO.setUpdateTime(new Date());
        menuDO.setUpdateUser(user.getUserId());
        fsGroupTopMenuMapper.update(eid, menuDO);

        //根据menuId查出所有映射群和levelId
        List<FsGroupTopMenuMappingDO> mappingDOS = fsGroupTopMenuMappingMapper.queryByMenuId(eid, request.getId());
        if (CollectionUtils.isEmpty(mappingDOS)){
            return;
        }
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        mappingDOS.stream().forEach(c->{
            rateLimiter.acquire();
            sendUpdateGroupTopMenuMsg(c.getChatId(),request, token, c.getFsTabId());
        });
    }

    @Override
    public void updateGroupNotice(String eid, FsGroupNoticeRequest request, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        FsGroupNoticeDO noticeDO = fsGroupNoticeMapper.queryById(eid, request.getId());
        if (Objects.isNull(noticeDO)){
            log.error("更新飞书群公告失败，不存在这个公告:{}",request.getId());
        }
        noticeDO.setName(request.getName());
        noticeDO.setContent(request.getContent());
        noticeDO.setImg(request.getImg());
        noticeDO.setDetailUrl(request.getDetailUrl());
        noticeDO.setDetailType(request.getDetailType());
        noticeDO.setUpdateTime(new Date());
        noticeDO.setUpdateUser(user.getUserId());
        fsGroupNoticeMapper.update(eid, noticeDO);
        //根据noticeId查出所有映射群和MsgId
        List<FsGroupNoticeMappingDO> mappingDOS = fsGroupNoticeMappingMapper.queryByNoticeId(eid, request.getId());
        if (CollectionUtils.isEmpty(mappingDOS)){
            return;
        }
        String token = fsService.getAccessToken(user.getDingCorpId(), AppTypeEnum.FEI_SHU.getValue());
        mappingDOS.stream().forEach(c->{
            rateLimiter.acquire();
            sendUpdateGroupNoticeMsg(c.getChatId(),request, token, c.getMsgId());
        });

    }

    private void sendUpdateGroupNoticeMsg(String chatId, FsGroupNoticeRequest request, String token, String msgId) {
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
        Map<String, String> params = Maps.newHashMap();
        String content = getNoticeContent(request.getName(), request.getContent(), request.getImg(), request.getDetailUrl());
        params.put("content",content);
        log.info("更新飞书群公告参数:{}:{}",chatId,JSONObject.toJSONString(params));
        JSONObject resp = httpRestTemplateService.patchWithBody("https://open.feishu.cn/open-apis/im/v1/messages/"+msgId, params, JSONObject.class, header);
        log.info("更新飞书群公告返回:{}",resp);
    }

    private void sendUpdateGroupTopMenuMsg(String chatId, FsGroupTopMenuRequest request, String token, String fsTabId) {
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
        HashMap<String, Object> params = Maps.newHashMap();
        List<JSONObject> tabs = Lists.newArrayList();
        JSONObject tab = new JSONObject();
        tab.put("tab_id",fsTabId);
        tab.put("tab_name",request.getTopName());
        tab.put("tab_type","url");
        tab.put("tab_content",JSONObject.parseObject("{\"url\":\""+request.getUrl()+"\"}"));
        tabs.add(tab);
        params.put("chat_tabs",tabs);
        log.info("更新飞书群顶部菜单参数:{}",chatId);
        JSONObject resp = httpRestTemplateService.postForObject("https://open.feishu.cn/open-apis/im/v1/chats/"+chatId+"/chat_tabs/update_tabs", params, JSONObject.class, header);
        log.info("更新飞书群顶部菜单返回:{}",resp);
    }

    private Boolean sendUpdateGroupMenuMsg(String chatId,FsGroupMenuRequest request ,String token, String levelId) {
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
        HashMap<String, Object> params = Maps.newHashMap();
        List<String> fields = Lists.newArrayList();

        fields.add("NAME");
        fields.add("REDIRECT_LINK");

        Map<String, Object> item = Maps.newHashMap();
        item.put("name",request.getMenuName());
        item.put("redirect_link",JSONObject.parseObject("{\"common_url\":\""+request.getUrl()+"\"}"));

        params.put("update_fields",fields);
        params.put("chat_menu_item",item);
        log.info("更新飞书群菜单参数:{}",chatId);
        JSONObject resp = httpRestTemplateService.patchWithBody("https://open.feishu.cn/open-apis/im/v1/chats/"+chatId+"/menu_items/"+levelId, params, JSONObject.class, header);
        log.info("更新飞书群菜单返回:{}",resp);
        return resp.getIntValue("code")==0;
    }

    private Boolean sendDeletedGroupMsg(String chatId,String token){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        log.info("删除飞书群参数:{}",chatId);
        JSONObject resp = httpRestTemplateService.deleteForObject("https://open.feishu.cn/open-apis/im/v1/chats/" + chatId, JSONObject.class, header);
        log.info("删除飞书群返回:{}",resp);
        return resp.getIntValue("code")==0;
    }

    private Boolean sendDeletedGroupTopMenuMsg(String chatId,String token,String tabId){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
        HashMap<String, Object> params = Maps.newHashMap();
        List<String> tabIds = Lists.newArrayList();
        tabIds.add(tabId);
        params.put("tab_ids",tabIds);
        log.info("删除飞书群菜单参数:{}",chatId);
        JSONObject resp = httpRestTemplateService.deleteWithBody("https://open.feishu.cn/open-apis/im/v1/chats/" + chatId + "/chat_tabs/delete_tabs", params, JSONObject.class, header);
        log.info("删除飞书群菜单返回:{}",resp);
        return resp.getIntValue("code")==0;
    }

    private Boolean sendDeletedGroupMenuMsg(String chatId,String token,String levelId){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
        HashMap<String, Object> params = Maps.newHashMap();
        List<String> levelIds = Lists.newArrayList();
        levelIds.add(levelId);
        params.put("chat_menu_top_level_ids",levelIds);
        log.info("删除飞书群菜单参数:{}",chatId);
        JSONObject resp = httpRestTemplateService.deleteWithBody("https://open.feishu.cn/open-apis/im/v1/chats/"+chatId+"/menu_tree", params, JSONObject.class, header);
        log.info("删除飞书群菜单返回:{}",resp);
        return resp.getIntValue("code")==0;
    }

    public String putTopMsg(String token,String chatId,String msgId){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
        HashMap<String, Object> params = Maps.newHashMap();
        HashMap<String, Object> data = Maps.newHashMap();
        data.put("action_type","1");
        data.put("message_id",msgId);
        params.put("chat_top_notice",Lists.newArrayList(data));
        //发送飞书消息
        log.info("置顶飞书消息参数:{}",params);
        JSONObject resp = httpRestTemplateService.postForObject("https://open.feishu.cn/open-apis/im/v1/chats/"+chatId+"/top_notice/put_top_notice", params, JSONObject.class, header);
        log.info("置顶飞书消息返回:{}",resp);
        if (resp.getIntValue("code")!=0){
            log.error("置顶飞书消息失败:{}",resp.toJSONString());
            return null;
        }
        return msgId;
    }

    public String createFsGroup(String eid,String token,FsGroupAddRequest request, List<String> userIds,String curRegionId,CurrentUser user){
        //调用飞书接口创建群
        String chatId = sendCreateFsGroupAPI(user.getDingCorpId(),token, request,userIds);
        List<RegionDO> regionDOS=Lists.newArrayList();
        if (!FsGroupTypeEnum.STORE.getCode().equals(request.getType())){
            List<StoreWorkCommonDTO> regionIds = request.getRegionIds();
            List<String> storeIds = regionIds.stream().filter(c -> FsGroupTypeEnum.STORE.getCode().equals(c.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
            List<String> regionIdList = regionIds.stream().filter(c -> FsGroupTypeEnum.REGION.getCode().equals(c.getType())).map(StoreWorkCommonDTO::getValue).collect(Collectors.toList());
            //根据storeIds查询regionIds
            if (CollectionUtils.isNotEmpty(storeIds)){
                regionDOS.addAll(regionMapper.getRegionIdByStoreIds(eid, storeIds));
            }
            if (CollectionUtils.isNotEmpty(regionIdList)){
                regionDOS.addAll(regionMapper.getRegionByRegionIds(eid,regionIdList));
            }
        }else {
            regionDOS.add(regionMapper.getByRegionId(eid, Long.valueOf(curRegionId)));
        }
        regionDOS.stream().distinct();
        StringBuilder regionWays=new StringBuilder();
        regionDOS.stream().forEach(c->{
            String fullRegionPath = c.getFullRegionPath();
            if (StringUtils.isNotBlank(fullRegionPath)){
                regionWays.append(fullRegionPath+",");
            }
        });
        String idStr = regionDOS.stream().map(c->c.getRegionId()).collect(Collectors.joining(",", ",", ","));
        Date date = new Date();
        FsGroupDO groupDO = FsGroupDO.builder()
                .chatId(chatId)
                .name(request.getName())
                .type(request.getType())
                .groupOwnerId(request.getGroupOwnerId())
                .groupOwnerName(request.getGroupOwnerName())
                .bindRegionIds(idStr)
                .bindRegionWays(regionWays.toString())
                .createTime(date)
                .createUserId(user.getUserId())
                .createUserName(user.getName())
                .updateUserId(user.getUserId())
                .updateTime(date)
                .build();
        int insert = fsGroupMapper.insert(eid, groupDO);
        List<FsGroupMappingDO> dos = Lists.newArrayList();
        userIds.stream().forEach(c->{
            FsGroupMappingDO build = FsGroupMappingDO.builder().chatId(chatId).userId(c).createTime(date).build();
            dos.add(build);
        });
        fsGroupMappingMapper.insertBatch(eid,dos);
        return chatId;
    }

    /**
     * 创建飞书群API
     * @param corpId
     * @param request
     * @return
     */
    public String sendCreateFsGroupAPI(String corpId, String token,FsGroupAddRequest request, List<String> userIds){
        HashMap<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);
        header.put("Content-Type", "application/json; charset=utf-8");
        //请求参数
//      https://open.feishu.cn/document/server-docs/group/chat/create?appId=cli_a3e89ad42178d00e
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("name",request.getName());
        params.put("description","");//群描述
        params.put("owner_id",request.getGroupOwnerId());
        params.put("user_id_list",userIds);
        params.put("group_message_type","chat");
        params.put("chat_type","private");
        params.put("join_message_visibility","all_members");
        params.put("leave_message_visibility","all_members");
        params.put("membership_approval","no_approval_required");
        params.put("edit_permission","all_members");
        //创建飞书群
        log.info("创建飞书群参数:{}",params);
        JSONObject resp = httpRestTemplateService.postForObject("https://open.feishu.cn/open-apis/im/v1/chats?user_id_type=open_id", params, JSONObject.class, header);
        log.info("创建飞书群返回:{}",resp);
        if (resp.getIntValue("code")!=0){
            log.error("创建飞书群失败:{}",resp.toJSONString());
            throw new RuntimeException("创建飞书群失败");
        }
        return resp.getJSONObject("data").getString("chat_id");
    }
}
