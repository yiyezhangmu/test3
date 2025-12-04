package com.coolcollege.intelligent.service.question;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.QuestionRecordDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.QuestionRecordListVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.request.QuestionRecordListRequest;
import com.coolcollege.intelligent.model.question.request.RegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.question.vo.*;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.taobao.api.ApiException;

import java.util.List;

/**
 * 问题工单任务记录
 * @author zhangnan
 * @date 2021-12-22 13:53
 */
public interface QuestionRecordService {

    /**
     * 查询工单列表（分页）
     * @param enterpriseId 企业id
     * @param searchRequest TbQuestionRecordSearchRequest
     * @param pageRequest pageRequest
     * @param user CurrentUser
     * @return PageVO<TbQuestionRecordListVO>
     */
    PageVO<TbQuestionRecordListVO> list(String enterpriseId, TbQuestionRecordSearchRequest searchRequest, PageRequest pageRequest, CurrentUser user);

    /**
     * 问题工单详情
     * @param enterpriseId 企业id
     * @param unifyTaskId 任务id
     * @param storeId 门店id
     * @param user 用户
     * @return TbQuestionRecordDetailVO
     */
    TbQuestionRecordDetailVO detail(String enterpriseId, Long unifyTaskId, String storeId, CurrentUser user, Long loopCount);

    /**
     * 完成任务
     * @param taskMessageDTO
     * @param isComplete
     * @return
     */
    boolean updateQuestionTaskRecord(TaskMessageDTO taskMessageDTO, Boolean isComplete);

    /**
     * 查询工单列表（分页，移动端）
     * @param enterpriseId 企业id
     * @param searchRequest TbQuestionRecordSearchRequest
     * @param pageRequest pageRequest
     * @param user CurrentUser
     * @return PageVO<TbQuestionRecordMobileListVO>
     */
    PageVO<TbQuestionRecordMobileListVO> listForMobile(String enterpriseId, TbQuestionRecordSearchRequest searchRequest,
                                                       PageRequest pageRequest, CurrentUser user);

    /**
     * 导出工单
     * @param enterpriseId 企业id
     * @param recordSearchRequest TbQuestionRecordSearchRequest
     * @param user CurrentUser
     * @return ImportTaskDO
     */
    ImportTaskDO export(String enterpriseId, TbQuestionRecordSearchRequest recordSearchRequest, CurrentUser user);

    /**
     * 导出工单查询
     * @param enterpriseId 企业id
     * @param searchRequest TbQuestionRecordSearchRequest
     * @param dbName 数据库名称
     * @param pageRequest PageRequest
     * @return PageVO<TbQuestionRecordExportVO>
     */
    PageVO<TbQuestionRecordExportVO> listForExport(String enterpriseId, TbQuestionRecordSearchRequest searchRequest, PageRequest pageRequest, String dbName);

    /**
     * 根据工单id删除
     * @param enterpriseId 企业id
     * @param questionRecordId 工单id
     * @param dingCorpId 钉钉组织id
     * @param appType apptype
     */
    void deleteByQuestionRecordId(String enterpriseId, Long questionRecordId, String dingCorpId, String appType);

    /**
     * 添加工单记录
     * @param taskMessageDTO
     * @param dataColumnId
     * @param metaColumnId
     * @param contentLearnFirst
     */
    void addQuestionRecord(TaskMessageDTO taskMessageDTO, Long dataColumnId, Long metaColumnId, Boolean contentLearnFirst);


    /**
     * 陈列列表 开发平台使用
     * @param enterpriseId
     * @param questionDTO
     * @return
     */
    PageDTO<QuestionRecordListVO> questionList(String enterpriseId, QuestionDTO questionDTO);


    /**
     * 工单详情 开发平台
     * @param enterpriseId
     * @param questionId
     * @return
     */
    QuestionRecordDetailVO questionDetail(String enterpriseId, Long questionId);

    /**
     * 子工单列表
     * @param enterpriseId
     * @param questionRecordListRequest
     * @return
     */
    PageDTO<SubQuestionRecordListVO> subQuestionRecordList(String enterpriseId, QuestionRecordListRequest questionRecordListRequest);


    /**
     * 删除子工单
     * @param enterpriseId
     * @param recordId
     */
    void delQuestionRecord(String enterpriseId, Long recordId, String dingCorpId, String appType);

    /**
     * 工单催办
     * @param enterpriseId 企业id
     * @param questionParentInfoId  父工单id
     * @param questionRecordId 子工单id
     * @return
     */
    ResponseResult<List<String>> questionReminder(String enterpriseId, Long questionParentInfoId, Long questionRecordId, String appType);

    void batchQuestionReminder(String enterpriseId, List<Long> questionParentInfoIds, String appType);

    /**
     * 工单分享
     * @param enterpriseId 企业id
     * @param isOneKeyShare  是否批量
     * @param questionRecordIds 子工单id
     * @param shareKey
     */
    void questionShare(String enterpriseId, Boolean isOneKeyShare, List<Long> questionRecordIds, String shareUserId, String shareKey);


    /**
     * 获取工单分享详情
     * @param enterpriseId 企业id
     * @param shareKey 分享key
     * @return
     */
    ResponseResult getQuestionShareDetail(String enterpriseId, Long questionRecordId, String shareKey) throws ApiException;

    /**
     * 子工单详情表
     * @param enterpriseId
     * @param tbQuestionRecordSearchRequest
     * @return
     */
    PageDTO<SubQuestionDetailVO> subQuestionDetailList(String enterpriseId, String userId, TbQuestionRecordSearchRequest tbQuestionRecordSearchRequest);

    /**
     * 区域工单报表
     * @param enterpriseId
     * @param request
     * @return
     */
    List<RegionQuestionReportVO> getQuestionReport(String enterpriseId, RegionQuestionReportRequest request, CurrentUser user);



    /**
     * 待办-工单详列表
     * @param enterpriseId
     * @param type 我创建的/我管理的:all 待我处理/审批:pending 抄送给我的:cc ,默认查pending
     * @return
     */
    List<SubQuestionRecordListVO> questionDetailList(String enterpriseId, List<Long> questionParentInfoIds, String userId, String status, Boolean isBatchApprove, String type);


    /**
     * 工单详情导出
     * @param enterpriseId
     * @param tbQuestionRecordSearchRequest
     * @param user
     * @return
     */
    ImportTaskDO subQuestionDetailListExport(String enterpriseId, TbQuestionRecordSearchRequest tbQuestionRecordSearchRequest,CurrentUser user);

    /**
     * 区域工单报表导出
     * @param enterpriseId
     * @param request
     * @param user
     * @return
     */
    ImportTaskDO regionQuestionReportExport(String enterpriseId, RegionQuestionReportRequest request,CurrentUser user);

    /**
     * 查询工单列表（分页，移动端）
     * @param enterpriseId 企业id
     * @param searchRequest TbQuestionRecordSearchRequest
     * @param pageRequest pageRequest
     * @param user CurrentUser
     * @return PageVO<TbQuestionRecordMobileListVO>
     */
    PageVO<SubQuestionRecordListVO> recordList(String enterpriseId, TbQuestionRecordSearchRequest searchRequest,
                                                       PageRequest pageRequest, CurrentUser user);

    /**
     * 获取工单详情
     * @param enterpriseId
     * @param dataColumnId
     * @return
     */
    SubQuestionRecordListVO getRecordByDataColumnId(String enterpriseId, Long dataColumnId);

    PageDTO<TbQuestionSubRecordListExportVO> subQuestionDetailListForExport(String enterpriseId, TbQuestionRecordSearchRequest request);

    /**
     * 工单追踪
     * @param enterpriseId
     * @param businessId
     * @param user
     * @return
     */
    List<SubQuestionRecordListVO> questionPatrolList(String enterpriseId, Long businessId,CurrentUser user);

    List<TbQuestionRecordDO> getSubQuestionByParentUnifyTaskId(String enterpriseId, Long unifyTaskId);
}
