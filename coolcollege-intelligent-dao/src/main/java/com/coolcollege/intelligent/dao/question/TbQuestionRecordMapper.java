package com.coolcollege.intelligent.dao.question;

import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserDetailStatisticsDTO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.dto.QuestionStageDateDTO;
import com.coolcollege.intelligent.model.question.dto.StoreQuestionDTO;
import com.coolcollege.intelligent.model.question.dto.TbQuestionRecordSearchDTO;
import com.coolcollege.intelligent.model.question.request.RegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Date;
import java.util.Set;

/**
 * @author zhangchenbiao
 * @date 2021-12-20 07:18
 */
@Mapper
public interface TbQuestionRecordMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2021-12-20 07:18
     */
    int insertSelective(@Param("record") TbQuestionRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2021-12-20 07:18
     */
    TbQuestionRecordDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2021-12-20 07:18
     */
    TbQuestionRecordDO selectByTaskIdAndStoreId(@Param("enterpriseId") String enterpriseId,
                                                @Param("unifyTaskId")Long unifyTaskId,  @Param("storeId")String storeId,
                                                @Param("loopCount")Long loopCount);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2021-12-20 07:18
     */
    int updateByPrimaryKeySelective(@Param("record") TbQuestionRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2021-12-20 07:18
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);


    List<TbQuestionRecordDO> taskQuestionRecord(@Param("enterpriseId") String enterpriseId,
                                                @Param("regionPath") String regionPath,
                                                @Param("metaColumnIdList") List<Long> metaColumnIdList,
                                                @Param("storeIdList") List<String> storeIdList,
                                                @Param("metaTableId") Long metaTableId,
                                                @Param("taskName") String taskName,
                                                @Param("beginTime") Date beginTime,
                                                @Param("endTime") Date endTime,
                                                @Param("completeBeginDate") Date completeBeginDate, @Param("completeEndDate") Date completeEndDate,
                                                @Param("questionType")String questionType,
                                                @Param("regionPathList") List<String> regionPathList,
                                                @Param("createUserIdList") List<String> createUserIdList);
    /**
     * 查询工单列表
     * @param param TbQuestionRecordSearchDTO
     * @return List<TbQuestionRecordDO>
     */
    List<TbQuestionRecordDO> selectQuestionRecordList(TbQuestionRecordSearchDTO param);

    /**
     * 根据父任务id删除
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     */
    void deleteByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据父任务id删除
     * @param enterpriseId 企业id
     * @param id 父任务id
     */
    void deleteById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 统计问题工单数量
     * @param searchParam TbQuestionRecordSearchDTO
     * @return Long
     */
    Long countQuestionRecords(TbQuestionRecordSearchDTO searchParam);

    /**
     *
     * 清空人员信息
     * dateTime:2021-12-20 07:18
     */
    int clearUserIdById(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);


    /**
     * 工单列表 开发平台使用 修改谨慎
     * @param param QuestionDTO
     * @return List<TbQuestionRecordDO>
     */
    List<TbQuestionRecordDO> questionList(@Param("enterpriseId") String enterpriseId, @Param("param") QuestionDTO param);

    /**
     *
     * 清空人员信息
     * dateTime:2021-12-20 07:18
     */
    UnifySubStatisticsDTO selectQuestionTaskCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);
    /**
     *
     * 清空人员信息
     * dateTime:2021-12-20 07:18
     */
    Long selectDataColumnId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     *
     * 1
     * dateTime:2021-12-20 07:18
     */
    TbQuestionRecordDO getByDataColumnId(@Param("enterpriseId") String enterpriseId, @Param("dataColumnId") Long dataColumnId, @Param("isStoreWork") Boolean isStoreWork);

    /**
     *
     * 1
     * dateTime:2021-12-20 07:18
     */
    List<TbQuestionRecordDO> selectListDataColumnIdList(@Param("enterpriseId") String enterpriseId, @Param("dataColumnIdList") List<Long> dataColumnIdList);


    /**
     * 查询工单详情
     * @param param
     * @return
     */
    List<TbQuestionRecordDO> selectSubQuestionDetailList(@Param("enterpriseId") String enterpriseId,
                                                         @Param("param") TbQuestionRecordSearchRequest param,
                                                         @Param("fullRegionPath") String fullRegionPath,
                                                         @Param("fullRegionPathList")List<String> fullRegionPathList);

    /**
     * 工单详情数量
     * @param enterpriseId
     * @param param
     * @param fullRegionPath
     * @return
     */
    Long countSubQuestionDetailList(@Param("enterpriseId") String enterpriseId,
                                       @Param("param") TbQuestionRecordSearchRequest param,
                                       @Param("fullRegionPath") String fullRegionPath,
                                        @Param("fullRegionPathList")List<String> fullRegionPathList);


    List<TbQuestionRecordDO> questionListByTaskStoreIds(@Param("enterpriseId") String enterpriseId, @Param("taskStoreIdList") List<Long> taskStoreIdList);


    List<StoreQuestionDTO> getStoreQuestion(@Param("enterpriseId") String enterpriseId,@Param("storeIdList") List<String> storeIdList);
    /**
     *
     * 清空人员信息
     * dateTime:2021-12-20 07:18
     */
    List<TbQuestionRecordDO> questionListByTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds") List<Long> unifyTaskIds,  @Param("status") String status);


    /**
     * 查询工单阶段数据
     * @param enterpriseId
     * @return
     */
    List<QuestionStageDateDTO>selectQuestionStageDate(@Param("enterpriseId") String enterpriseId, @Param("entity") RegionQuestionReportRequest request, @Param("fullRegionPath") String  fullRegionPath);

    /**
     * 已整改工单数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    Integer getRectifiedQuestionCount(@Param("enterpriseId") String enterpriseId, @Param("entity") RegionQuestionReportRequest request,@Param("fullRegionPath") String  fullRegionPath);

    /**
     * 处理阶段逾数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    Integer handleStageOverdueCount(@Param("enterpriseId") String enterpriseId, @Param("entity") RegionQuestionReportRequest request,@Param("fullRegionPath") String  fullRegionPath);


    /**
     * 完成阶段逾期数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    Integer completeStageOverdueCount(@Param("enterpriseId") String enterpriseId, @Param("entity") RegionQuestionReportRequest request,@Param("fullRegionPath") String  fullRegionPath);

    /**
     * 工单总时长
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    Long questionTotalDuration(@Param("enterpriseId") String enterpriseId, @Param("entity") RegionQuestionReportRequest request,@Param("fullRegionPath") String  fullRegionPath);


    /**
     * 通过驳回次数
     * @param enterpriseId
     * @param request
     * @param fullRegionPath
     * @return
     */
    QuestionStageDateDTO approveStagePassOrRejectCount(@Param("enterpriseId") String enterpriseId, @Param("entity") RegionQuestionReportRequest request,@Param("fullRegionPath") String  fullRegionPath);

    /**
     * 根据id 获取工单记录
     * @param enterpriseId
     * @param ids
     * @return
     */
    List<TbQuestionRecordDO> questionListByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);


    List<UserDetailStatisticsDTO> getUserCreateQuestionInfo(@Param("enterpriseId") String enterpriseId, @Param("userIds") Set<String> userIds,
                                                            @Param("questionTypes") List<String> questionTypes, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    List<TbQuestionRecordDO> getUserCreateQuestionRecordByUserId(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds,
                                                            @Param("questionTypes") List<String> questionTypes, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    List<TbQuestionRecordDO> getUserCreateQuestionRecordByStoreId(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds,
                                                            @Param("questionTypes") List<String> questionTypes, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);


    String getQuestionTypeByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    int updateQuestionRecordFinish(@Param("enterpriseId") String enterpriseId, @Param("approveTime") Date approveTime
            , @Param("approveUserId") String approveUserId, @Param("approveUserName") String approveUserName
            ,@Param("approveActionKey") String approveActionKey, @Param("unifyTaskId") Long unifyTaskId);

    int updateHandleInfoByTaskId(@Param("enterpriseId") String enterpriseId, @Param("handleTime") Date handleTime
            , @Param("handleUserId") String handleUserId, @Param("handleUserName") String handleUserName
            ,@Param("handleActionKey") String handleActionKey, @Param("unifyTaskId") Long unifyTaskId);

    List<TbQuestionRecordDO> selectByUnifyTaskIds(@Param("enterpriseId") String enterpriseId,
                                                  @Param("unifyTaskIds") List<Long> unifyTaskIds);

    List<TbQuestionRecordDO> getSubQuestionByParentUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据store表订正问题工单任务记录表的regionId和regionPath
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @return int
     */
    int correctRegionIdAndPath(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据门店任务查询工单记录
     * @param enterpriseId 企业id
     * @param taskStoreList 门店任务列表
     * @return 巡店记录列表
     */
    List<TbQuestionRecordDO> selectByTaskStore(@Param("enterpriseId") String enterpriseId, @Param("taskStoreList") List<TaskStoreDO> taskStoreList);

    /**
     * 根据id查询工单记录
     * @param enterpriseId 企业id
     * @param ids 主键id列表
     * @return 工单记录列表
     */
    List<TbQuestionRecordDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);


    int getCountQuestionTypeAndDataColumnId(@Param("enterpriseId") String enterpriseId, @Param("questionType") String questionType, @Param("dataColumnId") Long dataColumnId);
}

