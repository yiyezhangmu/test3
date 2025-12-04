package com.coolcollege.intelligent.service.storework;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dto.EnterpriseStoreWorkSettingsDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRangeDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkHandleCommentUpdateDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkResolveDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkSingleStoreResolveDTO;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/8 15:22
 * @Version 1.0
 */
public interface StoreWorkService {


    /**
     * 日清任务分解
     * @param storeTaskResolveRequest
     */
    void dayClearTaskResolve(StoreTaskResolveRequest storeTaskResolveRequest);

    void resolve(StoreWorkResolveDTO storeWorkResolveDTO);

    void storeWorkSingleStoreResolve(StoreWorkSingleStoreResolveDTO dto);

    void storeWorkHandleCommentUpdate(StoreWorkHandleCommentUpdateDTO dto);

    /**
     * 日清定义
     * @param enterpriseId
     * @param user
     * @param request
     * @return
     */
    Long buildStoreWork(String enterpriseId, CurrentUser user, BuildStoreWorkRequest request);

    SwStoreWorkDO changeStoreWork(String enterpriseId, BuildStoreWorkRequest request, CurrentUser user, String dingCorpId, String appType);

    SwStoreWorkDetailVO getBuildCacheData(String enterpriseId, Long tempCacheDataId, CurrentUser user);
    /**
     * 店务列表
     * @param eid
     * @param storeWorkSearchRequest
     * @return
     */
    PageInfo<SwStoreWorkVO> storeWorkList(String eid, StoreWorkSearchRequest storeWorkSearchRequest, CurrentUser user);

    List<TbMetaStaTableColumnDO> columnList(String enterpriseId, ColumnListRequest request, CurrentUser user);


    /**
     * 店务详情
     * @param enterpriseId
     * @param storeWorkId
     * @return
     */
    SwStoreWorkDetailVO getStoreWorkDetail(String enterpriseId, Long storeWorkId);

    /**
     * 停止店务
     * @param enterpriseId
     * @param storeWorkId
     */
    void stopStoreWork(String enterpriseId, Long storeWorkId);
    /**
     * 店务删除
     * @param enterpriseId
     * @param storeWorkId
     */
    void delStoreWork(String enterpriseId, Long storeWorkId, String appType, String dingCorpId, CurrentUser user);

    void delStoreWorkTableData(String enterpriseId, Long storeWorkId, String appType, String dingCorpId);


    /**
     * 查询当前人管辖门店 门店店务点评数据
     * @param enterpriseId
     * @param storeWorkClearDetailRequest
     * @return
     */
    PageInfo<StoreDayClearDataVO> getCurrentUserStoreWorkData(String enterpriseId,CurrentUser user, StoreWorkClearDetailRequest storeWorkClearDetailRequest);

    PageInfo<StoreWorkDataDTO> getCurrentUserStoreWorkNoCommentData(String enterpriseId, CurrentUser user, StoreWorkClearDetailRequest storeWorkClearDetailRequest);

    /**
     * 点评首页数据统计
     * @param enterpriseId
     * @param user
     * @param storeWorkClearDetailRequest
     * @return
     */
    StoreWorkRecordStatisticsVO getStoreWorkRecordStatistics(String enterpriseId,CurrentUser user, StoreWorkClearDetailRequest storeWorkClearDetailRequest);

    /**
     *
     * @param enterpriseId
     * @param personPositionList
     */
    void fillPersonPositionName(String enterpriseId, List<StoreWorkCommonDTO> personPositionList);

    /**
     * 店务催办
     * @param enterpriseId
     * @param storeWorkDataListRequest
     * @param appType
     * @return
     */
    ResponseResult<List<String>> storeWorkRemind(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, String appType);

    EnterpriseStoreWorkSettingsDTO getEnterpriseStoreWorkSetting(String enterpriseId);

    void cancelUpcomingWhenDel(String enterpriseId, Long storeWorkId, String appType, String dingCorpId, CurrentUser user);


    Boolean fixCommentUser(List<String> enterpriseIds);

    Boolean checkCanReissue(String eid, Long storeWordId);

    List<String> pmdStoreWorkRemind(String enterpriseId, PmdStoreWorkDataListRequest storeWorkDataListRequest, String appType);

    /**
     * 删除店务子任务
     * @param enterpriseId 企业id
     * @param param 需要删除的数据参数
     * @param userId 用户id
     * @return 删除结果
     */
    Boolean delStoreWorkSubtask(String enterpriseId, StoreWorkSubtaskDelRequest param, String userId);

    List<StoreAreaDTO> getStoreRange(String enterpriseId, List<SwStoreWorkRangeDO> swStoreWorkRangeDOS);
}
