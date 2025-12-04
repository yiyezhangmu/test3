package com.coolcollege.intelligent.service.storework.Impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.StoreWorkConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaStaTableColumnDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkRecordDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkTableMappingDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRecordDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkQuestionDataDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkSubmitCommentMsgData;
import com.coolcollege.intelligent.model.storework.request.CommentScoreRequest;
import com.coolcollege.intelligent.model.storework.request.SingleExecutionRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataTableColumnRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableDefColumnVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableColumnVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableDetailVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.storework.StoreWorkDataTableColumnService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.TableTypeConstant.STANDARD;
import static com.coolcollege.intelligent.service.storework.Impl.StoreWorkDataTableServiceImpl.setAiStatusDisplayFlag;

/**
 * @Author suzhuhong
 * @Date 2022/9/19 11:33
 * @Version 1.0
 */
@Service
@Slf4j
public class StoreWorkDataTableColumnServiceImpl implements StoreWorkDataTableColumnService {
    
    @Resource
    SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    TbMetaTableMapper tbMetaTableMapper;
    @Resource
    SwStoreWorkDataTableDao swStoreWorkDataTableDao;
    @Resource
    TbMetaStaTableColumnDao tbMetaStaTableColumnDao;
    @Resource
    TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Autowired
    SysRoleMapper sysRoleMapper;
    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;
    @Autowired
    private RedisUtilPool redisUtil;
    @Resource
    SwStoreWorkRecordDao swStoreWorkRecordDao;
    @Resource
    private RedisConstantUtil redisConstantUtil;


    @Override
    public List<StoreWorkDataTableVO> getStoreWorkDataTableColumn(String enterpriseId, StoreWorkDataTableColumnRequest request, String userId) {
        List<Long> dataTableIds = request.getDataTableIds();
        String businessId = request.getBusinessId();
        List<String> checkResultList = request.getCheckResultList();
        //根据dataTableId查询数据表中项的数据
        List<Long> dataTableIdList = new ArrayList<>();
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dataTableIds)){
            dataTableIdList = dataTableIds;
            swStoreWorkDataTableDOS  = swStoreWorkDataTableDao.selectByIds(dataTableIdList,enterpriseId);
        }else {
            //查询全部的时候检查表数据表
            swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTableByBusinessId(enterpriseId,businessId,null);
            dataTableIdList = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getId).collect(Collectors.toList());
        }
        List<Long> metatableIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getMetaTableId).collect(Collectors.toList());

        List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnDOS = swStoreWorkDataTableColumnDao.selectColumnByDataTableId(enterpriseId, dataTableIdList,checkResultList, null, request.getAiCheckResultList());
        List<String> userIds = swStoreWorkDataTableColumnDOS.stream().filter(x -> StringUtils.isNotEmpty(x.getHandlerUserId())).map(SwStoreWorkDataTableColumnDO::getHandlerUserId).collect(Collectors.toList());
        Map<String, EnterpriseUserDO> userNameMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);

        List<Long> tableMappingIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getTableMappingId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByIds(enterpriseId, tableMappingIds);
        Map<Long, String> tableInfoMap = swStoreWorkTableMappingDOS.stream().collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getTableInfo));

        //根据dataTableId分组
        Map<Long, List<SwStoreWorkDataTableColumnDO>> listMap = swStoreWorkDataTableColumnDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableColumnDO::getDataTableId));

        //检查表定义集合
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, metatableIds);
        Map<Long, TbMetaTableDO> tableMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, x -> x));

        List<Long> defIds = tbMetaTableDOS.stream().filter(x -> TableTypeUtil.isUserDefinedTable(x.getTableProperty(), STANDARD)).map(TbMetaTableDO::getId).collect(Collectors.toList());
        Map<Long, List<TbMetaDefTableColumnDO>> groupMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(defIds)){
            List<TbMetaDefTableColumnDO> allColumnByMetaTableIdList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defIds);
            groupMap = allColumnByMetaTableIdList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
        }

        List<Long> staIds = tbMetaTableDOS.stream().filter(x -> !TableTypeUtil.isUserDefinedTable(x.getTableProperty(), STANDARD)).map(TbMetaTableDO::getId).collect(Collectors.toList());
        Map<Long, TbMetaStaTableColumnDO> executeDemandMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(staIds)){
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, staIds,Boolean.FALSE);
            executeDemandMap = tbMetaStaTableColumnDOS.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, data->data));
        }


        List<TbMetaColumnResultDO> tbMetaColumnResultDOS = tbMetaColumnResultMapper.selectByMetaTableIdList(enterpriseId, metatableIds);
        Map<Long, List<TbMetaColumnResultDO>> resultMap = tbMetaColumnResultDOS.stream().collect(Collectors.groupingBy(TbMetaColumnResultDO::getMetaColumnId));

        List<StoreWorkDataTableVO> storeWorkDataTableVOS = new ArrayList<>();
        Map<Long, List<TbMetaDefTableColumnDO>> finalGroupMap = groupMap;
        List<String> actualCommentUserIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getActualCommentUserId).collect(Collectors.toList());
        Map<String, EnterpriseUserDO> actualCommentUserMap = enterpriseUserDao.getUserMap(enterpriseId, actualCommentUserIds);
        List<String> businessIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getTcBusinessId).collect(Collectors.toList());
        List<SwStoreWorkRecordDO> swStoreWorkRecordDOS = swStoreWorkRecordDao.getByTcBusinessIds(enterpriseId, businessIds);
        Map<String, Long> recordIdMap = swStoreWorkRecordDOS.stream().collect(Collectors.toMap(SwStoreWorkRecordDO::getTcBusinessId, SwStoreWorkRecordDO::getId));

        Map<Long, TbMetaStaTableColumnDO> finalExecuteDemandMap = executeDemandMap;
        swStoreWorkDataTableDOS.forEach(x->{
            StoreWorkDataTableVO storeWorkDataTableVO = new StoreWorkDataTableVO();
            TbMetaTableDO tb = tableMap.getOrDefault(x.getMetaTableId(), new TbMetaTableDO());
            storeWorkDataTableVO.setTableProperty(tb.getTableProperty());
            String commentUserIds = x.getCommentUserIds();
            List<String> commentIds = new ArrayList<>();
            if (StringUtils.isNotEmpty(commentUserIds)){
                String[] split = commentUserIds.split(",");
                commentIds = Arrays.asList(split);
            }
            storeWorkDataTableVO.setCommentUserIds(commentIds);
            List<EnterpriseUserDO> enterpriseUserList = enterpriseUserDao.selectByUserIds(enterpriseId, commentIds);
            List<String>  commentUserIdNames= enterpriseUserList.stream().map(EnterpriseUserDO::getName).collect(Collectors.toList());
            storeWorkDataTableVO.setCommentUserNames(commentUserIdNames);
            storeWorkDataTableVO.setWorkCycle(x.getWorkCycle());
            storeWorkDataTableVO.setActualCommentUserId(x.getActualCommentUserId());
            storeWorkDataTableVO.setActualCommentUserName(actualCommentUserMap.getOrDefault(x.getActualCommentUserId(),new EnterpriseUserDO()).getName());
            storeWorkDataTableVO.setBeginTime(x.getBeginTime());
            storeWorkDataTableVO.setCommentStatus(x.getCommentStatus());
            storeWorkDataTableVO.setEndTime(x.getEndTime());
            storeWorkDataTableVO.setTableInfo(tableInfoMap.getOrDefault(x.getTableMappingId(),""));
            List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnList = listMap.getOrDefault(x.getId(),Collections.emptyList());
            if (CollectionUtils.isEmpty(swStoreWorkDataTableColumnList)){
                storeWorkDataTableVO.setStoreWorkDataTableColumnVOList(Collections.emptyList());
            }
            Map<Long, SwStoreWorkDataTableColumnDO> tableColumnMap = swStoreWorkDataTableColumnList.stream().collect(Collectors.toMap(SwStoreWorkDataTableColumnDO::getMetaColumnId, y -> y));

            Map<Long, SwStoreWorkDataTableColumnDO> lastResultMap = new HashMap<>(16);
            //上次检查表店务记录数据
            SwStoreWorkDataTableDO lastTimeDataTableDO = swStoreWorkDataTableDao.getLastTimeDataTableDO(enterpriseId, x.getStoreId(), x.getMetaTableId());
            if (lastTimeDataTableDO!=null){
                List<SwStoreWorkDataTableColumnDO> tbDataStaTableColumnDOS = swStoreWorkDataTableColumnDao.selectByDataTableId(enterpriseId, lastTimeDataTableDO.getId());
                lastResultMap = tbDataStaTableColumnDOS.stream().collect(Collectors.toMap(SwStoreWorkDataTableColumnDO::getMetaColumnId, data->data));
            }
            Map<Long, TbMetaDefTableColumnDO> tMap =new HashMap<>();
            if(TableTypeUtil.isUserDefinedTable(tb.getTableProperty(),STANDARD)){
                List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = finalGroupMap.getOrDefault(x.getMetaTableId(),Collections.emptyList());
                tMap = tbMetaDefTableColumnDOS.stream().collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, data -> data));
            }

            // 需要AI点评的情况下点评人已点评，则无法再次点评
            boolean aiCommentCanRecheck = Constants.INDEX_ONE.equals(x.getIsAiProcess()) && (x.getAiStatus() & Constants.STORE_WORK_AI.AI_STATUS_COMMENTED) == 0;
            Boolean commentTabDisplayFlag = getCommentTabDisplayFlag(x.getCommentStatus(),x.getCompleteStatus(),x.getEndTime(),x.getCommentUserIds(),userId, aiCommentCanRecheck);
            storeWorkDataTableVO.setCommentTabDisplayFlag(commentTabDisplayFlag);


            Map<Long, SwStoreWorkDataTableColumnDO> finalLastResultMap = lastResultMap;
            List<StoreWorkDataTableColumnVO>  list= new ArrayList<>();
            //每个表的项
            Map<Long, TbMetaDefTableColumnDO> finalTMap = tMap;
            swStoreWorkDataTableColumnList.forEach(storeWorkDataTableColumnDO->{
                StoreWorkDataTableColumnVO storeWorkDataTableColumnVO = new StoreWorkDataTableColumnVO();
                storeWorkDataTableColumnVO.setMetaColumnName(storeWorkDataTableColumnDO.getMetaColumnName());
                storeWorkDataTableColumnVO.setMetaColumnId(storeWorkDataTableColumnDO.getMetaColumnId());
                storeWorkDataTableColumnVO.setStoreWorkId(x.getStoreWorkId());
                storeWorkDataTableColumnVO.setId(storeWorkDataTableColumnDO.getId());
                storeWorkDataTableColumnVO.setBusinessId(x.getTcBusinessId());
                storeWorkDataTableColumnVO.setRecordId(recordIdMap.getOrDefault(x.getTcBusinessId(),0L));
                storeWorkDataTableColumnVO.setStoreId(x.getStoreId());
                storeWorkDataTableColumnVO.setStoreName(x.getStoreName());
                storeWorkDataTableColumnVO.setRegionId(x.getRegionId());
                storeWorkDataTableColumnVO.setRegionPath(x.getRegionPath());
                storeWorkDataTableColumnVO.setDataTableId(x.getId());
                storeWorkDataTableColumnVO.setTableName(x.getTableName());
                storeWorkDataTableColumnVO.setMetaTableId(storeWorkDataTableColumnDO.getMetaTableId());
                // storeWorkDataTableColumnVO.setDescription(storeWorkDataTableColumnDO.getDescription());
                if (StringUtils.isNotEmpty(storeWorkDataTableColumnDO.getHandlerUserId())){
                    List<SysRoleDO> sysRoleList = sysRoleMapper.getSysRoleByUserId(enterpriseId, storeWorkDataTableColumnDO.getHandlerUserId());
                    if (CollectionUtils.isNotEmpty(sysRoleList)){
                        String roleNameList = sysRoleList.stream().map(SysRoleDO::getRoleName).collect(Collectors.joining(","));
                        storeWorkDataTableColumnVO.setHandlerUserRoleName(roleNameList);
                    }
                }
                storeWorkDataTableColumnVO.setHandlerUserId(storeWorkDataTableColumnDO.getHandlerUserId());
                storeWorkDataTableColumnVO.setHandlerUserName(userNameMap.getOrDefault(storeWorkDataTableColumnDO.getHandlerUserId(), new EnterpriseUserDO()).getName());
                storeWorkDataTableColumnVO.setAvatar(userNameMap.getOrDefault(storeWorkDataTableColumnDO.getHandlerUserId(), new EnterpriseUserDO()).getAvatar());
                storeWorkDataTableColumnVO.setHandlerUserMobile(userNameMap.getOrDefault(storeWorkDataTableColumnDO.getHandlerUserId(), new EnterpriseUserDO()).getMobile());
                storeWorkDataTableColumnVO.setExecuteDemand(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getExecuteDemand());
                storeWorkDataTableColumnVO.setStaPic(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getStandardPic());
                storeWorkDataTableColumnVO.setDescription(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getDescription());
                storeWorkDataTableColumnVO.setCategoryName(storeWorkDataTableColumnDO.getCategoryName());
                storeWorkDataTableColumnVO.setSubmitStatus(storeWorkDataTableColumnDO.getSubmitStatus());
                storeWorkDataTableColumnVO.setCheckPics(storeWorkDataTableColumnDO.getCheckPics());
                storeWorkDataTableColumnVO.setCheckScore(storeWorkDataTableColumnDO.getCheckScore());
                storeWorkDataTableColumnVO.setSupportScore(storeWorkDataTableColumnDO.getColumnMaxScore());
                storeWorkDataTableColumnVO.setCheckText(storeWorkDataTableColumnDO.getCheckText());
                storeWorkDataTableColumnVO.setCheckVideo(storeWorkDataTableColumnDO.getCheckVideo());
                storeWorkDataTableColumnVO.setColumnType(storeWorkDataTableColumnDO.getColumnType());
                storeWorkDataTableColumnVO.setScoreTimes(storeWorkDataTableColumnDO.getScoreTimes());
                storeWorkDataTableColumnVO.setCommentContent(storeWorkDataTableColumnDO.getCommentContent());
                storeWorkDataTableColumnVO.setAwardTimes(storeWorkDataTableColumnDO.getAwardTimes());
                storeWorkDataTableColumnVO.setCheckResult(storeWorkDataTableColumnDO.getCheckResult());
                storeWorkDataTableColumnVO.setCheckResultId(storeWorkDataTableColumnDO.getCheckResultId());
                storeWorkDataTableColumnVO.setTaskQuestionStatus(storeWorkDataTableColumnDO.getTaskQuestionStatus());
                storeWorkDataTableColumnVO.setTaskQuestionId(storeWorkDataTableColumnDO.getTaskQuestionId());
                storeWorkDataTableColumnVO.setIsAiCheck(storeWorkDataTableColumnDO.getIsAiCheck());
                ////异步创建任务延迟，从缓存中取值
                setTaskQuestionId(enterpriseId, storeWorkDataTableColumnVO);
                storeWorkDataTableColumnVO.setCheckResultName(storeWorkDataTableColumnDO.getCheckResultName());
                storeWorkDataTableColumnVO.setColumnMaxAward(storeWorkDataTableColumnDO.getColumnMaxAward());
                storeWorkDataTableColumnVO.setColumnMaxScore(storeWorkDataTableColumnDO.getColumnMaxScore());
                storeWorkDataTableColumnVO.setColumnResultList(resultMap.get(storeWorkDataTableColumnDO.getMetaColumnId()));
                String lastStoreWorkResult = finalLastResultMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(), new SwStoreWorkDataTableColumnDO()).getCheckResult();
                storeWorkDataTableColumnVO.setLastPatrolStoreResult(lastStoreWorkResult);
                storeWorkDataTableColumnVO.setLastDataColumnId(finalLastResultMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(), new SwStoreWorkDataTableColumnDO()).getId());
                if(TableTypeUtil.isUserDefinedTable(tb.getTableProperty(),STANDARD)){
                    TbMetaDefTableColumnDO tbMetaDefTableColumnDO = finalTMap.get(storeWorkDataTableColumnDO.getMetaColumnId());
                    StoreWorkDataTableDefColumnVO storeWorkDataTableDefColumnVO = new StoreWorkDataTableDefColumnVO();
                    SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumn= tableColumnMap.get(tbMetaDefTableColumnDO.getId());
                    storeWorkDataTableDefColumnVO.setValue1(swStoreWorkDataTableColumn.getValue1());
                    storeWorkDataTableDefColumnVO.setValue2(swStoreWorkDataTableColumn.getValue2());
                    storeWorkDataTableDefColumnVO.setColumnName(tbMetaDefTableColumnDO.getColumnName());
                    storeWorkDataTableDefColumnVO.setMetaTableId(tbMetaDefTableColumnDO.getMetaTableId());
                    storeWorkDataTableDefColumnVO.setDescription(tbMetaDefTableColumnDO.getDescription());
                    storeWorkDataTableDefColumnVO.setColumnLength(tbMetaDefTableColumnDO.getColumnLength());
                    storeWorkDataTableDefColumnVO.setFormat(tbMetaDefTableColumnDO.getFormat());
                    storeWorkDataTableDefColumnVO.setRequired(tbMetaDefTableColumnDO.getRequired());
                    storeWorkDataTableDefColumnVO.setChooseValues(tbMetaDefTableColumnDO.getChooseValues());
                    storeWorkDataTableDefColumnVO.setSchema(tbMetaDefTableColumnDO.getSchema());
                    storeWorkDataTableColumnVO.setStoreWorkDataTableDefColumn(storeWorkDataTableDefColumnVO);
                }
                setVOAiField(storeWorkDataTableColumnVO, storeWorkDataTableColumnDO);
                list.add(storeWorkDataTableColumnVO);
                storeWorkDataTableVO.setStoreWorkDataTableColumnVOList(list);
            });
            storeWorkDataTableVO.setIsAiProcess(x.getIsAiProcess());
            storeWorkDataTableVO.setAiStatus(x.getAiStatus());
            setAiStatusDisplayFlag(storeWorkDataTableVO, tb);
            storeWorkDataTableVOS.add(storeWorkDataTableVO);
        });
        return storeWorkDataTableVOS;
    }

    private void setTaskQuestionId(String enterpriseId , StoreWorkDataTableColumnVO swStoreWorkDataTableColumnVO){
        try{
            if(swStoreWorkDataTableColumnVO.getTaskQuestionId() > 0){
                return;
            }
            String taskQuestionId = redisUtilPool.getString(redisConstantUtil.getStoreWorkQuestionTaskLockKey(enterpriseId, String.valueOf(swStoreWorkDataTableColumnVO.getId())));
            if(StringUtils.isNotBlank(taskQuestionId)){
                swStoreWorkDataTableColumnVO.setTaskQuestionId(Long.valueOf(taskQuestionId));
            }
        } catch (Exception exception) {
            log.error("#storeWork#setTaskQuestionId报错enterpriseId={},dataColumnId={}", enterpriseId, swStoreWorkDataTableColumnVO.getId());
        }

    }
    /**
     * 点评标识
     * @param commentStatus
     * @param handleStatus
     * @param endTime
     * @param comment_user_ids
     * @param userId
     * @param aiCommentCanRecheck AI点评后是否可以复核
     * @return
     */
    public Boolean getCommentTabDisplayFlag(Integer commentStatus, Integer handleStatus, Date endTime,String comment_user_ids,String userId, boolean aiCommentCanRecheck){
        //如果是点评过的 直接返回false
        if (Constants.INDEX_ONE.equals(commentStatus) && !aiCommentCanRecheck ||!comment_user_ids.contains(userId)){
            return Boolean.FALSE;
        }
        //执行过或者没有执行过但是当前时间大于结束时间
        if (Constants.INDEX_ONE.equals(handleStatus)||(Constants.INDEX_ZERO.equals(handleStatus)&&endTime.getTime()<System.currentTimeMillis())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    @Override
    public Boolean singleColumnSubmit(String enterpriseId, CurrentUser user, SingleExecutionRequest singleExecutionRequest) {
        if (singleExecutionRequest.getId()==null){
            throw  new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //校验逾期不能提交
        SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumn = swStoreWorkDataTableColumnDao.selectByPrimaryKey(singleExecutionRequest.getId(), enterpriseId);
        if(swStoreWorkDataTableColumn==null||swStoreWorkDataTableColumn.getDeleted()){
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_DATA_TABLE_COLUMN_IS_NOT_EXIST);
        }
        SwStoreWorkDataTableDO swStoreWorkDataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(swStoreWorkDataTableColumn.getDataTableId(), enterpriseId);
        if (Constants.INDEX_ONE.equals(swStoreWorkDataTableDO.getCommentStatus())){
            throw new ServiceException(ErrorCodeEnum.CHECK_TABLE_NOT_HANDLE);
        }
        //逾期是否能执行
        if (swStoreWorkDataTableDO.getEndTime().getTime()<System.currentTimeMillis()&&Constants.INDEX_ZERO.equals(swStoreWorkDataTableDO.getOverdueContinue())){
            throw  new ServiceException(ErrorCodeEnum.OVERDUE_CONTINUE);
        }
        // AI表已完成后不允许重复提交
        if (Constants.INDEX_ONE.equals(swStoreWorkDataTableDO.getCompleteStatus()) && Constants.INDEX_ONE.equals(swStoreWorkDataTableDO.getIsAiProcess())) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_COMPLETE);
        }

        //上传视频处理，从缓存获取转码后的url
        singleColumnSubmitVideoHandle(singleExecutionRequest,enterpriseId);
        SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = new SwStoreWorkDataTableColumnDO();
        swStoreWorkDataTableColumnDO.setCheckPics(singleExecutionRequest.getCheckPics());
        swStoreWorkDataTableColumnDO.setCheckText(singleExecutionRequest.getCheckText());
        swStoreWorkDataTableColumnDO.setCheckVideo(singleExecutionRequest.getCheckVideo());
        swStoreWorkDataTableColumnDO.setValue1(singleExecutionRequest.getValue1());
        swStoreWorkDataTableColumnDO.setValue2(singleExecutionRequest.getValue2());
        swStoreWorkDataTableColumnDO.setId(singleExecutionRequest.getId());
        swStoreWorkDataTableColumnDO.setUpdateTime(new Date());
        swStoreWorkDataTableColumnDO.setUpdateUserId(user.getUserId());
        swStoreWorkDataTableColumnDO.setHandlerUserId(user.getUserId());
        swStoreWorkDataTableColumnDO.setSubmitTime(new Date());
        swStoreWorkDataTableColumnDO.setSubmitStatus(Constants.INDEX_ONE);
        swStoreWorkDataTableColumnDao.updateByPrimaryKeySelective(swStoreWorkDataTableColumnDO,enterpriseId);
        // TODO: 2022/9/22 丢消息更新表、店务数据
        StoreWorkSubmitCommentMsgData storeWorkSubmitCommentMsgData = new StoreWorkSubmitCommentMsgData();
        storeWorkSubmitCommentMsgData.setEnterpriseId(enterpriseId);
        storeWorkSubmitCommentMsgData.setType(StoreWorkConstant.MsgType.SUBMIT);
        storeWorkSubmitCommentMsgData.setDataColumnId(singleExecutionRequest.getId());
        simpleMessageService.send(JSONObject.toJSONString(storeWorkSubmitCommentMsgData), RocketMqTagEnum.STOREWORK_SUBMIT_DATA_QUEUE);
        return Boolean.TRUE;
    }


    /**
     * 视频处理
     * @param request
     * @param enterpriseId
     */
    public void singleColumnSubmitVideoHandle(SingleExecutionRequest request, String enterpriseId){
        log.info("转码开始：singleColumnSubmitVideoHandle:{}",JSONObject.toJSONString(request));
        if(StringUtils.isBlank(request.getCheckVideo())){
            return;
        }
        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(request.getCheckVideo(), SmallVideoInfoDTO.class);
        if(smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())){
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                //如果转码完成就不处理，直接修改
                if(smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                    continue;
                }
                callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if(StringUtils.isNotBlank(callbackCache)){
                    smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                    if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >=3){
                        BeanUtils.copyProperties(smallVideoCache,smallVideo);
                    }else {
                        smallVideoParam = new SmallVideoParam();
                        setNotCompleteCache(smallVideoParam,smallVideo,request.getId(),enterpriseId);
                    }
                }else {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam,smallVideo,request.getId(),enterpriseId);
                }
            }
        }
        if(StringUtils.isNotBlank(request.getCheckVideo())){
            request.setCheckVideo(JSONObject.toJSONString(smallVideoInfo));
        }

    }
    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     * @param smallVideoParam
     * @param smallVideo
     * @param dataColumnId
     * @param enterpriseId
     */
    public void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, Long dataColumnId, String enterpriseId) {
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.STORE_WORK_SUBMIT.getValue());
        smallVideoParam.setBusinessId(dataColumnId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtilPool.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }

    @Override
    public StoreWorkDataTableDetailVO getTableColumn(String enterpriseId, CurrentUser user, Long dataTableId) {

        //查询当前人员是否有点评数据缓存
        String cacheValue = redisUtilPool.getString(MessageFormat.format(RedisConstant.STORE_WORK_COMMENT_CACHE_KEY, enterpriseId, user.getUserId(), dataTableId));
        List<CommentScoreRequest> list = JSONObject.parseArray(cacheValue, CommentScoreRequest.class);
        Map<Long, CommentScoreRequest> cacheValueMap= new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)){
            cacheValueMap = list.stream().collect(Collectors.toMap(CommentScoreRequest::getId, data -> data));
        }
        //需要点评的表的数据
        StoreWorkDataTableDetailVO storeWorkDataTableDetailVO = new StoreWorkDataTableDetailVO();
        SwStoreWorkDataTableDO swStoreWorkDataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
        if (swStoreWorkDataTableDO==null){
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_RECORD_TABLE_IS_NOT_EXIST);
        }
        storeWorkDataTableDetailVO.setTableName(swStoreWorkDataTableDO.getTableName());
        storeWorkDataTableDetailVO.setActualHandleUserId(swStoreWorkDataTableDO.getActualHandleUserId());
        storeWorkDataTableDetailVO.setBeginTime(swStoreWorkDataTableDO.getBeginTime());
        storeWorkDataTableDetailVO.setEndTime(swStoreWorkDataTableDO.getEndTime());
        storeWorkDataTableDetailVO.setCommentStatus(swStoreWorkDataTableDO.getCommentStatus());
        storeWorkDataTableDetailVO.setStoreName(swStoreWorkDataTableDO.getStoreName());
        storeWorkDataTableDetailVO.setBeginHandleTime(swStoreWorkDataTableDO.getBeginHandleTime());
        storeWorkDataTableDetailVO.setEndHandleTime(swStoreWorkDataTableDO.getEndHandleTime());

        SwStoreWorkTableMappingDO swStoreWorkTableMappingDO = swStoreWorkTableMappingDao.selectByPrimaryKey(swStoreWorkDataTableDO.getTableMappingId(), enterpriseId);
        if (swStoreWorkTableMappingDO!=null){
            storeWorkDataTableDetailVO.setTableInfo(swStoreWorkTableMappingDO.getTableInfo());
        }
        if (StringUtils.isNotEmpty(swStoreWorkDataTableDO.getActualHandleUserId())){
            String actualHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, swStoreWorkDataTableDO.getActualHandleUserId());
            storeWorkDataTableDetailVO.setActualHandleUserName(actualHandleUserName);
        }
        List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnDOS = swStoreWorkDataTableColumnDao.selectColumnByDataTableId(enterpriseId, Arrays.asList(dataTableId),null, null, null);

        if (CollectionUtils.isEmpty(swStoreWorkDataTableColumnDOS)){
            return storeWorkDataTableDetailVO;
        }



        List<Long> metaTableIds = swStoreWorkDataTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getMetaTableId).collect(Collectors.toList());
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIds);
        if (CollectionUtils.isNotEmpty(tbMetaTableDOS)){
            storeWorkDataTableDetailVO.setDefaultResultColumn(tbMetaTableDOS.get(Constants.INDEX_ZERO).getDefaultResultColumn());
            storeWorkDataTableDetailVO.setTableProperty(tbMetaTableDOS.get(Constants.INDEX_ZERO).getTableProperty());
        }
        Map<Long, SwStoreWorkDataTableColumnDO> lastResultMap = new HashMap<>(16);
        //上次检查表店务记录数据
        SwStoreWorkDataTableDO lastTimeDataTableDO = swStoreWorkDataTableDao.getLastTimeDataTableDO(enterpriseId, swStoreWorkDataTableColumnDOS.get(Constants.INDEX_ZERO).getStoreId(), swStoreWorkDataTableColumnDOS.get(Constants.INDEX_ZERO).getMetaTableId());
        if (lastTimeDataTableDO!=null){
            List<SwStoreWorkDataTableColumnDO> tbDataStaTableColumnDOS = swStoreWorkDataTableColumnDao.selectByDataTableId(enterpriseId, lastTimeDataTableDO.getId());
            lastResultMap = tbDataStaTableColumnDOS.stream().collect(Collectors.toMap(SwStoreWorkDataTableColumnDO::getMetaColumnId, data->data));
        }

        List<TbMetaColumnResultDO> tbMetaColumnResultDOS = tbMetaColumnResultMapper.selectByMetaTableIdList(enterpriseId, Arrays.asList(swStoreWorkDataTableColumnDOS.get(Constants.INDEX_ZERO).getMetaTableId()));
        Map<Long, List<TbMetaColumnResultDO>> resultMap = tbMetaColumnResultDOS.stream().collect(Collectors.groupingBy(TbMetaColumnResultDO::getMetaColumnId));

        List<Long> staIds = tbMetaTableDOS.stream().filter(x -> !TableTypeUtil.isUserDefinedTable(x.getTableProperty(), STANDARD)).map(TbMetaTableDO::getId).collect(Collectors.toList());
        Map<Long, TbMetaStaTableColumnDO> executeDemandMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(staIds)){
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, staIds,Boolean.FALSE);
            executeDemandMap = tbMetaStaTableColumnDOS.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, data->data));
        }

        List<StoreWorkDataTableColumnVO> result = new ArrayList<>();
        Map<Long, SwStoreWorkDataTableColumnDO> finalLastResultMap = lastResultMap;
        Map<Long, CommentScoreRequest> finalCacheValueMap = cacheValueMap;
        Map<Long, TbMetaStaTableColumnDO> finalExecuteDemandMap = executeDemandMap;
        swStoreWorkDataTableColumnDOS.forEach(storeWorkDataTableColumnDO-> {
            CommentScoreRequest commentScoreRequest = finalCacheValueMap.get(storeWorkDataTableColumnDO.getId());
            StoreWorkDataTableColumnVO storeWorkDataTableColumnVO = new StoreWorkDataTableColumnVO();
            storeWorkDataTableColumnVO.setMetaColumnName(storeWorkDataTableColumnDO.getMetaColumnName());
            storeWorkDataTableColumnVO.setMetaColumnId(storeWorkDataTableColumnDO.getMetaColumnId());
            storeWorkDataTableColumnVO.setStoreWorkId(storeWorkDataTableColumnDO.getStoreWorkId());
            storeWorkDataTableColumnVO.setId(storeWorkDataTableColumnDO.getId());
            storeWorkDataTableColumnVO.setBusinessId(storeWorkDataTableColumnDO.getTcBusinessId());
            storeWorkDataTableColumnVO.setUserDefinedScore(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getUserDefinedScore());
            storeWorkDataTableColumnVO.setConfigType(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getConfigType());
            storeWorkDataTableColumnVO.setSupportScore(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getSupportScore());
            storeWorkDataTableColumnVO.setLowestScore(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getLowestScore());
            storeWorkDataTableColumnVO.setStoreId(storeWorkDataTableColumnDO.getStoreId());
            storeWorkDataTableColumnVO.setStoreName(storeWorkDataTableColumnDO.getStoreName());
            storeWorkDataTableColumnVO.setRegionId(storeWorkDataTableColumnDO.getRegionId());
            storeWorkDataTableColumnVO.setRegionPath(storeWorkDataTableColumnDO.getRegionPath());
            storeWorkDataTableColumnVO.setTaskQuestionStatus(storeWorkDataTableColumnDO.getTaskQuestionStatus());
            storeWorkDataTableColumnVO.setTaskQuestionId(storeWorkDataTableColumnDO.getTaskQuestionId());
            storeWorkDataTableColumnVO.setTableName(storeWorkDataTableColumnDO.getTableName());
            storeWorkDataTableColumnVO.setMetaTableId(storeWorkDataTableColumnDO.getMetaTableId());
            storeWorkDataTableColumnVO.setDescription(storeWorkDataTableColumnDO.getDescription());
            storeWorkDataTableColumnVO.setExecuteDemand(finalExecuteDemandMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(),new TbMetaStaTableColumnDO()).getExecuteDemand());
            storeWorkDataTableColumnVO.setCategoryName(storeWorkDataTableColumnDO.getCategoryName());
            storeWorkDataTableColumnVO.setSubmitStatus(storeWorkDataTableColumnDO.getSubmitStatus());
            storeWorkDataTableColumnVO.setCheckPics(commentScoreRequest!=null?commentScoreRequest.getCheckPics():storeWorkDataTableColumnDO.getCheckPics());
            storeWorkDataTableColumnVO.setCheckScore(commentScoreRequest!=null?commentScoreRequest.getCheckScore():storeWorkDataTableColumnDO.getCheckScore());
            storeWorkDataTableColumnVO.setCheckText(storeWorkDataTableColumnDO.getCheckText());
            storeWorkDataTableColumnVO.setCheckVideo(storeWorkDataTableColumnDO.getCheckVideo());
            storeWorkDataTableColumnVO.setColumnType(storeWorkDataTableColumnDO.getColumnType());
            storeWorkDataTableColumnVO.setScoreTimes(commentScoreRequest!=null?commentScoreRequest.getScoreTimes():storeWorkDataTableColumnDO.getScoreTimes());
            storeWorkDataTableColumnVO.setCommentContent(commentScoreRequest!=null?commentScoreRequest.getCommentContent():storeWorkDataTableColumnDO.getCommentContent());
            storeWorkDataTableColumnVO.setAwardTimes(commentScoreRequest!=null?commentScoreRequest.getAwardTimes():storeWorkDataTableColumnDO.getAwardTimes());
            storeWorkDataTableColumnVO.setCheckResult(commentScoreRequest!=null?commentScoreRequest.getCheckResult():storeWorkDataTableColumnDO.getCheckResult());
            storeWorkDataTableColumnVO.setCheckResultId(commentScoreRequest!=null?commentScoreRequest.getCheckResultId():storeWorkDataTableColumnDO.getCheckResultId());
            storeWorkDataTableColumnVO.setCheckResultName(commentScoreRequest!=null?commentScoreRequest.getCheckResultName():storeWorkDataTableColumnDO.getCheckResultName());
            storeWorkDataTableColumnVO.setColumnMaxAward(storeWorkDataTableColumnDO.getColumnMaxAward());
            storeWorkDataTableColumnVO.setColumnMaxScore(storeWorkDataTableColumnDO.getColumnMaxScore());
            storeWorkDataTableColumnVO.setColumnResultList(resultMap.get(storeWorkDataTableColumnDO.getMetaColumnId()));
            String lastStoreWorkResult = finalLastResultMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(), new SwStoreWorkDataTableColumnDO()).getCheckResult();
            storeWorkDataTableColumnVO.setLastPatrolStoreResult(lastStoreWorkResult);
            storeWorkDataTableColumnVO.setIsAiCheck(storeWorkDataTableColumnDO.getIsAiCheck());
            storeWorkDataTableColumnVO.setLastDataColumnId(finalLastResultMap.getOrDefault(storeWorkDataTableColumnDO.getMetaColumnId(), new SwStoreWorkDataTableColumnDO()).getId());
            setVOAiField(storeWorkDataTableColumnVO, storeWorkDataTableColumnDO);
            result.add(storeWorkDataTableColumnVO);
        });
        storeWorkDataTableDetailVO.setStoreWorkDataTableColumnVOS(result);
        boolean aiCommentCanRecheck = Constants.INDEX_ONE.equals(swStoreWorkDataTableDO.getIsAiProcess()) && (swStoreWorkDataTableDO.getAiStatus() & Constants.STORE_WORK_AI.AI_STATUS_COMMENTED) == 0;
        Boolean commentTabDisplayFlag = getCommentTabDisplayFlag(swStoreWorkDataTableDO.getCommentStatus(), swStoreWorkDataTableDO.getCompleteStatus(),
                swStoreWorkDataTableDO.getEndTime(), swStoreWorkDataTableDO.getCommentUserIds(), user.getUserId(), aiCommentCanRecheck);
        storeWorkDataTableDetailVO.setCommentTabDisplayFlag(commentTabDisplayFlag);
        storeWorkDataTableDetailVO.setIsAiProcess(swStoreWorkDataTableDO.getIsAiProcess());
        storeWorkDataTableDetailVO.setAiStatus(swStoreWorkDataTableDO.getAiStatus());
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, swStoreWorkDataTableDO.getMetaTableId());
        setAiStatusDisplayFlag(storeWorkDataTableDetailVO, tbMetaTableDO);
        return storeWorkDataTableDetailVO;
    }

    /**
     * 设置数据项VO对象的AI结果字段
     * @param vo 数据项VO对象
     * @param columnDO 数据项DO对象
     */
    private void setVOAiField(StoreWorkDataTableColumnVO vo, SwStoreWorkDataTableColumnDO columnDO) {
        vo.setAiCheckResult(columnDO.getAiCheckResult());
        vo.setAiCheckResultId(columnDO.getAiCheckResultId());
        vo.setAiCheckResultName(columnDO.getAiCheckResultName());
        vo.setAiCommentContent(columnDO.getAiCommentContent());
        vo.setAiCheckScore(columnDO.getAiCheckScore());
        vo.setAiStatus(columnDO.getAiStatus());
    }

    @Override
    public Boolean setTableColumnCache(String enterpriseId, CurrentUser user,Long dataTableId,List<CommentScoreRequest> requestList) {
        if (CollectionUtils.isEmpty(requestList)){
            return Boolean.TRUE;
        }
        //缓存数据
        String cacheKey = MessageFormat.format(RedisConstant.STORE_WORK_COMMENT_CACHE_KEY, enterpriseId, user.getUserId(), dataTableId);
        redisUtilPool.setString(cacheKey, JSONObject.toJSONString(requestList),60*60*24);
        return Boolean.TRUE;
    }

    @Override
    public void updateQuestionData(String enterpriseId, Long dataColumnId) {
        //查询数据项
        SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = swStoreWorkDataTableColumnDao.selectByPrimaryKey(dataColumnId, enterpriseId);
        if (swStoreWorkDataTableColumnDO==null){
            return;
        }
        Long dataTableId = swStoreWorkDataTableColumnDO.getDataTableId();
        //每个表的项不合格发工单数量汇总
        StoreWorkQuestionDataDTO storeWorkQuestionDataDTO = swStoreWorkDataTableColumnDao.tableQuestionDate(enterpriseId, dataTableId);
        SwStoreWorkDataTableDO sw = SwStoreWorkDataTableDO.builder().id(dataTableId).unHandleQuestionNum(storeWorkQuestionDataDTO.getHandleCount())
                .unApproveQuestionNum(storeWorkQuestionDataDTO.getRecheckCount()).questionNum(storeWorkQuestionDataDTO.getHandleCount()+
                        storeWorkQuestionDataDTO.getFinishCount()+storeWorkQuestionDataDTO.getRecheckCount())
                .finishQuestionNum(storeWorkQuestionDataDTO.getFinishCount()).build();
        swStoreWorkDataTableDao.updateByPrimaryKeySelective(sw,enterpriseId);
        //更新record表
        SwStoreWorkRecordDO swStoreWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, swStoreWorkDataTableColumnDO.getTcBusinessId());
        StoreWorkQuestionDataDTO storeWorkQuestionData = swStoreWorkDataTableDao.recordQuestionDate(enterpriseId, swStoreWorkRecordDO.getTcBusinessId());
        Integer unHandleQuestionNum = storeWorkQuestionData.getHandleCount();
        Integer unApproveQuestionNum = storeWorkQuestionData.getRecheckCount();
        Integer finishQuestionNum = storeWorkQuestionData.getFinishCount();
        Integer totalQuestionNum = unHandleQuestionNum + unApproveQuestionNum + finishQuestionNum;
        SwStoreWorkRecordDO storeWorkRecordDO = SwStoreWorkRecordDO.builder().id(swStoreWorkRecordDO.getId()).unHandleQuestionNum(unHandleQuestionNum)
                .unApproveQuestionNum(unApproveQuestionNum).finishQuestionNum(finishQuestionNum).questionNum(totalQuestionNum).build();
        swStoreWorkRecordDao.updateByPrimaryKeySelective(storeWorkRecordDO,enterpriseId);
    }


}
