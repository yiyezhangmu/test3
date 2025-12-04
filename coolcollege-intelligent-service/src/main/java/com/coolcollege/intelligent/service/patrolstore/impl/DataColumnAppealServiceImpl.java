package com.coolcollege.intelligent.service.patrolstore.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.safetycheck.FoodCheckNoticeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaColumnReasonDao;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreHistoryMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordInfoMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.ScSafetyCheckFlowDao;
import com.coolcollege.intelligent.dao.safetycheck.dao.ScSafetyCheckUpcomingDao;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbDataColumnAppealDao;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbDataColumnHistoryDao;
import com.coolcollege.intelligent.model.common.FileDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.DataColumnOperateTypeEnum;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnResultDTO;
import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaTableInfoVO;
import com.coolcollege.intelligent.model.patrolstore.PatrolStoreConstant;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.dto.BatchDataColumnAppealDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbDataColumnAppealHistoryDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbDataColumnAppealListDTO;
import com.coolcollege.intelligent.model.patrolstore.param.DataColumnAppealParam;
import com.coolcollege.intelligent.model.patrolstore.vo.TbDataStaTableColumnVO;
import com.coolcollege.intelligent.model.region.dto.PatrolStoreScoreMsgDTO;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckFlowDO;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckUpcomingDO;
import com.coolcollege.intelligent.model.safetycheck.TbDataColumnAppealDO;
import com.coolcollege.intelligent.model.safetycheck.TbDataColumnHistoryDO;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCommentAppealVO;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.OrderMessageService;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.patrolstore.DataColumnAppealService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.safetycheck.ScSafetyCheckFlowService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @author byd
 * @date 2023-08-17 15:28
 */
@Slf4j
@Service
public class DataColumnAppealServiceImpl implements DataColumnAppealService {

    @Resource
    private TbDataColumnAppealDao tbDataColumnAppealDao;

    @Resource
    private TbDataStaTableColumnMapper dataStaTableColumnMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;

    @Resource
    private TbMetaTableService metaTableService;

    @Resource
    private TbMetaColumnReasonDao metaColumnReasonDao;

    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Resource
    private ScSafetyCheckFlowService scSafetyCheckFlowService;

    @Resource
    private TbDataColumnHistoryDao dataColumnHistoryDao;

    @Resource
    private TbPatrolStoreHistoryMapper patrolStoreHistoryMapper;

    @Resource
    private ScSafetyCheckFlowDao safetyCheckFlowDao;

    @Resource
    private ScSafetyCheckUpcomingDao safetyCheckUpcomingDao;

    @Resource
    private SimpleMessageService simpleMessageService;


    @Resource
    private OrderMessageService orderMessageService;

    @Resource
    private PatrolStoreService patrolStoreService;

    @Resource
    private JmsTaskService jmsTaskService;

    @Resource
    private TbPatrolStoreRecordInfoMapper patrolStoreRecordInfoMapper;

    @Autowired
    private RedisUtilPool redisUtil;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void appeal(String eid, String userId, String userName, BatchDataColumnAppealDTO batchDataColumnAppealDTO) {
        AtomicReference<Long> businessId = new AtomicReference<>();
        batchDataColumnAppealDTO.getDataColumnAppealList().forEach(dataColumnAppealDTO -> {
            TbDataColumnAppealDO tbDataColumnAppealDO = tbDataColumnAppealDao.selectByDataColumnId(eid, dataColumnAppealDTO.getDataColumnId());
            if (tbDataColumnAppealDO != null) {
                throw new ServiceException(tbDataColumnAppealDO.getMetaColumnName() + "该数据项已经申诉过了");
            }

            TbDataStaTableColumnDO dataStaTableColumnDO = dataStaTableColumnMapper.selectByPrimaryKey(eid, dataColumnAppealDTO.getDataColumnId());
            businessId.set(dataStaTableColumnDO.getBusinessId());
            tbDataColumnAppealDO = new TbDataColumnAppealDO();
            tbDataColumnAppealDO.setBusinessId(dataStaTableColumnDO.getBusinessId());
            tbDataColumnAppealDO.setDataTableId(dataStaTableColumnDO.getDataTableId());
            tbDataColumnAppealDO.setMetaTableId(dataStaTableColumnDO.getMetaTableId());
            tbDataColumnAppealDO.setMetaColumnId(dataStaTableColumnDO.getMetaColumnId());
            tbDataColumnAppealDO.setDataColumnId(dataColumnAppealDTO.getDataColumnId());
            tbDataColumnAppealDO.setMetaColumnName(dataStaTableColumnDO.getMetaColumnName());
            tbDataColumnAppealDO.setAppealStatus(UnifyStatus.ONGOING.getCode());
            tbDataColumnAppealDO.setAppealContent(dataColumnAppealDTO.getAppealContent());
            tbDataColumnAppealDO.setAppealRemark(dataColumnAppealDTO.getAppealRemark());
            tbDataColumnAppealDO.setAppealUserId(userId);
            tbDataColumnAppealDO.setAppealTime(new Date());
            tbDataColumnAppealDO.setCreateTime(new Date());
            tbDataColumnAppealDO.setUpdateTime(new Date());
            tbDataColumnAppealDO.setDeleted(false);
            tbDataColumnAppealDO.setStoreId(dataStaTableColumnDO.getStoreId());
            tbDataColumnAppealDO.setPictures(dataColumnAppealDTO.getPictures());
            tbDataColumnAppealDO.setVideos(dataColumnAppealDTO.getVideos());
            tbDataColumnAppealDao.insertSelective(tbDataColumnAppealDO, eid);
            checkVideoHandel(eid, tbDataColumnAppealDO);
            tbDataColumnAppealDao.updateByPrimaryKeySelective(tbDataColumnAppealDO, eid);
        });
        //插入申诉审批人待办
        ScSafetyCheckFlowDO scSafetyCheckFlowDO = safetyCheckFlowDao.getByBusinessId(eid, businessId.get());
        String appealReviewUser = scSafetyCheckFlowDO.getAppealReviewUser();
        if (StringUtils.isNotBlank(appealReviewUser)) {
            List<String> appealReviewUserList = JSONObject.parseArray(appealReviewUser, String.class);
            jmsTaskService.sendSafetyCheckMessage(eid, businessId.get(), FoodCheckNoticeEnum.APPEALAPPROVE.getNode(), appealReviewUserList);
            List<String> hasUserIdList = safetyCheckUpcomingDao.getByUserIdList(eid, businessId.get(), appealReviewUserList);
            if (CollectionUtils.isNotEmpty(hasUserIdList)) {
                appealReviewUserList.removeAll(hasUserIdList);
            }
            if (CollectionUtils.isNotEmpty(appealReviewUserList)) {
                TbPatrolStoreRecordDO tbPatrolStoreRecordDO =
                        tbPatrolStoreRecordMapper.selectById(eid, businessId.get());
                List<ScSafetyCheckUpcomingDO> upcomingDOList = appealReviewUserList.stream().map(nextNodeUser -> ScSafetyCheckUpcomingDO.builder().businessId(businessId.get()).storeId(tbPatrolStoreRecordDO.getStoreId()).userId(nextNodeUser).cycleCount(scSafetyCheckFlowDO.getCycleCount())
                        .status(UnifyStatus.ONGOING.getCode()).nodeNo(Constants.NODE_NO_APPEAL_APPROVE).createTime(new Date()).createUserId(userId).build()).collect(Collectors.toList());
                safetyCheckUpcomingDao.batchInsert(eid, upcomingDOList);

            }
        }

        TbPatrolStoreHistoryDo tbPatrolStoreHistoryDo = new TbPatrolStoreHistoryDo();
        tbPatrolStoreHistoryDo.setBusinessId(businessId.get());
        tbPatrolStoreHistoryDo.setCreateTime(new Date(System.currentTimeMillis()));
        tbPatrolStoreHistoryDo.setUpdateTime(new Date(System.currentTimeMillis()));
        tbPatrolStoreHistoryDo.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
        tbPatrolStoreHistoryDo.setActionKey("");
        tbPatrolStoreHistoryDo.setDeleted(false);
        tbPatrolStoreHistoryDo.setOperateType(PatrolStoreConstant.PatrolStoreOperateTypeConstant.APPEAL);
        tbPatrolStoreHistoryDo.setOperateUserId(userId);
        tbPatrolStoreHistoryDo.setSubTaskId(0L);
        tbPatrolStoreHistoryDo.setOperateUserName(userName);
        tbPatrolStoreHistoryDo.setRemark("");
        patrolStoreHistoryMapper.insertPatrolStoreHistory(eid, tbPatrolStoreHistoryDo);
    }

    /**
     * 视频转码
     *
     * @param enterpriseId         企业id
     * @param tbDataColumnAppealDO 申诉记录
     */
    private void checkVideoHandel(String enterpriseId, TbDataColumnAppealDO tbDataColumnAppealDO) {
        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(tbDataColumnAppealDO.getVideos(), SmallVideoInfoDTO.class);
        if (smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())) {
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                //如果转码完成
                if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    continue;
                }
                callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if (StringUtils.isNotBlank(callbackCache)) {
                    smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                    if (smallVideoCache != null && smallVideoCache.getStatus() != null && smallVideoCache.getStatus() >= 3) {
                        BeanUtils.copyProperties(smallVideoCache, smallVideo);
                    } else {
                        smallVideoParam = new SmallVideoParam();
                        setNotCompleteCache(smallVideoParam, smallVideo, tbDataColumnAppealDO.getId(), enterpriseId);
                    }
                } else {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam, smallVideo, tbDataColumnAppealDO.getId(), enterpriseId);
                }
            }
            tbDataColumnAppealDO.setVideos(JSONObject.toJSONString(smallVideoInfo));
        }
    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     */
    private void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, Long businessId, String enterpriseId) {
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.DATA_COLUMN_APPEAL.getValue());
        smallVideoParam.setBusinessId(businessId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtil.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }

    @Override
    public List<TbDataColumnAppealListDTO> appealList(String eid, String userId, Long businessId) {
        List<TbDataColumnAppealDO> dataColumnAppealDOList = tbDataColumnAppealDao.selectListByBusinessId(eid, businessId);
        if (CollectionUtils.isEmpty(dataColumnAppealDOList)) {
            return new ArrayList<>();
        }
        Set<String> userIdList = new HashSet<>();
        Set<Long> metaStaColumnIdList = new HashSet<>();
        Set<Long> dataStaColumnIdList = new HashSet<>();
        Set<Long> metaTableIds = new HashSet<>();
        dataColumnAppealDOList.forEach(dataColumnAppealDO -> {
            metaStaColumnIdList.add(dataColumnAppealDO.getMetaColumnId());
            dataStaColumnIdList.add(dataColumnAppealDO.getDataColumnId());
            userIdList.add(dataColumnAppealDO.getAppealUserId());
            metaTableIds.add(dataColumnAppealDO.getMetaTableId());
            if (StringUtils.isNotBlank(dataColumnAppealDO.getAppealActualReviewUserId())) {
                userIdList.add(dataColumnAppealDO.getAppealActualReviewUserId());
            }
        });
        List<TbMetaStaTableColumnDO> metaStaColumnList =
                tbMetaStaTableColumnMapper.selectByIds(eid, new ArrayList<>(metaStaColumnIdList));
        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList =
                tbMetaColumnResultMapper.selectByColumnIds(eid, new ArrayList<>(metaStaColumnIdList));
        List<TbMetaColumnResultDTO> columnResultDTOList = metaTableService.getMetaColumnResultList(eid, columnResultDOList);
        Map<Long, List<TbMetaColumnResultDTO>> columnIdResultMap =
                columnResultDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDTO::getMetaColumnId));

        //不合格原因
        List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByColumnIdList(eid, new ArrayList<>(metaStaColumnIdList));
        Map<Long, List<TbMetaColumnReasonDTO>> columnIdReasonMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnReasonDTOList)) {
            columnIdReasonMap =
                    columnReasonDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnReasonDTO::getMetaColumnId));
        }
        Map<Long, List<TbMetaColumnReasonDTO>> finalColumnIdReasonMap = columnIdReasonMap;
        List<MetaStaColumnVO> metaStaColumnVOList = metaStaColumnList.stream().map(a -> {
            MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
            BeanUtils.copyProperties(a, metaStaColumnVO);
            //如果是采集项
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())) {
                metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
            }
            metaStaColumnVO
                    .setColumnResultList(columnIdResultMap.getOrDefault(a.getId(), new ArrayList<>()));
            // 填充结果项
            metaStaColumnVO.fillColumnResultList();
            metaStaColumnVO.setColumnReasonList(finalColumnIdReasonMap.get(a.getId()));
            return metaStaColumnVO;
        }).collect(Collectors.toList());
        Map<Long, MetaStaColumnVO> metaStaColumnMap = metaStaColumnVOList.stream().collect(Collectors.toMap(MetaStaColumnVO::getId, metaStaColumnVO -> metaStaColumnVO));


        List<TbDataStaTableColumnDO> dataStaTableColumnDOList = tbDataStaTableColumnMapper.selectByIds(eid, new ArrayList<>(dataStaColumnIdList));

        dataStaTableColumnDOList.forEach(dataStaColumn -> {
            if (StringUtils.isNotBlank(dataStaColumn.getHandlerUserId())) {
                userIdList.add(dataStaColumn.getHandlerUserId());
            }
        });
        Map<String, String> userMap = enterpriseUserDao.getUserNameMap(eid, new ArrayList<>(userIdList));

        Map<Long, TbDataColumnCommentAppealVO> commentAppealMap = scSafetyCheckFlowService.getLatestCommentAppealInfo(eid, businessId, new ArrayList<>(dataStaColumnIdList));

        List<TbDataStaTableColumnVO> tbDataStaTableColumnVOList = dataStaTableColumnDOList.stream().map(a -> {
            TbDataStaTableColumnVO tbDataStaTableColumnVO = new TbDataStaTableColumnVO();
            BeanUtils.copyProperties(a, tbDataStaTableColumnVO);
            TbDataColumnCommentAppealVO commentAppealVO = commentAppealMap.get(a.getId());
            tbDataStaTableColumnVO.setCommentAppealVO(commentAppealVO);
            return tbDataStaTableColumnVO;
        }).collect(Collectors.toList());

        Map<Long, TbDataStaTableColumnVO> dataStaTableColumnMap = tbDataStaTableColumnVOList.stream().collect(Collectors.toMap(TbDataStaTableColumnVO::getId, tbDataStaTableColumnVO -> tbDataStaTableColumnVO));
        // Map:metaTableId->metaTableDO
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(eid, new ArrayList<>(metaTableIds));


        Map<Long, TbMetaTableInfoVO> idMetaTableMap =
                tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, data ->
                        TbMetaTableInfoVO.builder().id(data.getId()).createTime(data.getCreateTime()).editTime(data.getEditTime())
                                .tableName(data.getTableName()).description(data.getDescription()).createUserId(data.getCreateUserId())
                                .createUserName(data.getCreateUserName()).supportScore(data.getSupportScore()).locked(data.getLocked())
                                .active(data.getActive()).tableType(data.getTableType()).shareGroup(data.getShareGroup()).deleted(data.getDeleted())
                                .editUserId(data.getEditUserId()).editUserName(data.getEditUserName()).shareGroupName(data.getShareGroupName())
                                .resultShareGroup(data.getResultShareGroup()).resultShareGroup(data.getResultShareGroup()).resultShareGroupName(data.getResultShareGroupName())
                                .levelRule(data.getLevelRule()).levelInfo(data.getLevelInfo()).storeSceneId(data.getStoreSceneId()).defaultResultColumn(data.getDefaultResultColumn())
                                .noApplicableRule(data.getNoApplicableRule()).viewResultAuth(true)
                                .categoryNameList(CollectionUtils.isEmpty(JSONObject.parseArray(data.getCategoryNameList(), String.class)) ||
                                        CollectionUtils.isEmpty(new ArrayList<>()) ? null :
                                        ListUtils.retainAll(JSONObject.parseArray(data.getCategoryNameList(), String.class), new ArrayList<>()))
                                .orderNum(data.getOrderNum()).status(data.getStatus()).totalScore(data.getTotalScore()).tableProperty(data.getTableProperty())
                                .useRange(data.getUseRange())
                                .resultViewRange(data.getResultViewRange())
                                .build(), (a, b) -> a));

        List<TbDataColumnAppealListDTO> result = new ArrayList<>();
        dataColumnAppealDOList.forEach(tbDataColumnAppealDO -> {
            TbDataColumnAppealListDTO appealListDTO = new TbDataColumnAppealListDTO();
            appealListDTO.setId(tbDataColumnAppealDO.getId());
            appealListDTO.setBusinessId(tbDataColumnAppealDO.getBusinessId());
            appealListDTO.setDataTableId(tbDataColumnAppealDO.getDataTableId());
            appealListDTO.setMetaTableId(tbDataColumnAppealDO.getMetaTableId());
            appealListDTO.setMetaColumnId(tbDataColumnAppealDO.getMetaColumnId());
            appealListDTO.setDataColumnId(tbDataColumnAppealDO.getDataColumnId());
            appealListDTO.setMetaColumnName(tbDataColumnAppealDO.getMetaColumnName());
            appealListDTO.setAppealStatus(tbDataColumnAppealDO.getAppealStatus());
            appealListDTO.setAppealContent(tbDataColumnAppealDO.getAppealContent());
            appealListDTO.setAppealRemark(tbDataColumnAppealDO.getAppealRemark());
            appealListDTO.setAppealUserId(tbDataColumnAppealDO.getAppealUserId());
            if (StringUtils.isNotBlank(tbDataColumnAppealDO.getAppealUserId())) {
                appealListDTO.setAppealUserName(userMap.get(tbDataColumnAppealDO.getAppealUserId()));
            }
            appealListDTO.setAppealTime(tbDataColumnAppealDO.getAppealTime());
            appealListDTO.setAppealResult(tbDataColumnAppealDO.getAppealResult());
            appealListDTO.setAppealReviewRemark(tbDataColumnAppealDO.getAppealReviewRemark());
            appealListDTO.setAppealActualReviewUserId(tbDataColumnAppealDO.getAppealActualReviewUserId());
            if (StringUtils.isNotBlank(tbDataColumnAppealDO.getAppealActualReviewUserId())) {
                appealListDTO.setAppealActualReviewUserName(userMap.get(tbDataColumnAppealDO.getAppealActualReviewUserId()));
            }
            appealListDTO.setAppealReviewTime(tbDataColumnAppealDO.getAppealReviewTime());
            appealListDTO.setStoreId(tbDataColumnAppealDO.getStoreId());
            TbDataStaTableColumnVO dataStaTableColumnVO = dataStaTableColumnMap.get(tbDataColumnAppealDO.getDataColumnId());
            if (StringUtils.isNotBlank(dataStaTableColumnVO.getHandlerUserId())) {
                dataStaTableColumnVO.setHandlerUserName(userMap.get(dataStaTableColumnVO.getHandlerUserId()));
            }
            appealListDTO.setDataStaTableColumnVO(dataStaTableColumnVO);
            appealListDTO.setMetaStaColumnVO(metaStaColumnMap.get(tbDataColumnAppealDO.getMetaColumnId()));
            appealListDTO.setMetaTable(idMetaTableMap.get(tbDataColumnAppealDO.getMetaTableId()));
            appealListDTO.setPictures(tbDataColumnAppealDO.getPictures());
            appealListDTO.setVideos(tbDataColumnAppealDO.getVideos());
            result.add(appealListDTO);
        });
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void appealApprove(String eid, String userId, String userName, DataColumnAppealParam dataColumnAppealParam, String dingCorpId,String appType) {
        TbDataColumnAppealDO tbDataColumnAppealDO = tbDataColumnAppealDao.selectByPrimaryKey(dataColumnAppealParam.getAppealId(), eid);
        if (tbDataColumnAppealDO == null) {
            throw new ServiceException("申诉记录不存在");
        }
        if (!UnifyStatus.ONGOING.getCode().equals(tbDataColumnAppealDO.getAppealStatus())) {
            throw new ServiceException("申诉审批已完成，无法再次审批");
        }

        TbDataStaTableColumnDO dataStaTableColumnDO = dataStaTableColumnMapper.selectById(eid, tbDataColumnAppealDO.getDataColumnId());
        //审批通过，更新数据表列信息
        if (PatrolStoreConstant.ActionKeyConstant.PASS.equals(dataColumnAppealParam.getAppealResult())) {
            if (dataStaTableColumnDO.getCheckPics().equals(dataColumnAppealParam.getCheckPics())
                    && dataStaTableColumnDO.getCheckVideo().equals(dataColumnAppealParam.getCheckVideo())
                    && dataStaTableColumnDO.getCheckText().equals(dataColumnAppealParam.getCheckText())
                    && (dataColumnAppealParam.getCheckScore() != null && dataStaTableColumnDO.getCheckScore().compareTo(dataColumnAppealParam.getCheckScore()) == 0)
                    && dataStaTableColumnDO.getCheckResult().equals(dataColumnAppealParam.getCheckResult())
                    && dataStaTableColumnDO.getCheckResultId().equals(dataColumnAppealParam.getCheckResultId())
                    && dataStaTableColumnDO.getCheckResultReason().equals(dataColumnAppealParam.getCheckResultReason())
                    && dataStaTableColumnDO.getScoreTimes().compareTo(dataColumnAppealParam.getScoreTimes()) == 0
                    && dataStaTableColumnDO.getAwardTimes().compareTo(dataColumnAppealParam.getAwardTimes()) == 0) {
                throw new ServiceException("没有修改巡店内容，不能申诉审核同意");
            }
        }

        TbDataColumnAppealDO update = new TbDataColumnAppealDO();
        update.setId(tbDataColumnAppealDO.getId());
        update.setAppealStatus(UnifyStatus.COMPLETE.getCode());
        update.setAppealResult(dataColumnAppealParam.getAppealResult());
        update.setAppealReviewRemark(dataColumnAppealParam.getAppealReviewRemark());
        update.setAppealActualReviewUserId(userId);
        update.setAppealReviewTime(new Date());
        update.setUpdateTime(new Date());
        tbDataColumnAppealDao.updateByPrimaryKeySelective(update, eid);

        TbPatrolStoreHistoryDo tbPatrolStoreHistoryDo = new TbPatrolStoreHistoryDo();
        tbPatrolStoreHistoryDo.setBusinessId(tbDataColumnAppealDO.getBusinessId());
        tbPatrolStoreHistoryDo.setCreateTime(new Date(System.currentTimeMillis()));
        tbPatrolStoreHistoryDo.setUpdateTime(new Date(System.currentTimeMillis()));
        tbPatrolStoreHistoryDo.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
        tbPatrolStoreHistoryDo.setActionKey(dataColumnAppealParam.getAppealResult());
        tbPatrolStoreHistoryDo.setDeleted(false);
        tbPatrolStoreHistoryDo.setOperateType(PatrolStoreConstant.PatrolStoreOperateTypeConstant.APPEAL_APPROVE);
        tbPatrolStoreHistoryDo.setOperateUserId(userId);
        tbPatrolStoreHistoryDo.setSubTaskId(0L);
        tbPatrolStoreHistoryDo.setOperateUserName(userName);
        tbPatrolStoreHistoryDo.setRemark(dataColumnAppealParam.getAppealReviewRemark());
        patrolStoreHistoryMapper.insertPatrolStoreHistory(eid, tbPatrolStoreHistoryDo);

        TbPatrolStoreRecordDO tbPatrolStoreRecordDO =
                tbPatrolStoreRecordMapper.selectById(eid, tbDataColumnAppealDO.getBusinessId());

        //审批通过，更新数据表列信息
        if (PatrolStoreConstant.ActionKeyConstant.PASS.equals(dataColumnAppealParam.getAppealResult())) {
            dataStaTableColumnDO.setCheckResult(dataColumnAppealParam.getCheckResult());
            dataStaTableColumnDO.setCheckResultId(dataColumnAppealParam.getCheckResultId());
            dataStaTableColumnDO.setCheckResultName(dataColumnAppealParam.getCheckResultName());
            dataStaTableColumnDO.setCheckPics(dataColumnAppealParam.getCheckPics());
            dataStaTableColumnDO.setCheckVideo(dataColumnAppealParam.getCheckVideo());
            dataStaTableColumnDO.setCheckText(dataColumnAppealParam.getCheckText());
            dataStaTableColumnDO.setCheckScore(dataColumnAppealParam.getCheckScore());
            dataStaTableColumnDO.setScoreTimes(dataColumnAppealParam.getScoreTimes());
            dataStaTableColumnDO.setAwardTimes(dataColumnAppealParam.getAwardTimes());
            dataStaTableColumnDO.setCheckResultReason(dataColumnAppealParam.getCheckResultReason());
            dataStaTableColumnDO.setHandlerUserId(userId);
            dataStaTableColumnDO.setPatrolStoreTime(new Date());
            //视频转码
            if(StringUtils.isNotBlank(dataStaTableColumnDO.getCheckVideo())){
                patrolStoreService.checkVideoHandel(Collections.singletonList(dataStaTableColumnDO), eid);
            }
            dataStaTableColumnMapper.batchUpdate(eid, dataStaTableColumnDO.getBusinessId(), PATROL_STORE,
                    dataStaTableColumnDO.getDataTableId(), Collections.singletonList(dataStaTableColumnDO), false);


            TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(eid, dataStaTableColumnDO.getMetaTableId());
            //重新计算得分
            patrolStoreService.countScore(eid, tbPatrolStoreRecordDO, metaTableDO, dataStaTableColumnDO.getDataTableId());

            //取最新数据
            dataStaTableColumnDO = dataStaTableColumnMapper.selectById(eid, tbDataColumnAppealDO.getDataColumnId());


            //存储历史记录
            TbDataColumnHistoryDO dataColumnHistoryDO = new TbDataColumnHistoryDO();
            dataColumnHistoryDO.setId(dataStaTableColumnDO.getId());
            dataColumnHistoryDO.setBusinessId(dataStaTableColumnDO.getBusinessId());
            dataColumnHistoryDO.setHistoryId(tbPatrolStoreHistoryDo.getBusinessId());
            dataColumnHistoryDO.setOperateType(DataColumnOperateTypeEnum.APPEAL.getCode());
            dataColumnHistoryDO.setOperateUserId(userId);
            dataColumnHistoryDO.setOperateUserName(userName);
            dataColumnHistoryDO.setDataTableId(dataStaTableColumnDO.getDataTableId());
            dataColumnHistoryDO.setMetaTableId(dataStaTableColumnDO.getMetaTableId());
            dataColumnHistoryDO.setMetaColumnId(dataStaTableColumnDO.getMetaColumnId());
            dataColumnHistoryDO.setDataColumnId(dataStaTableColumnDO.getId());
            dataColumnHistoryDO.setMetaColumnName(dataStaTableColumnDO.getMetaColumnName());
            dataColumnHistoryDO.setDescription(dataStaTableColumnDO.getDescription());
            dataColumnHistoryDO.setCategoryName(dataStaTableColumnDO.getCategoryName());
            dataColumnHistoryDO.setCheckResult(dataStaTableColumnDO.getCheckResult());
            dataColumnHistoryDO.setCheckResultId(dataStaTableColumnDO.getCheckResultId());
            dataColumnHistoryDO.setCheckResultName(dataStaTableColumnDO.getCheckResultName());
            dataColumnHistoryDO.setCheckText(dataStaTableColumnDO.getCheckText());
            dataColumnHistoryDO.setCheckScore(dataStaTableColumnDO.getCheckScore());
            dataColumnHistoryDO.setCheckVideo(dataStaTableColumnDO.getCheckVideo());
            dataColumnHistoryDO.setRewardPenaltMoney(dataStaTableColumnDO.getRewardPenaltMoney());
            dataColumnHistoryDO.setCheckResultReason(dataStaTableColumnDO.getCheckResultReason());
            dataColumnHistoryDO.setAppealContent(tbDataColumnAppealDO.getAppealContent());
            dataColumnHistoryDO.setAppealRemark(tbDataColumnAppealDO.getAppealRemark());
            dataColumnHistoryDO.setAppealUserId(tbDataColumnAppealDO.getAppealUserId());
            dataColumnHistoryDO.setAppealTime(tbDataColumnAppealDO.getAppealTime());
            dataColumnHistoryDO.setAppealResult(dataColumnAppealParam.getAppealResult());
            dataColumnHistoryDO.setAppealReviewRemark(dataColumnAppealParam.getAppealReviewRemark());
            dataColumnHistoryDO.setAppealActualReviewUserId(userId);
            dataColumnHistoryDO.setAppealReviewTime(new Date());
            dataColumnHistoryDO.setCreateTime(new Date());
            dataColumnHistoryDO.setUpdateTime(new Date());
            dataColumnHistoryDO.setDeleted(false);
            dataColumnHistoryDO.setCheckPics(dataStaTableColumnDO.getCheckPics());
            dataColumnHistoryDO.setStoreId(dataStaTableColumnDO.getStoreId());
            dataColumnHistoryDO.setAwardTimes(dataColumnAppealParam.getAwardTimes());
            dataColumnHistoryDO.setScoreTimes(dataColumnAppealParam.getScoreTimes());
            dataColumnHistoryDO.setWeightPercent(dataStaTableColumnDO.getWeightPercent());
            dataColumnHistoryDao.insertSelective(dataColumnHistoryDO, eid);
            //审核通过次数
            patrolStoreRecordInfoMapper.updateSafetyCheckAppealPassNum(eid, tbPatrolStoreRecordDO.getId());
        }else {
            //审核通过次数
            patrolStoreRecordInfoMapper.updateSafetyCheckAppealRejectNum(eid, tbPatrolStoreRecordDO.getId());
        }
        //清理申诉审批人待办(所有申诉处理完，以及存在待办)
        ScSafetyCheckFlowDO scSafetyCheckFlowDO = safetyCheckFlowDao.getByBusinessId(eid, tbDataColumnAppealDO.getBusinessId());
        List<TbDataColumnAppealDO> dataColumnAppealDOList = tbDataColumnAppealDao.selectListByBusinessIdAndStatus(eid, tbDataColumnAppealDO.getBusinessId(), UnifyStatus.ONGOING.getCode());
        if (CollectionUtils.isEmpty(dataColumnAppealDOList)) {
            safetyCheckUpcomingDao.updateUpcomingStatus(eid, UnifyStatus.COMPLETE.getCode(), tbDataColumnAppealDO.getBusinessId(),
                    scSafetyCheckFlowDO.getCycleCount(), Constants.NODE_NO_APPEAL_APPROVE);
            // 取消申诉审核待办
            String appealReviewUser = scSafetyCheckFlowDO.getAppealReviewUser();
            List<String> appealReviewUserList = JSONObject.parseArray(appealReviewUser, String.class);
            cancelUpcoming(eid, dingCorpId, appType, tbDataColumnAppealDO.getBusinessId(), scSafetyCheckFlowDO.getCycleCount(), Constants.NODE_NO_APPEAL_APPROVE, appealReviewUserList);
        }

        //校验发起工单
        if (CheckResultEnum.FAIL.getCode().equals(dataColumnAppealParam.getCheckResult())) {
            try {
                // 稽稽核完成修改结果对新产生的不合格项发起工单
                simpleMessageService.send(JSONObject.toJSONString(new PatrolStoreScoreMsgDTO(eid, tbDataColumnAppealDO.getBusinessId(), 0L, userId,
                                userName, true)),
                        RocketMqTagEnum.PATROL_STORE_SCORE_COUNT_QUEUE, System.currentTimeMillis() + 1000);
            } catch (Exception e) {
                log.error("完成申诉审批修改结果对新产生的不合格项发起工单 businessId:{}", tbDataColumnAppealDO.getBusinessId(), e);
            }
        }

        if(StringUtils.isNotBlank(scSafetyCheckFlowDO.getCcUserInfo())){
            JSONObject jsonObject = JSONObject.parseObject(scSafetyCheckFlowDO.getCcUserInfo());
            List<String> appealResultCcUserList = JSONObject.parseArray(jsonObject.getString("appealResultCcInfo"), String.class);
            if(CollectionUtils.isNotEmpty(appealResultCcUserList)){
                jmsTaskService.sendSafetyCheckMessage(eid, tbDataColumnAppealDO.getBusinessId(), FoodCheckNoticeEnum.APPEALRESULTCCINFO.getNode(), appealResultCcUserList);
            }
        }

    }

    @Override
    public List<TbDataColumnAppealHistoryDTO> appealHistoryList(String eid, Long dataColumnId) {
        List<TbDataColumnAppealDO> dataColumnAppealDOList = tbDataColumnAppealDao.selectListByDataColumnId(eid, dataColumnId);
        if (CollectionUtils.isEmpty(dataColumnAppealDOList)) {
            return Collections.emptyList();
        }
        Set<String> userIdSet = new HashSet<>();
        dataColumnAppealDOList.forEach(dataColumnAppealDO -> {
            userIdSet.add(dataColumnAppealDO.getAppealUserId());
            if (StringUtils.isNotBlank(dataColumnAppealDO.getAppealActualReviewUserId())) {
                userIdSet.add(dataColumnAppealDO.getAppealActualReviewUserId());
            }
        });
        Map<String, String> userMap = enterpriseUserDao.getUserNameMap(eid, new ArrayList<>(userIdSet));
        List<TbDataColumnAppealHistoryDTO> resultList = new ArrayList<>();
        dataColumnAppealDOList.forEach(dataColumnAppealDO -> {
            TbDataColumnAppealHistoryDTO tbDataColumnAppealHistoryDTO = new TbDataColumnAppealHistoryDTO();
            tbDataColumnAppealHistoryDTO.setId(dataColumnAppealDO.getId());
            tbDataColumnAppealHistoryDTO.setBusinessId(dataColumnAppealDO.getBusinessId());
            tbDataColumnAppealHistoryDTO.setDataTableId(dataColumnAppealDO.getDataTableId());
            tbDataColumnAppealHistoryDTO.setMetaTableId(dataColumnAppealDO.getMetaTableId());
            tbDataColumnAppealHistoryDTO.setMetaColumnId(dataColumnAppealDO.getMetaColumnId());
            tbDataColumnAppealHistoryDTO.setDataColumnId(dataColumnAppealDO.getDataColumnId());
            tbDataColumnAppealHistoryDTO.setMetaColumnName(dataColumnAppealDO.getMetaColumnName());
            tbDataColumnAppealHistoryDTO.setAppealStatus(dataColumnAppealDO.getAppealStatus());
            tbDataColumnAppealHistoryDTO.setAppealContent(dataColumnAppealDO.getAppealContent());
            tbDataColumnAppealHistoryDTO.setAppealRemark(dataColumnAppealDO.getAppealRemark());
            tbDataColumnAppealHistoryDTO.setAppealUserId(dataColumnAppealDO.getAppealUserId());
            if (StringUtils.isNotBlank(dataColumnAppealDO.getAppealUserId())) {
                tbDataColumnAppealHistoryDTO.setAppealUserName(userMap.get(dataColumnAppealDO.getAppealUserId()));
            }
            tbDataColumnAppealHistoryDTO.setAppealTime(dataColumnAppealDO.getAppealTime());
            tbDataColumnAppealHistoryDTO.setAppealResult(dataColumnAppealDO.getAppealResult());
            tbDataColumnAppealHistoryDTO.setAppealReviewRemark(dataColumnAppealDO.getAppealReviewRemark());
            tbDataColumnAppealHistoryDTO.setAppealActualReviewUserId(dataColumnAppealDO.getAppealActualReviewUserId());
            if (StringUtils.isNotBlank(dataColumnAppealDO.getAppealActualReviewUserId())) {
                tbDataColumnAppealHistoryDTO.setAppealActualReviewUserName(userMap.get(dataColumnAppealDO.getAppealActualReviewUserId()));
            }
            tbDataColumnAppealHistoryDTO.setAppealReviewTime(dataColumnAppealDO.getAppealReviewTime());
            tbDataColumnAppealHistoryDTO.setStoreId(dataColumnAppealDO.getStoreId());
            resultList.add(tbDataColumnAppealHistoryDTO);
        });
        return resultList;
    }

    @Override
    public TbDataColumnAppealListDTO appealDetail(String eid, String userId, Long appealId) {
        TbDataColumnAppealDO tbDataColumnAppealDO = tbDataColumnAppealDao.selectByPrimaryKey(appealId, eid);
        if (tbDataColumnAppealDO == null) {
            throw new ServiceException("申诉记录不存在");
        }
        Set<String> userIdList = new HashSet<>();
        Set<Long> metaStaColumnIdList = new HashSet<>();
        Set<Long> dataStaColumnIdList = new HashSet<>();

        metaStaColumnIdList.add(tbDataColumnAppealDO.getMetaColumnId());
        dataStaColumnIdList.add(tbDataColumnAppealDO.getDataColumnId());
        userIdList.add(tbDataColumnAppealDO.getAppealUserId());
        if (StringUtils.isNotBlank(tbDataColumnAppealDO.getAppealActualReviewUserId())) {
            userIdList.add(tbDataColumnAppealDO.getAppealActualReviewUserId());
        }

        List<TbMetaStaTableColumnDO> metaStaColumnList =
                tbMetaStaTableColumnMapper.selectByIds(eid, new ArrayList<>(metaStaColumnIdList));
        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList =
                tbMetaColumnResultMapper.selectByColumnIds(eid, new ArrayList<>(metaStaColumnIdList));
        List<TbMetaColumnResultDTO> columnResultDTOList = metaTableService.getMetaColumnResultList(eid, columnResultDOList);
        Map<Long, List<TbMetaColumnResultDTO>> columnIdResultMap =
                columnResultDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDTO::getMetaColumnId));

        //不合格原因
        List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByColumnIdList(eid, new ArrayList<>(metaStaColumnIdList));
        Map<Long, List<TbMetaColumnReasonDTO>> columnIdReasonMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnReasonDTOList)) {
            columnIdReasonMap =
                    columnReasonDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnReasonDTO::getMetaColumnId));
        }
        Map<Long, List<TbMetaColumnReasonDTO>> finalColumnIdReasonMap = columnIdReasonMap;
        List<MetaStaColumnVO> metaStaColumnVOList = metaStaColumnList.stream().map(a -> {
            MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
            BeanUtils.copyProperties(a, metaStaColumnVO);
            //如果是采集项
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())) {
                metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
            }
            metaStaColumnVO
                    .setColumnResultList(columnIdResultMap.getOrDefault(a.getId(), new ArrayList<>()));
            // 填充结果项
            metaStaColumnVO.fillColumnResultList();
            metaStaColumnVO.setColumnReasonList(finalColumnIdReasonMap.get(a.getId()));
            return metaStaColumnVO;
        }).collect(Collectors.toList());
        Map<Long, MetaStaColumnVO> metaStaColumnMap = metaStaColumnVOList.stream().collect(Collectors.toMap(MetaStaColumnVO::getId, metaStaColumnVO -> metaStaColumnVO));

        Map<String, String> userMap = enterpriseUserDao.getUserNameMap(eid, new ArrayList<>(userIdList));

        List<TbDataStaTableColumnDO> dataStaTableColumnDOList = tbDataStaTableColumnMapper.selectByIds(eid, new ArrayList<>(dataStaColumnIdList));

        Map<Long, TbDataColumnCommentAppealVO> commentAppealMap = scSafetyCheckFlowService.getLatestCommentAppealInfo(eid, tbDataColumnAppealDO.getBusinessId(), new ArrayList<>(dataStaColumnIdList));

        List<TbDataStaTableColumnVO> tbDataStaTableColumnVOList = dataStaTableColumnDOList.stream().map(a -> {
            TbDataStaTableColumnVO tbDataStaTableColumnVO = new TbDataStaTableColumnVO();
            BeanUtils.copyProperties(a, tbDataStaTableColumnVO);
            TbDataColumnCommentAppealVO commentAppealVO = commentAppealMap.get(a.getId());
            tbDataStaTableColumnVO.setCommentAppealVO(commentAppealVO);
            return tbDataStaTableColumnVO;
        }).collect(Collectors.toList());

        Map<Long, TbDataStaTableColumnVO> dataStaTableColumnMap = tbDataStaTableColumnVOList.stream().collect(Collectors.toMap(TbDataStaTableColumnVO::getId, tbDataStaTableColumnVO -> tbDataStaTableColumnVO));

        TbDataColumnAppealListDTO appealListDTO = new TbDataColumnAppealListDTO();
        appealListDTO.setId(tbDataColumnAppealDO.getId());
        appealListDTO.setBusinessId(tbDataColumnAppealDO.getBusinessId());
        appealListDTO.setDataTableId(tbDataColumnAppealDO.getDataTableId());
        appealListDTO.setMetaTableId(tbDataColumnAppealDO.getMetaTableId());
        appealListDTO.setMetaColumnId(tbDataColumnAppealDO.getMetaColumnId());
        appealListDTO.setDataColumnId(tbDataColumnAppealDO.getDataColumnId());
        appealListDTO.setMetaColumnName(tbDataColumnAppealDO.getMetaColumnName());
        appealListDTO.setAppealStatus(tbDataColumnAppealDO.getAppealStatus());
        appealListDTO.setAppealContent(tbDataColumnAppealDO.getAppealContent());
        appealListDTO.setAppealRemark(tbDataColumnAppealDO.getAppealRemark());
        appealListDTO.setAppealUserId(tbDataColumnAppealDO.getAppealUserId());
        if (StringUtils.isNotBlank(tbDataColumnAppealDO.getAppealUserId())) {
            appealListDTO.setAppealUserName(userMap.get(tbDataColumnAppealDO.getAppealUserId()));
        }
        appealListDTO.setAppealTime(tbDataColumnAppealDO.getAppealTime());
        appealListDTO.setAppealResult(tbDataColumnAppealDO.getAppealResult());
        appealListDTO.setAppealReviewRemark(tbDataColumnAppealDO.getAppealReviewRemark());
        appealListDTO.setAppealActualReviewUserId(tbDataColumnAppealDO.getAppealActualReviewUserId());
        if (StringUtils.isNotBlank(tbDataColumnAppealDO.getAppealActualReviewUserId())) {
            appealListDTO.setAppealActualReviewUserName(userMap.get(tbDataColumnAppealDO.getAppealActualReviewUserId()));
        }
        appealListDTO.setAppealReviewTime(tbDataColumnAppealDO.getAppealReviewTime());
        appealListDTO.setStoreId(tbDataColumnAppealDO.getStoreId());
        appealListDTO.setDataStaTableColumnVO(dataStaTableColumnMap.get(tbDataColumnAppealDO.getDataColumnId()));
        appealListDTO.setMetaStaColumnVO(metaStaColumnMap.get(tbDataColumnAppealDO.getMetaColumnId()));
        appealListDTO.setPictures(tbDataColumnAppealDO.getPictures());
        appealListDTO.setVideos(tbDataColumnAppealDO.getVideos());
        return appealListDTO;
    }
    // 取消申诉审核待办
    public void cancelUpcoming(String enterpriseId, String dingCorpId,String appType, Long businessId, Integer cycleCount, String currentNodeNo, List<String> userIdList) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.FOODCHECK.getDesc() + "_" + businessId + "_" + cycleCount + "_" + currentNodeNo);
        jsonObject.put("appType",appType);
        jsonObject.put("userIds", userIdList);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

}
