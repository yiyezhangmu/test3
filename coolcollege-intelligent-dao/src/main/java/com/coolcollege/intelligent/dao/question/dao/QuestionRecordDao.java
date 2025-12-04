package com.coolcollege.intelligent.dao.question.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserDetailStatisticsDTO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.dto.QuestionStageDateDTO;
import com.coolcollege.intelligent.model.question.dto.TbQuestionRecordSearchDTO;
import com.coolcollege.intelligent.model.question.request.RegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

/**
 * 问题工单任务记录
 * @author zhangnan
 * @date 2021-12-22 13:54
 */
@Repository
public class QuestionRecordDao {

    @Resource
    private TbQuestionRecordMapper questionRecordMapper;

    /**
     * 查询问题工单列表（分页）
     * @param searchParam TbQuestionRecordSearchDTO
     * @param pageNumber 分页页码
     * @param pageSize 分页条数
     * @return PageInfo<TbQuestionRecordDO>
     */
    public PageInfo<TbQuestionRecordDO> selectQuestionRecordPage(TbQuestionRecordSearchDTO searchParam,
                                                                 Integer pageNumber, Integer pageSize) {
        // 参数校验，参数不可以为空
        Optional.ofNullable(searchParam).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        PageHelper.startPage(Optional.ofNullable(pageNumber).orElse(Constants.INDEX_ONE)
                , Optional.ofNullable(pageSize).orElse(Constants.DEFAULT_PAGE_SIZE));
        return new PageInfo<>(questionRecordMapper.selectQuestionRecordList(searchParam));
    }

    /**
     * 根据主键id查询记录
     * @param id
     * @param enterpriseId
     * @return
     */
    public TbQuestionRecordDO selectById(Long id, String enterpriseId){
        return questionRecordMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     * 根据主键id查询记录
     * @param unifyTaskId
     * @param enterpriseId
     * @return
     */
    public TbQuestionRecordDO selectByTaskIdAndStoreId(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount){
        return questionRecordMapper.selectByTaskIdAndStoreId(enterpriseId, unifyTaskId, storeId, loopCount);
    }


    /**
     * 插入
     * @param record
     * @param enterpriseId
     * @return
     */
    public int insertSelective(TbQuestionRecordDO record, String enterpriseId){
        return questionRecordMapper.insertSelective(record, enterpriseId);
    }

    /**
     * 更新
     * @param record
     * @param enterpriseId
     * @return
     */
    public int updateByPrimaryKeySelective(TbQuestionRecordDO record, String enterpriseId){
        return questionRecordMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    /**
     * 图片报表列表
     * @param enterpriseId
     * @return
     */
    public List<TbQuestionRecordDO> taskQuestionRecord(String enterpriseId, String regionPath,
                                                       List<Long> metaColumnIdList,
                                                       List<String> storeIdList,
                                                       Long metaTableId,
                                                       String taskName,
                                                       Date beginTime,
                                                       Date endTime,
                                                       Date completeBeginDate,
                                                       Date completeEndDate,
                                                       String questionType,
                                                       List<String> regionPathList,
                                                       List<String> createUserIdList) {
        return questionRecordMapper.taskQuestionRecord(enterpriseId, regionPath, metaColumnIdList, storeIdList, metaTableId, taskName, beginTime, endTime,
                 completeBeginDate, completeEndDate, questionType, regionPathList, createUserIdList);
    }

    /**
     * 根据父任务id删除
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     */
    public void deleteByUnifyTaskId(String enterpriseId, Long unifyTaskId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskId)) {
            return;
        }
        questionRecordMapper.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
    }

    public void deleteById(String enterpriseId, Long id) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return;
        }
        questionRecordMapper.deleteById(enterpriseId, id);
    }

    /**
     * 查询问题工单列表
     * @param searchParam TbQuestionRecordSearchDTO
     * @return List<TbQuestionRecordDO>
     */
    public List<TbQuestionRecordDO> selectQuestionRecords(TbQuestionRecordSearchDTO searchParam) {
        // 参数校验，创建时间区间参数不可以为空
        Optional.ofNullable(searchParam).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        Optional.ofNullable(searchParam.getBeginCreateDate()).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        Optional.ofNullable(searchParam.getEndCreateDate()).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return questionRecordMapper.selectQuestionRecordList(searchParam);
    }

    /**
     * 统计工单数量
     * @param searchParam TbQuestionRecordSearchDTO
     * @return Long
     */
    public Long countQuestionRecords(TbQuestionRecordSearchDTO searchParam) {
        Optional.ofNullable(searchParam).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return questionRecordMapper.countQuestionRecords(searchParam);
    }

    /**
     * 清空人员信息
     * @param enterpriseId
     * @param id
     * @return
     */
    public int clearUserIdById(String enterpriseId, Long id){
        return questionRecordMapper.clearUserIdById(id, enterpriseId);
    }

    public List<TbQuestionRecordDO> questionList(String enterpriseId,QuestionDTO questionDTO) {
        return questionRecordMapper.questionList(enterpriseId,questionDTO);
    }

    /**
     *
     * 清空人员信息
     * dateTime:2021-12-20 07:18
     */
    public UnifySubStatisticsDTO selectQuestionTaskCount(String enterpriseId, Long unifyTaskId){
        return questionRecordMapper.selectQuestionTaskCount(enterpriseId, unifyTaskId);
    }

    public Long selectDataColumnId(String enterpriseId, Long unifyTaskId){
        return questionRecordMapper.selectDataColumnId(enterpriseId, unifyTaskId);
    }

    public List<TbQuestionRecordDO> selectListDataColumnIdList(String enterpriseId, List<Long> dataColumnIdList){
        if(CollectionUtils.isEmpty(dataColumnIdList)){
            return new ArrayList<>();
        }
        return questionRecordMapper.selectListDataColumnIdList(enterpriseId, dataColumnIdList);
    }

    public TbQuestionRecordDO getByDataColumnId(String enterpriseId, Long dataColumnId,Boolean isStoreWork){
        return questionRecordMapper.getByDataColumnId(enterpriseId, dataColumnId,isStoreWork);
    }

    public PageInfo<TbQuestionRecordDO> selectSubQuestionDetailList(String enterpriseId,String fullRegionPath,List<String> fullRegionPathList,TbQuestionRecordSearchRequest request,
                                                                    Integer pageNumber, Integer pageSize) {
        // 参数校验，参数不可以为空
        Optional.ofNullable(request).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        PageHelper.startPage(Optional.ofNullable(pageNumber).orElse(Constants.INDEX_ONE)
                , Optional.ofNullable(pageSize).orElse(Constants.DEFAULT_PAGE_SIZE));
        return new PageInfo<TbQuestionRecordDO>(questionRecordMapper.selectSubQuestionDetailList(enterpriseId,request,fullRegionPath,fullRegionPathList));
    }

    public Long countSubQuestionDetailList(String enterpriseId, String fullRegionPath,List<String> fullRegionPathList, TbQuestionRecordSearchRequest request) {
        return questionRecordMapper.countSubQuestionDetailList(enterpriseId,request,fullRegionPath,fullRegionPathList);
    }

    public List<TbQuestionRecordDO> questionListByTaskStoreIds(String enterpriseId, List<Long> taskStoreIdList) {
        if(CollectionUtils.isEmpty(taskStoreIdList)){
            return new ArrayList<>();
        }
        return questionRecordMapper.questionListByTaskStoreIds(enterpriseId, taskStoreIdList);
    }

    public List<TbQuestionRecordDO> questionListByTaskId(String enterpriseId, List<Long> unifyTaskIds, String status) {
        if(CollectionUtils.isEmpty(unifyTaskIds)){
            return new ArrayList<>();
        }
        return questionRecordMapper.questionListByTaskId(enterpriseId, unifyTaskIds, status);
    }

    /**
     * 查询工单阶段数据
     * @param enterpriseId
     * @return
     */
    public List<QuestionStageDateDTO> selectQuestionStageDate(String enterpriseId,  RegionQuestionReportRequest request, String  fullRegionPath){
        return questionRecordMapper.selectQuestionStageDate(enterpriseId,request,fullRegionPath);
    }

    /**
     * 已整改工单数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    public Integer getRectifiedQuestionCount(String enterpriseId, RegionQuestionReportRequest request,String  fullRegionPath){
        return questionRecordMapper.getRectifiedQuestionCount(enterpriseId,request,fullRegionPath);
    }

    /**
     * 处理阶段逾数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    public Integer handleStageOverdueCount(String enterpriseId,  RegionQuestionReportRequest request, String  fullRegionPath){
        return questionRecordMapper.handleStageOverdueCount(enterpriseId,request,fullRegionPath);
    }


    /**
     * 完成阶段逾期数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    public Integer completeStageOverdueCount(String enterpriseId,  RegionQuestionReportRequest request,String  fullRegionPath){
        return questionRecordMapper.completeStageOverdueCount(enterpriseId,request,fullRegionPath);
    }

    /**
     * 工单总时长
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    public Long questionTotalDuration(String enterpriseId,  RegionQuestionReportRequest request,String  fullRegionPath){
        return questionRecordMapper.questionTotalDuration(enterpriseId,request,fullRegionPath);
    }


    /**
     * 通过驳回次数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    public  QuestionStageDateDTO approveStagePassOrRejectCount(String enterpriseId,  RegionQuestionReportRequest request,String  fullRegionPath){
        return questionRecordMapper.approveStagePassOrRejectCount(enterpriseId,request,fullRegionPath);
    }

    public List<TbQuestionRecordDO> questionListByIds(String enterpriseId, List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return new ArrayList<>();
        }
        return questionRecordMapper.questionListByIds(enterpriseId, ids);
    }


    public List<UserDetailStatisticsDTO> getUserCreateQuestionInfo(String enterpriseId, Set<String> userIds, List<String> questionTypes, Date beginDate, Date endDate){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return questionRecordMapper.getUserCreateQuestionInfo(enterpriseId, userIds, questionTypes, beginDate, endDate);
    }

    public List<TbQuestionRecordDO> getUserCreateQuestionRecordByUserId(String enterpriseId, List<String> userIds, List<String> questionTypes, Date beginDate, Date endDate){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return questionRecordMapper.getUserCreateQuestionRecordByUserId(enterpriseId, userIds, questionTypes, beginDate, endDate);
    }

    public List<TbQuestionRecordDO> getUserCreateQuestionRecordByStoreId(String enterpriseId, List<String> storeIds, List<String> questionTypes, Date beginDate, Date endDate){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeIds)){
            return Lists.newArrayList();
        }
        return questionRecordMapper.getUserCreateQuestionRecordByStoreId(enterpriseId, storeIds, questionTypes, beginDate, endDate);
    }

    public String getQuestionTypeByUnifyTaskId(String enterpriseId, Long unifyTaskId){
        return questionRecordMapper.getQuestionTypeByUnifyTaskId(enterpriseId, unifyTaskId);
    }

    public int updateQuestionRecordFinish(String enterpriseId,Date approveTime, String approveUserId, String approveUserName, String approveActionKey,  Long unifyTaskId){
        return questionRecordMapper.updateQuestionRecordFinish(enterpriseId, approveTime, approveUserId, approveUserName, approveActionKey, unifyTaskId);
    }
    public int updateHandleInfoByTaskId(String enterpriseId, Date handleTime
            ,String handleUserId, String handleUserName
            ,String handleActionKey, Long unifyTaskId){
        return questionRecordMapper.updateHandleInfoByTaskId(enterpriseId, handleTime, handleUserId, handleUserName, handleActionKey, unifyTaskId);
    }

    public List<TbQuestionRecordDO> getSubQuestionByParentUnifyTaskId(String enterpriseId, Long unifyTaskId){
        return questionRecordMapper.getSubQuestionByParentUnifyTaskId(enterpriseId, unifyTaskId);
    }

    /**
     * 根据store表订正问题工单任务记录表的regionId和regionPath
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @return int
     */
    public int correctRegionIdAndPath(String enterpriseId, Long unifyTaskId) {
        return questionRecordMapper.correctRegionIdAndPath(enterpriseId, unifyTaskId);
    }

    /**
     * 根据门店任务查询工单记录
     * @param enterpriseId 企业id
     * @param taskStoreList 门店任务列表
     * @return 巡店记录列表
     */
    public List<TbQuestionRecordDO> getRecordByTaskStore(String enterpriseId, List<TaskStoreDO> taskStoreList) {
        if (CollectionUtils.isEmpty(taskStoreList)) {
            return Collections.emptyList();
        }
        return questionRecordMapper.selectByTaskStore(enterpriseId, taskStoreList);
    }
}
