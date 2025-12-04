package com.coolcollege.intelligent.service.newstore;

import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.NsVisitRecordDTO;
import com.coolcollege.intelligent.model.newstore.request.*;
import com.coolcollege.intelligent.model.newstore.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

/**
 * 新店拜访记录
 * @author zhangnan
 * @date 2022-03-04 17:22
 */
public interface NsVisitRecordService {

    /**
     * 签到并生成新店拜访记录
     * @param eid 企业id
     * @param request NsVisitSignInRequest
     * @return NsVisitSignInVO
     */
    NsVisitSignInVO signIn(String eid, NsVisitSignInRequest request, CurrentUser currentUser);

    /**
     * 获取用户当天进行中拜访记录
     * @param eid 企业id
     * @param newStoreId 新店id
     * @param userId 用户id
     * @return NsTodayOngoingRecordVO
     */
    NsTodayOngoingRecordVO getTodayOngoingRecord(String eid, Long newStoreId, String userId);

    /**
     * 更新拜访记录
     * @param enterpriseId 企业id
     * @param request NsVisitRecordUpdateRequest
     * @param currentUser CurrentUser
     */
    void updateRecord(String enterpriseId, NsVisitRecordUpdateRequest request, CurrentUser currentUser);

    /**
     * 提交拜访记录
     * @param enterpriseId 企业id
     * @param request NsVisitRecordSubmitRequest
     * @param currentUser CurrentUser
     */
    void submitRecord(String enterpriseId, NsVisitRecordSubmitRequest request, CurrentUser currentUser);

    /**
     * 根据拜访记录id查询拜访表信息
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     * @return NsVisitTableInfoVO
     */
    NsVisitTableInfoVO getVisitTableInfoByRecordId(String enterpriseId, Long recordId);

    /**
     * 保存拜访表信息
     * @param enterpriseId 企业id
     * @param request NsDataVisitTableColumnSaveRequest
     * @param currentUser CurrentUser
     */
    void saveVisitTableInfo(String enterpriseId, NsDataVisitTableColumnSaveRequest request, CurrentUser currentUser);

    /**
     * 分页查询拜访记录
     * @param enterpriseId 企业id
     * @param request NsVisitRecordListRequest
     * @return PageInfo<NsVisitRecordListVO>
     */
    PageInfo<NsVisitRecordListVO> getRecordList(String enterpriseId, NsVisitRecordListRequest request);

    /**
     * 根据拜访记录id查询拜访记录详情
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     * @return NsVisitRecordDetailVO
     */
    NsVisitRecordDetailVO getRecordDetail(String enterpriseId, Long recordId);

    /**
     * 分页查询拜访记录 开放接口
     * @param enterpriseId
     * @param param
     * @return
     */
    PageDTO<NsVisitRecordListVO> getVisitRecordList(String enterpriseId, NsVisitRecordDTO param);
}
