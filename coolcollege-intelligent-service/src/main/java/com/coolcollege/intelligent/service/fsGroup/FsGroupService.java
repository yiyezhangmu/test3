package com.coolcollege.intelligent.service.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupDO;
import com.coolcollege.intelligent.model.fsGroup.query.*;
import com.coolcollege.intelligent.model.fsGroup.request.*;
import com.coolcollege.intelligent.model.fsGroup.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * (FsGroup)表服务接口
 *
 * @author CFJ
 * @since 2024-04-23 09:36:29
 */
public interface FsGroupService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupDO queryById(String enterpriseId,Long id);


    /**
     * 新增数据
     *
     * @param fsGroupDO 实例对象
     * @return 实例对象
     */
    FsGroupDO insert(String enterpriseId,FsGroupDO fsGroupDO);

    /**
     * 修改数据
     *
     * @param fsGroupDO 实例对象
     * @return 实例对象
     */
    FsGroupDO update(String enterpriseId,FsGroupDO fsGroupDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String enterpriseId,Long id);

    /**
     *  添加飞书群
     * @param eid
     * @param request
     */
    void addFsGroup(String eid, FsGroupAddRequest request, CurrentUser user);

    void addGroupTopMsg(String eid, FsGroupTopMenuRequest request, CurrentUser user);

    void addGroupNotice(String eid, FsGroupNoticeRequest request, CurrentUser user);

    void addGroupTopMenu(String eid, FsGroupTopMenuRequest request, CurrentUser user);

    PageInfo<FsGroupVO> getFsGroupList(String eid, FsGroupQuery query);

    PageInfo<FsGroupSceneVO> getFsGroupSceneList(String eid, FsGroupSceneQuery query);

    PageInfo<FsGroupVO> getFsGroupByScene(String eid, FsGroupSceneMappingQuery query);

    PageInfo<FsGroupNoticeVO> getFsGroupNoticeList(String eid, FsGroupNoticeQuery query);

    PageInfo<FsGroupTopMenuVO> getFsGroupTopMenuList(String eid, FsGroupTopMenuQuery query);

    void addGroupMenu(String eid, FsGroupMenuRequest request, CurrentUser user);

    String sendFsMsg(String token,String receiveIdType,String receiveId,String msgType,String content);

    void deletedGroup(String eid, String chatId, CurrentUser user);

    void deletedGroupNotice(String eid, Long noticeId);

    void deletedGroupTopMenu(String eid, Long topMenuId, CurrentUser user);

    PageInfo<FsGroupMenuVO> getGroupMenuList(String eid, FsGroupMenuQuery query);

    void deletedGroupMenu(String eid, Long menuId, CurrentUser user);

    void updateGroupMenu(String eid, FsGroupMenuRequest request, CurrentUser user);

    void updateGroupTopMenu(String eid, FsGroupTopMenuRequest request, CurrentUser user);

    void updateGroupNotice(String eid, FsGroupNoticeRequest request, CurrentUser user);

    void searchChatNotice(String eid);

    FsGroupVO getFsGroupDetail(String eid, Long id);

    FsGroupNoticeVO getGroupNoticeDetail(String eid, Long noticeId);

    FsGroupTopMenuVO getGroupTopMenuDetail(String eid, Long topMenuId);

    FsGroupMenuVO getGroupMenuDetail(String eid, Long menuId);

    String uploadFsImg(String eid, MultipartFile img);

    String getFsToken(String eid);

    void updateGroup(String eid, FsGroupAddRequest request, CurrentUser user);

    byte[] downloadFsImg(String eid, String imgKey);

    Boolean deleteSceneGroup(String eid, List<Long> ids);

    Boolean addSceneForGroups(String eid, SceneGroupIdRequest request);

    void queryChatNoticeReadNum(String enterpriseId);
}
