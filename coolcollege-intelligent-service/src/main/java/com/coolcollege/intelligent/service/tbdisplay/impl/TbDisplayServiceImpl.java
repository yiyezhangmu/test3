package com.coolcollege.intelligent.service.tbdisplay.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.SendResult;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.tbdisplay.*;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.mapper.mq.MqMessageDAO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.tbdisplay.*;
import com.coolcollege.intelligent.model.tbdisplay.constant.TbDisplayConstant;
import com.coolcollege.intelligent.model.tbdisplay.param.*;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayColumnReportVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableDataContentVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.vo.GetTaskByPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.ai.DashScopeService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author : WXP
 * @version : 1.0
 * @Description : 陈列任务操作
 * @date ：2021/03/05
 */
@Service
@Slf4j
public class TbDisplayServiceImpl implements TbDisplayService {
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private TbDisplayTableRecordServiceImpl tbDisplayTableRecordService;

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Resource
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;

    @Resource
    private TbDisplayHistoryMapper tbDisplayHistoryMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Lazy
    @Autowired
    private UnifyTaskService unifyTaskService;

    @Resource
    TbDisplayTableDataContentMapper tbDisplayTableDataContentMapper;

    @Resource
    TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    @Autowired
    private ImportTaskService importTaskService;

    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;

    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private UnifyTaskParentService unifyTaskParentService;
    @Resource
    private MqMessageDAO mqMessageDAO;
    @Resource
    private DashScopeService dashScopeService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor queryExecutor;

    @Override
    public TbDisplayTableRecordDO tbDisplayColumnHandle(String enterpriseId, TbDisplayHandleParam handleParam, CurrentUser user, boolean isAiCheck) {
        // 参数校验
        TbDisplayTableRecordDO tbDisplayTableRecordDO = checkHandleParam(enterpriseId, handleParam, user);
        List<TbDisplayTableDataColumnDO> records = handleParam.getHandlePhotoList().stream().map(a -> {
            TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO = new TbDisplayTableDataColumnDO();
            tbDisplayTableDataColumnDO.setId(a.getDataColumnId());
            tbDisplayTableDataColumnDO.setPhotoArray(a.getPhotoArray());
            tbDisplayTableDataColumnDO.setRemark(a.getDescription());
            tbDisplayTableDataColumnDO.setCheckVideo(a.getCheckVideo());
            return tbDisplayTableDataColumnDO;
        }).collect(Collectors.toList());

        //上传视频处理，从缓存获取转码后的url

        if(handleParam.getTableProperty()!=null&&handleParam.getTableProperty().equals(Constants.INDEX_ONE)){
            //检查内容数据
            checkVideoHandel(records,enterpriseId, UploadTypeEnum.TB_DISPLAY_TABLE_DATA_CONTENT.getValue());
            tbDisplayTableDataContentMapper.batchUpdate(enterpriseId,records);
        }else {
            //检查项数据
            checkVideoHandel(records,enterpriseId, UploadTypeEnum.TB_DISPLAY_TABLE_DATA_COLUMN.getValue());
            tbDisplayTableDataColumnMapper.batchUpdate(enterpriseId, records);
        }
        List<Long> idList = records.stream().map(TbDisplayTableDataColumnDO::getId).collect(Collectors.toList());
        List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOS = tbDisplayTableDataColumnMapper.listByIdList(enterpriseId, idList);
        if(isAiCheck){
            queryExecutor.execute(()->{aiDealPhoto(enterpriseId, handleParam.getTableRecordId(), tbDisplayTableDataColumnDOS);});
        }
        return  tbDisplayTableRecordDO;
    }

    private void aiDealPhoto(String enterpriseId, Long recordId, List<TbDisplayTableDataColumnDO> columnList){
        log.info("图片ai分析");
        if(CollectionUtils.isEmpty(columnList)){
            return;
        }
        List<TbDisplayTableDataColumnDO> aiCheckColumn = columnList.stream().filter(o -> YesOrNoEnum.YES.getCode().equals(o.getIsAiCheck()) && StringUtils.isNotBlank(o.getPhotoArray())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(aiCheckColumn)){
            log.info("没有需要ai检查的");
            return;
        }
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<Long> metaColumnIds = aiCheckColumn.stream().map(TbDisplayTableDataColumnDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO> metaColumnList = tbMetaDisplayTableColumnMapper.listByIdList(enterpriseId, metaColumnIds);
        Map<Long, TbMetaDisplayTableColumnDO> columnMap = metaColumnList.stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, Function.identity(), (a, b) -> a));
        BigDecimal totalScore = BigDecimal.ZERO;
        for (TbDisplayTableDataColumnDO column : aiCheckColumn) {
            BigDecimal score = BigDecimal.ZERO;
            Integer aiColumnCnt = 0;
            JSONArray jsonArray = JSONArray.parseArray(column.getPhotoArray());
            TbMetaDisplayTableColumnDO tbMetaTableColumn = columnMap.get(column.getMetaColumnId());
            if(Objects.isNull(tbMetaTableColumn) || Objects.isNull(jsonArray)){
                continue;
            }
            List<JSONObject> jsonObjectList = new ArrayList<>();
            for (Object obj : jsonArray) {
                try {
                    JSONObject jsonObject = (JSONObject) obj;
                    String handleUrl = jsonObject.getString("handleUrl");
                    BigDecimal aiScore = jsonObject.getBigDecimal("aiScore");
                    if(Objects.nonNull(aiScore)){
                        jsonObjectList.add(jsonObject);
                        score = score.add(aiScore);
                        aiColumnCnt++;
                        continue;
                    }
                    AiCommentAndScoreVO imageAiScore = dashScopeService.getAiCommentAndScore(enterpriseId, new AiCommentAndScoreDTO(handleUrl, tbMetaTableColumn.getAiCheckStdDesc(), tbMetaTableColumn.getScore()));
                    column.setAiScore(imageAiScore.getAiScore());
                    column.setScore(imageAiScore.getAiScore());
                    jsonObject.put("aiScore", imageAiScore.getAiScore());
                    jsonObject.put("aiComment", imageAiScore.getAiComment());
                    if(Objects.nonNull(imageAiScore.getAiScore())){
                        score = score.add(imageAiScore.getAiScore());
                    }
                    aiColumnCnt++;
                    jsonObjectList.add(jsonObject);
                } catch (NoApiKeyException e) {
                    e.printStackTrace();
                } catch (UploadFileException e) {
                    e.printStackTrace();
                }
            }
            BigDecimal aiScore = BigDecimal.ZERO;
            if(aiColumnCnt.compareTo(0) > 0){
                aiScore = score.divide(new BigDecimal(aiColumnCnt), 2, RoundingMode.HALF_UP);
            }
            column.setAiScore(aiScore);
            column.setScore(aiScore);
            column.setPhotoArray(JSONObject.toJSONString(jsonObjectList));
            totalScore = totalScore.add(aiScore);
        }
        TbDisplayTableRecordDO tbDisplayTableRecordDO = new TbDisplayTableRecordDO();
        tbDisplayTableRecordDO.setId(recordId);
        tbDisplayTableRecordDO.setScore(totalScore);
        tbDisplayTableRecordDO.setAiScore(totalScore);
        tbDisplayTableRecordDO.setIsAiCheck(Boolean.TRUE);
        tbDisplayTableRecordMapper.updateByPrimaryKeySelective(enterpriseId, tbDisplayTableRecordDO);
        tbDisplayTableDataColumnMapper.batchUpdate(enterpriseId, aiCheckColumn);
    }

    @Override
    public void tableRecordHandleSubmit(String enterpriseId, TbDisplayHandleParam handleParam, CurrentUser user) {
        Long recordId = handleParam.getTableRecordId();
        // 检查项处理提交
        TbDisplayTableRecordDO tableRecordDO = tbDisplayTableRecordMapper.selectByPrimaryKey(enterpriseId, recordId);
        // 检查有无处理权限
        boolean hasHandleAuth = unifyTaskService.checkHasHandleAuth(enterpriseId, tableRecordDO.getUnifyTaskId()
                , tableRecordDO.getStoreId(), tableRecordDO.getLoopCount(), UnifyNodeEnum.FIRST_NODE.getCode(), user.getUserId());
        if (!hasHandleAuth) {
            log.info("您没有处理该门店的权限，recordId:" + recordId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "您没有处理该门店的权限【"+recordId+"】");
        }
        //查询是否存在符合条件的子任务
        TaskSubDO oldSub = taskSubMapper.getByTbDisplayTableRecordInfo(enterpriseId, tableRecordDO.getUnifyTaskId(),tableRecordDO.getStoreId()
                ,tableRecordDO.getLoopCount(), user.getUserId(), UnifyNodeEnum.FIRST_NODE.getCode());
        if (null == oldSub) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }
        if(oldSub.getSubBeginTime() > System.currentTimeMillis()){
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "任务还未开始");
        }
        Boolean flag = workflowService.subSubmitCheck(oldSub);
        if (flag == null || !flag) {
            log.info("该任务已被其他人操作，subTaskId:" + oldSub.getId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }

        if(CollUtil.isNotEmpty(handleParam.getHandlePhotoList())){
            tbDisplayColumnHandle(enterpriseId, handleParam, user, true);
        }

        String redisKeyPrefix = RedisConstant.TB_DISPLAY_HANDLE;
        String taskKey = redisKeyPrefix + "_" + enterpriseId + "_" + oldSub.getUnifyTaskId() + "_" + oldSub.getStoreId() + "_" + oldSub.getLoopCount();
        //加两分钟防止重复提交
        if(StringUtils.isNotBlank(redisUtilPool.getString(taskKey))){
            log.info("该任务已被其他人操作，subTaskId:" + oldSub);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }
        redisUtilPool.setString(taskKey, user.getUserId(), 10);

        // 项保存事务  fsad 层
        // check所有必填项
        final boolean[] handleFlag = {false};
        List<TbDisplayTableDataColumnDO> dataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordId(enterpriseId, recordId);
        List<TbDisplayTableDataContentDO> dataContentDOList = tbDisplayTableDataContentMapper.listByRecordId(enterpriseId, recordId);
        dataColumnDOList.stream().forEach(dataColumnDO -> {
            if (StrUtil.isNotEmpty(dataColumnDO.getPhotoArray())) {
                handleFlag[0] = true;
            }
        });
        //如果是高级检查表的话，检查项不会传图片 根据检查内容图片校验 dataContentDOList可能为null
        if (dataContentDOList!=null){
            dataContentDOList.forEach(dataContentDO -> {
                if (StrUtil.isNotEmpty(dataContentDO.getPhotoArray())) {
                    handleFlag[0] = true;
                }
            });
        }
        if (!handleFlag[0]) {
            redisUtilPool.delKey(taskKey);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "请上传检查项图片");
        }
        // 插入处理单
        TbDisplayHistoryDO tbDisplayHistoryDO = TbDisplayHistoryDO.builder().subTaskId(oldSub.getId()).recordId(recordId)
                .actionKey("").operateType(TbDisplayConstant.TbDisplayRecordStatusConstant.HANDLE)
                .operateUserId(user.getUserId()).operateUserName(user.getName()).isValid(true).nodeNo(oldSub.getNodeNo()).score(new BigDecimal(Constants.ZERO_STR)).build();
        tbDisplayHistoryMapper.insert(enterpriseId, tbDisplayHistoryDO);
        // 修改记录状态 和  处理人信息
        tbDisplayTableRecordMapper.updateHandleInfoByRecordId(enterpriseId,TbDisplayConstant.TbDisplayRecordStatusConstant.APPROVE ,user.getUserId(), user.getName(),recordId);
        // 处理完 所有检查项分数设置为默认1 分
//        tbDisplayTableDataColumnMapper.updateScoreByRecordId(enterpriseId, TbDisplayConstant.DEFAULT_SCORE, recordId);
        // 更新dataTable中的子任务id
        tbDataTableMapper.updateSubTaskIdById(enterpriseId, oldSub.getId(),dataColumnDOList.get(0).getDataTableId());

        //同一批次同一节点的同一的必是已完成
        TaskSubDO queryDO = new TaskSubDO(oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getNodeNo(),
                oldSub.getGroupItem(), oldSub.getLoopCount());
        TaskSubDO updateDO = TaskSubDO.builder()
                .subStatus(UnifyStatus.COMPLETE.getCode())
                .isOperateOverdue(oldSub.getIsOperateOverdue())
                .build();
        taskSubMapper.updateSubDetailExclude(enterpriseId, queryDO, updateDO, oldSub.getId());
        //记录实际处理人
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(oldSub.getNodeNo())) {
            TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getLoopCount());
            taskStoreMapper.updatedHandlerUserByTaskStoreId(enterpriseId, taskStoreDO.getId(), user.getUserId());
        }
        //发送消息处理
        WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, oldSub.getId(), oldSub,
                TbDisplayConstant.BizCodeConstant.DISPLAY_HANDLE, TbDisplayConstant.ActionKeyConstant.PASS, null,
                user.getUserId(), null, null, null);
        mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), oldSub.getId(), JSONObject.toJSONString(workflowDataDTO));
        simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
    }

    @Override
    public Map<Long, TbDisplayTableRecordVO> detailGroupBySubTaskId(String enterpriseId, String userId, List<Long> subTaskIdList) {
        return subTaskIdList.stream()
            .collect(Collectors.toMap(Function.identity(), a -> detail(enterpriseId, userId, a), (a, b) -> a));
    }

    @Override
    public Map<Long, TbDisplayTableRecordVO> detailGroupByTaskStoreId(String enterpriseId, String userId, List<Long> taskStoreIdList) {
        return taskStoreIdList.stream()
                .collect(Collectors.toMap(Function.identity(), a -> detailByTaskStoreId(enterpriseId, userId, a), (a, b) -> a));
    }


    @Override
    public TbDisplayTableRecordVO detail(String enterpriseId, String userId, Long subTaskId) {
        // 根据子任务id获取子任务信息
        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
        if (taskSubDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的子任务");
        }
        TbDisplayTableRecordVO tbDisplayTableRecordVO = tbDisplayTableRecordService.getTbDisplayTableRecordVO(enterpriseId, userId, taskSubDO);
        tbDisplayTableRecordVO.setSubTaskId(subTaskId);
        tbDisplayTableRecordVO.setOverdueTaskContinue("1".equals(taskSubDO.getIsOperateOverdue()) || StringUtils.isBlank(taskSubDO.getIsOperateOverdue()));
        // 根据子任务类型，获取处理单id
        if (StringUtils.isBlank(taskSubDO.getBizCode()) || StringUtils.isBlank(taskSubDO.getCid())) {
            // 如果没有bizCode和cid，说明未生成处理，审批单，直接返回检查项标准图
            return tbDisplayTableRecordVO;
        }
        return tbDisplayTableRecordVO;
    }

    @Override
    public TbDisplayTableRecordVO detailByTaskStoreId(String enterpriseId, String userId, Long taskStoreId) {
        // 根据子任务id获取子任务信息
        Long subTaskId = getTaskSubByTaskStoreId(enterpriseId, taskStoreId, userId);
        return detail(enterpriseId, userId, subTaskId);
    }

    @Override
    public TbDisplayTableRecordVO detailByTaskIdAndStoreIdAndLoopCount(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount) {
        TbDisplayTableRecordVO tbDisplayTableRecordVO = tbDisplayTableRecordService.getTableRecordByTaskIdAndStoreIdAndLoopCount(enterpriseId, unifyTaskId, storeId, loopCount);
        if (tbDisplayTableRecordVO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的陈列记录");
        }
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeId);
        RegionDO regionDO = regionMapper.getByRegionId(enterpriseId, storeDO.getRegionId());
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, tbDisplayTableRecordVO.getMetaTableId());
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, unifyTaskId);
        tbDisplayTableRecordVO.setTaskName(taskParentDO.getTaskName());
        tbDisplayTableRecordVO.setTaskType(taskParentDO.getTaskType());
        tbDisplayTableRecordVO.setStoreName(storeDO.getStoreName());
        tbDisplayTableRecordVO.setStoreAreaName(regionDO.getName());
        tbDisplayTableRecordVO.setTableName(tbMetaTableDO.getTableName());
        tbDisplayTableRecordVO.setAvatar(storeDO.getAvatar());
        tbDisplayTableRecordVO.setScore(tbDisplayTableRecordVO.getScore());
        tbDisplayTableRecordVO.setTaskDesc(taskParentDO.getTaskDesc());
        List<TbDisplayTableDataContentVO> tbDisplayDataContentVOList = new ArrayList<>();
        List<TbDisplayTableDataContentDO> tbDisplayTableDataContentDOList = tbDisplayTableDataContentMapper.listByRecordId(enterpriseId, tbDisplayTableRecordVO.getId());
        //检查内容
        if (CollectionUtils.isNotEmpty(tbDisplayTableDataContentDOList)) {
            //筛选出陈列使用得到的陈列检查内容id
            List<Long> metaContentIdList = tbDisplayTableDataContentDOList.stream().map(TbDisplayTableDataContentDO::getMetaContentId).collect(Collectors.toList());
            //查询出陈列用到的模板数据集合
            List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableContentDOList = tbMetaDisplayTableColumnMapper.listByIdList(enterpriseId, metaContentIdList);
            Map<Long, TbMetaDisplayTableColumnDO> tbMetaDisplayTableContentMap =
                    tbMetaDisplayTableContentDOList.stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, Function.identity(), (a, b) -> a));

            for (TbDisplayTableDataContentDO tbDisplayTableDataContentDO : tbDisplayTableDataContentDOList) {
                TbDisplayTableDataContentVO tbDisplayDataContentVo = new TbDisplayTableDataContentVO();
                BeanUtils.copyProperties(tbDisplayTableDataContentDO, tbDisplayDataContentVo);
                TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableContentMap.get(tbDisplayTableDataContentDO.getMetaContentId());
                if (tbMetaDisplayTableColumnDO != null) {
                    tbDisplayDataContentVo.setStandardPic(tbMetaDisplayTableColumnDO.getStandardPic());
                    tbDisplayDataContentVo.setDescription(tbMetaDisplayTableColumnDO.getDescription());
                    tbDisplayDataContentVo.setColumnName(tbMetaDisplayTableColumnDO.getColumnName());
                    tbDisplayDataContentVo.setOrderNum(tbMetaDisplayTableColumnDO.getOrderNum() == null ? 0 : tbMetaDisplayTableColumnDO.getOrderNum());
                    tbDisplayDataContentVo.setMetaScore(tbMetaDisplayTableColumnDO.getScore());
                }
                tbDisplayDataContentVOList.add(tbDisplayDataContentVo);
            }
        }
        tbDisplayTableRecordVO.setTbDisplayDataContentList(tbDisplayDataContentVOList);
        return tbDisplayTableRecordVO;
    }

    @Override
    public List<TbDisplayColumnReportVO> storeColumnExport(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount) {
        TbDisplayTableRecordVO tbDisplayTableRecordVO = tbDisplayTableRecordService.getTableRecordByTaskIdAndStoreIdAndLoopCount(enterpriseId, unifyTaskId, storeId, loopCount);
        if (tbDisplayTableRecordVO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的陈列记录");
        }
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeId);
        RegionDO regionDO = regionMapper.getByRegionId(enterpriseId, storeDO.getRegionId());
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId,tbDisplayTableRecordVO.getMetaTableId());
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, unifyTaskId);
        List<Long> unifyTaskIdList = new ArrayList<>();
        unifyTaskIdList.add(unifyTaskId);
        tbDisplayTableRecordVO.setHandleUserName(tbDisplayTableRecordVO.getHandleUserName());
        tbDisplayTableRecordVO.setApproveUserName(tbDisplayTableRecordVO.getApproveUserName());
        tbDisplayTableRecordVO.setRecheckUserName(tbDisplayTableRecordVO.getRecheckUserName());

        tbDisplayTableRecordVO.setTaskName(taskParentDO.getTaskName());
        tbDisplayTableRecordVO.setTaskType(taskParentDO.getTaskType());
        tbDisplayTableRecordVO.setStoreName(storeDO.getStoreName());
        tbDisplayTableRecordVO.setStoreAreaName(regionDO.getName());
        tbDisplayTableRecordVO.setTableName(tbMetaTableDO.getTableName());
        tbDisplayTableRecordVO.setAvatar(storeDO.getAvatar());
        List<TbDisplayColumnReportVO> tbDisplayColumnReportVOList = Lists.newArrayList();
        tbDisplayTableRecordVO.getTbDisplayDataColumnVOList().forEach(data->{
            TbDisplayColumnReportVO vo = new TbDisplayColumnReportVO();
            vo.setStoreAreaName(tbDisplayTableRecordVO.getStoreAreaName());
            vo.setStoreName(tbDisplayTableRecordVO.getStoreName());
            vo.setTaskName(tbDisplayTableRecordVO.getTaskName());
            vo.setTableName(tbDisplayTableRecordVO.getTableName());
            vo.setCheckType("陈列任务");
            vo.setColumnName(data.getColumnName());
            vo.setStandardPic(data.getStandardPic());
            vo.setDescription(data.getDescription());
            vo.setAvatar(tbDisplayTableRecordVO.getAvatar());
            vo.setHandleUserName(tbDisplayTableRecordVO.getHandleUserName());
            vo.setApproveUserName(tbDisplayTableRecordVO.getApproveUserName());
            vo.setRecheckUserName(tbDisplayTableRecordVO.getRecheckUserName());
            vo.setHandleUrl(getHandleUrlByPhotoArray(data.getPhotoArray()));
            tbDisplayColumnReportVOList.add(vo);
        });

        return tbDisplayColumnReportVOList;
    }

    @Override
    public ImportTaskDO exportDetailList(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount, String dbName) {
        GetTaskByPersonVO taskParentById = unifyTaskParentService.getTaskParentById(enterpriseId, unifyTaskId);
        StoreDTO storeByStoreId = storeMapper.getStoreByStoreId(enterpriseId, storeId);
        log.info("exportDetailList taskParentById:{}",JSONObject.toJSONString(taskParentById));
        String fileName = "陈列记录明细列表(" +taskParentById.getTaskName() +"-"+storeByStoreId.getStoreName() + ").xlsl";
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.TB_DISPLAY_TASK_REPORT_LIST_NEW);
        TbDisplayReportQueryParam query = new TbDisplayReportQueryParam();
        query.setDbName(dbName);
        query.setUnifyTaskId(unifyTaskId);
        query.setLoopCount(loopCount);
        query.setStoreId(storeId);
        query.setEid(enterpriseId);
        query.setExportServiceEnum(ExportServiceEnum.EXPORT_DISPLAY_RECORD_DETAIL_LIST);
        MsgUniteData msgUniteData = new MsgUniteData();
        ExportMsgSendRequest exportMsgSendRequest = new ExportMsgSendRequest();
        exportMsgSendRequest.setEnterpriseId(enterpriseId);
        exportMsgSendRequest.setRequest(JSONObject.parseObject(JSONObject.toJSONString(query)));
        exportMsgSendRequest.setTotalNum(Constants.MAX_EXPORT_SIZE);
        exportMsgSendRequest.setImportTaskDO(importTaskDO);
        exportMsgSendRequest.setDbName(dbName);
        msgUniteData.setData(JSONObject.toJSONString(exportMsgSendRequest));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    /**
     * 多个任务审核
     */
    @Override
    public void batchApprove(String enterpriseId, CurrentUser user, TbBatchApproveDisplayTaskParam displayTaskParam) {

        List<TbDisplayApprovePhotoParam> list = displayTaskParam.getApproveItemList();
        //可能为null 普通检查表的时候 ，没有对应的检查内容
        List<TbDisplayApproveContentParam> contentlist = displayTaskParam.getApproveContentList();
        if(CollectionUtils.isEmpty(list)){
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "请填写检查项");
        }
        //检查项 分组
        Map<Long, List<TbDisplayApprovePhotoParam>> taskParamMap = list.stream().collect(Collectors.groupingBy(TbDisplayApprovePhotoParam::getSubTaskId));

        //检查内容 分组
        Map<Long, List<TbDisplayApproveContentParam>> contentMap = new HashMap<>();
        if (contentlist!=null){
            contentMap = contentlist.stream().collect(Collectors.groupingBy(TbDisplayApproveContentParam::getSubTaskId));
        }

        // 使用Iterator遍历
        for (Map.Entry<Long, List<TbDisplayApprovePhotoParam>> entry : taskParamMap.entrySet()) {
            //单个进行审核
            TbApproveDisplayTaskParam tbApproveDisplayTaskParam = new TbApproveDisplayTaskParam();
            tbApproveDisplayTaskParam.setSubTaskId(entry.getKey());
            tbApproveDisplayTaskParam.setApproveItemList(entry.getValue());
            if (contentMap.size()!=0){
                tbApproveDisplayTaskParam.setApproveContentList(contentMap.get(entry.getKey()));
            }
            //审核
            tbDisplayTableRecordService.approve(enterpriseId, user, tbApproveDisplayTaskParam);

        }
    }

    @Override
    public void batchScore(String enterpriseId, CurrentUser user, TbBatchApproveDisplayTaskParam displayTaskParam) {
        List<TbDisplayApprovePhotoParam> list = displayTaskParam.getApproveItemList();
        //可能为null 普通检查表的时候 ，没有对应的检查内容
        List<TbDisplayApproveContentParam> contentlist = displayTaskParam.getApproveContentList();
        if (CollectionUtils.isEmpty(list)&&CollectionUtils.isEmpty(contentlist)) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "请填写检查项或者检查内容");
        }
        //分组
        Map<Long, List<TbDisplayApprovePhotoParam>> taskParamMap = new HashMap<>();
        if (list!=null) {
            taskParamMap = list.stream().collect(Collectors.groupingBy(TbDisplayApprovePhotoParam::getSubTaskId));
        }

        Map<Long, List<TbDisplayApproveContentParam>> contentMap = new HashMap<>();
        if (contentlist!=null){
            contentMap = contentlist.stream().collect(Collectors.groupingBy(TbDisplayApproveContentParam::getSubTaskId));
        }
        //检查项为null的时候
        if (taskParamMap.size()==0){
            for (Map.Entry<Long, List<TbDisplayApproveContentParam>> entry : contentMap.entrySet()) {
                TbApproveDisplayTaskParam tbApproveDisplayTaskParam = new TbApproveDisplayTaskParam();
                tbApproveDisplayTaskParam.setSubTaskId(entry.getKey());
                tbApproveDisplayTaskParam.setApproveContentList(contentMap.get(entry.getKey()));
                //审核
                tbDisplayTableRecordService.score(enterpriseId, user, tbApproveDisplayTaskParam);
            }
        }
        // 检查项不为null的时候 使用Iterator遍历
        for (Map.Entry<Long, List<TbDisplayApprovePhotoParam>> entry : taskParamMap.entrySet()) {
            //单个进行审核
            TbApproveDisplayTaskParam tbApproveDisplayTaskParam = new TbApproveDisplayTaskParam();
            tbApproveDisplayTaskParam.setSubTaskId(entry.getKey());
            tbApproveDisplayTaskParam.setApproveItemList(entry.getValue());
            if (contentMap.size()!=0){
                tbApproveDisplayTaskParam.setApproveContentList(contentMap.get(entry.getKey()));
            }
            //审核
            tbDisplayTableRecordService.score(enterpriseId, user, tbApproveDisplayTaskParam);

        }
    }

    private TbDisplayTableRecordDO checkHandleParam(String enterpriseId, TbDisplayHandleParam handleParam, CurrentUser user) {

        Long recordId = handleParam.getTableRecordId();
        TbDisplayTableRecordDO tableRecordDO = tbDisplayTableRecordMapper.selectByPrimaryKey(enterpriseId, recordId);
        if (null == tableRecordDO) {
            log.info("陈列记录不存在，recordId:" + recordId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "陈列记录不存在【"+recordId+"】");
        }
        if(CollUtil.isEmpty(handleParam.getHandlePhotoList())){
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "处理图片不能为空");
        }
        TaskSubDO taskSubDO = taskSubMapper.getByTbDisplayTableRecordInfo(enterpriseId, tableRecordDO.getUnifyTaskId(),tableRecordDO.getStoreId()
                ,tableRecordDO.getLoopCount(), user.getUserId(), UnifyNodeEnum.FIRST_NODE.getCode());
        if (null == taskSubDO) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }
        if(taskSubDO.getSubBeginTime() > System.currentTimeMillis()){
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "任务未开始，不可执行！");
        }
        boolean hasHandleAuth = unifyTaskService.checkHasHandleAuth(enterpriseId, tableRecordDO.getUnifyTaskId()
                , tableRecordDO.getStoreId(), tableRecordDO.getLoopCount(), UnifyNodeEnum.FIRST_NODE.getCode(), user.getUserId());
        if (!hasHandleAuth) {
            log.info("您没有处理该门店的权限，recordId:" + recordId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "您没有处理该门店的权限【"+recordId+"】");
        }

        //无效校验
//        List<Long> dataColumnIdList = handleParam.getHandlePhotoList().stream().map(TbDisplayHandlePhotoParam::getDataColumnId).collect(Collectors.toList());
//        List<TbDisplayTableDataColumnDO>  tbDisplayTableDataColumnDOList = tbDisplayTableDataColumnMapper.listByIdListAndRecordId(enterpriseId, dataColumnIdList, recordId);
//        Map<Long, TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOMap =
//                tbDisplayTableDataColumnDOList.stream().collect(Collectors.toMap(TbDisplayTableDataColumnDO::getId, Function.identity(), (a, b) -> a));
//        handleParam.getHandlePhotoList().forEach(handlePhoto -> {
//            TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO = tbDisplayTableDataColumnDOMap.get(handlePhoto.getDataColumnId());
//            if (tbDisplayTableDataColumnDO == null) {
//                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "检查项不存在【"+handlePhoto.getDataColumnId()+"】");
//            }
//        });
        return  tableRecordDO;
    }

    // [{"handleUrl":"url1","finalUrl":"url2"},{"handleUrl":"url1","finalUrl":"url2"}]
    public String getHandleUrlByPhotoArray(String photoArray) {
        List<String> handleUrlList = Lists.newArrayList();
        if(StrUtil.isNotEmpty(photoArray)){
            JSONArray jsonArray = JSONArray.parseArray(photoArray);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String handleUrl = jsonObject.getString("handleUrl");
                handleUrlList.add(handleUrl);
            }
            return String.join(",", handleUrlList);
        }
        return "";
    }

    public Long getTaskSubByTaskStoreId(String enterpriseId, Long taskStoreId, String userId){
        // 查询最新node节点
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        // 查询当前人记录
        TaskSubVO taskSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                userId, UnifyStatus.ONGOING.getCode(), null);
        if (taskSub == null) {
            //查不到当前人记录查询进行中最新的一条,
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                    null, UnifyStatus.ONGOING.getCode(), null);
        }

        if (taskSub == null) {
            //查不到进行中的记录，则任务已完成查询完成的记录
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                    null, UnifyStatus.COMPLETE.getCode(), UnifyNodeEnum.END_NODE.getCode());
        }

        if (taskSub == null) {
            //查不到进行中的记录，则任务已完成查询完成的记录
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                    null, UnifyStatus.COMPLETE.getCode(), null);
        }
        Long subTaskId = null;
        if(taskSub != null){
            subTaskId = taskSub.getSubTaskId();
        }
        return subTaskId;
    }

    /**
     * 如果状态为转码完成，直接修改，否则从redis获取转码的视频信息
     * @param tbDisplayTableDataColumnDOList
     * @param enterpriseId
     * @return void
     */
    @Override
    public void checkVideoHandel(List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList, String enterpriseId, Integer uploadType){
        if(CollectionUtils.isEmpty(tbDisplayTableDataColumnDOList)){
            return;
        }
        for (TbDisplayTableDataColumnDO tbDisplayTableDataColumnDO : tbDisplayTableDataColumnDOList) {
            SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(tbDisplayTableDataColumnDO.getCheckVideo(), SmallVideoInfoDTO.class);
            if(smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())){
                String callbackCache;
                SmallVideoDTO smallVideoCache;
                SmallVideoParam smallVideoParam;
                for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                    //如果转码完成
                    if(smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                        continue;
                    }
                    callbackCache = redisUtilPool.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                    if(StringUtils.isNotBlank(callbackCache)){
                        smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                        if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >= 3){
                            BeanUtils.copyProperties(smallVideoCache,smallVideo);
                        }else {
                            smallVideoParam = new SmallVideoParam();
                            setNotCompleteCache(smallVideoParam,smallVideo,tbDisplayTableDataColumnDO.getId(),enterpriseId, uploadType);
                        }
                    }else {
                        smallVideoParam = new SmallVideoParam();
                        setNotCompleteCache(smallVideoParam,smallVideo,tbDisplayTableDataColumnDO.getId(),enterpriseId, uploadType);
                    }
                }
                tbDisplayTableDataColumnDO.setCheckVideo(JSONObject.toJSONString(smallVideoInfo));
            }
        }

    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     * @date 2021/10/14
     * @param smallVideoParam
     * @param smallVideo
     * @param businessId
     * @param enterpriseId
     * @return void
     */
    public void setNotCompleteCache(SmallVideoParam smallVideoParam,SmallVideoDTO smallVideo,Long businessId,String enterpriseId, Integer uploadType){
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(uploadType);
        smallVideoParam.setBusinessId(businessId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtilPool.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE,smallVideo.getVideoId(),JSONObject.toJSONString(smallVideoParam));
    }
}
