package com.coolcollege.intelligent.service.share.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataDefTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.share.ShareUserMappingMapper;
import com.coolcollege.intelligent.dao.share.TaskShareMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataContentMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionRecordDetailVO;
import com.coolcollege.intelligent.model.share.TaskShareDO;
import com.coolcollege.intelligent.model.share.dto.CheckItemDO;
import com.coolcollege.intelligent.model.share.dto.CheckTableDO;
import com.coolcollege.intelligent.model.share.dto.TaskShareDTO;
import com.coolcollege.intelligent.model.share.dto.TaskShareInsertDTO;
import com.coolcollege.intelligent.model.share.enums.ShareTypeEnum;
import com.coolcollege.intelligent.model.share.enums.VisibleRangeEnum;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataContentDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.StorePersonDto;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.share.ShareService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShareServiceImpl  implements ShareService {
    @Resource
    private TaskShareMapper taskShareMapper;
    @Resource
    private ShareUserMappingMapper shareUserMappingMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private TaskSubMapper taskSubMapper;

    @Resource
    private TaskMappingMapper taskMappingMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TbDataDefTableColumnMapper tbDataDefTableColumnMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;

    @Resource
    private TbDisplayTableDataContentMapper tbDisplayTableDataContentMapper;

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    @Autowired
    private QuestionRecordService questionRecordService;

    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;
    private final String VISIBLE_RANGE_ALL="ALL";
    private final String VISIBLE_RANGE_PART="PART";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addShare(String eId, List<TaskShareInsertDTO> taskShareInsertDTOList) {
        TaskShareInsertDTO entity = taskShareInsertDTOList.get(0);
        Long taskId = entity.getTaskId();
        TaskParentDO taskParentDO =taskParentMapper.selectTaskById(eId,taskId);
        Integer visibleRange  = entity.getVisibleRange();
        List<String> userIdList = entity.getUserIdList();
        String userId = UserHolder.getUser().getUserId();
        taskShareInsertDTOList = taskShareUserMapping(eId, taskShareInsertDTOList, entity, taskId, visibleRange, userIdList, userId);
        List<TaskShareDO> taskShareDOList = new ArrayList<>();

        //新标准陈列分享
        if(StringUtils.equals(ShareTypeEnum.TB_DISPLAY_STANDARD.getShareType(),entity.getShareType())){
            taskShareDOList = tbDisplayStandardShareMap(eId, taskShareInsertDTOList, taskId, taskParentDO,visibleRange);
        }
        if(StringUtils.equals(ShareTypeEnum.PATROL.getShareType(),entity.getShareType())){
            //获取子任务idList
            taskShareDOList = patrolShareMap(eId, taskShareInsertDTOList,taskParentDO,visibleRange);
        }
        taskShareMapper.batchInsertTaskShareDO(eId,taskShareDOList);
        if(visibleRange != 0){
            shareUserMappingMapper.batchInsertShareIdAndUserId(eId, taskShareInsertDTOList);
        }
        return Boolean.TRUE;
    }

    private List<TaskShareDO> patrolShareMap(String eId, List<TaskShareInsertDTO> taskShareInsertDTOList,TaskParentDO taskParentDO,Integer visibleRange) {
        List<TaskShareDO> result = new ArrayList<>();
        //通过子任务id获取巡店记录信息
        List<Long> businessIdList = taskShareInsertDTOList.stream().map(taskShareInsertDTO -> taskShareInsertDTO.getBusinessId()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(businessIdList)){
            List<TbDataTableDO> tbDataTableDOS = tbDataTableMapper.getListByBusinessIdList(eId,businessIdList, MetaTableConstant.BusinessTypeConstant.PATROL_STORE);
            Map<Long,List<TbDataTableDO>> subTaskTableMap = tbDataTableDOS.stream().collect(Collectors.groupingBy(data -> data.getBusinessId()));
            List<TbDataDefTableColumnDO> tbDataDefTableColumnDOS = tbDataDefTableColumnMapper.getListByRecordIdList(eId,businessIdList, null);
            List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnMapper.getListByRecordIdList(eId,businessIdList);
            Map<Long,List<TbDataDefTableColumnDO>> defTableMap = CollectionUtils.emptyIfNull(tbDataDefTableColumnDOS).stream()
                    .collect(Collectors.groupingBy(data -> data.getMetaTableId()));
            Map<Long,List<TbDataStaTableColumnDO>> staTableMap = CollectionUtils.emptyIfNull(tbDataStaTableColumnDOS).stream()
                    .collect(Collectors.groupingBy(data -> data.getMetaTableId()));

            List<CheckTableDO> checkTableDOList = new ArrayList<>(taskShareInsertDTOList.size());
            result =  taskShareInsertDTOList.stream()
                    .map(t -> {
                        TaskShareDO taskShareDO = new TaskShareDO();
                        taskShareDO.setShareId(t.getShareId());
                        taskShareDO.setCreateTime(System.currentTimeMillis());
                        taskShareDO.setCreateUser(UserHolder.getUser().getUserId());
                        taskShareDO.setShareStoreLabel(t.getShareStoreLabel());
                        taskShareDO.setBusinessId(t.getBusinessId());
                        String shareType = ShareTypeEnum.getShareTypeEnumByType(t.getShareType()).getShareType();
                        taskShareDO.setShareType(shareType);
                        if(taskParentDO != null){
                            taskShareDO.setShareContent(taskParentDO.getTaskDesc());
                            taskShareDO.setShareTopic(taskParentDO.getTaskName());
                        }
                        taskShareDO.setTaskScore(new BigDecimal(Constants.ZERO_STR));
                        taskShareDO.setStoreId(t.getStoreId());
                        taskShareDO.setTaskId(t.getTaskId());
                        taskShareDO.setTaskSubId(t.getTaskSubId());
                        if(visibleRange == 0){
                            taskShareDO.setVisibleRange(VISIBLE_RANGE_ALL);
                        }else {
                            taskShareDO.setVisibleRange(VISIBLE_RANGE_PART);

                        }
                        //组装detail,1.获取检查表，2.获取检查项
                        List<TbDataTableDO> tbDataTableDOList = subTaskTableMap.get(t.getBusinessId());
                        List<String> picList = new ArrayList<>();
                        CollectionUtils.emptyIfNull(tbDataTableDOList)
                                .forEach( tbDataTableDO -> {
                                    CheckTableDO checkTableDO = new CheckTableDO();
                                    checkTableDO.setCheckTableName(tbDataTableDO.getTableName());
                                    List<CheckItemDO> checkItemDOList = new ArrayList<>();
                                    if(!TableTypeUtil.isUserDefinedTable(tbDataTableDO.getTableProperty(), tbDataTableDO.getTableType())){
                                        List<TbDataStaTableColumnDO> tbDataStaTableColumnList = staTableMap.get(tbDataTableDO.getMetaTableId());
                                        CollectionUtils.emptyIfNull(tbDataStaTableColumnList).stream().forEach(tbDataStaTableColumnDO -> {
                                            CheckItemDO checkItemDO = new CheckItemDO();
                                            checkItemDO.setCheckItemName(tbDataStaTableColumnDO.getMetaColumnName());
                                            checkItemDO.setDescription(tbDataStaTableColumnDO.getCheckText());
                                            if(StrUtil.isNotBlank(tbDataStaTableColumnDO.getCheckPics())){
                                                picList.add(tbDataStaTableColumnDO.getCheckPics());
                                            }
                                            BigDecimal a = tbDataStaTableColumnDO.getCheckScore();
                                            BigDecimal b = a==null ?taskShareDO.getTaskScore():a.add(taskShareDO.getTaskScore());
                                            taskShareDO.setTaskScore(b);
                                            checkItemDO.setPicture(tbDataStaTableColumnDO.getCheckPics());
                                            checkItemDO.setCheckItemId(tbDataStaTableColumnDO.getSubTaskId());
                                            checkItemDOList.add(checkItemDO);
                                        });
                                    }else {
                                        List<TbDataDefTableColumnDO> tbDataDefTableColumnDOList = defTableMap.get(tbDataTableDO.getMetaTableId());
                                        CollectionUtils.emptyIfNull(tbDataDefTableColumnDOList).stream().forEach(tbDataDefTableColumnDO -> {
                                            CheckItemDO checkItemDO = new CheckItemDO();
                                            checkItemDO.setCheckItemName(tbDataDefTableColumnDO.getMetaColumnName());
                                            checkItemDO.setCheckItemId(tbDataDefTableColumnDO.getSubTaskId());
                                            checkItemDOList.add(checkItemDO);
                                        });
                                    }
                                    checkTableDO.setCheckItemList(checkItemDOList);
                                    checkTableDOList.add(checkTableDO);
                                });
                        if(CollUtil.isNotEmpty(picList)){
                            taskShareDO.setSharePicture(CollUtil.join(picList, ","));
                        }
                        taskShareDO.setDetail(JSON.toJSONString(checkTableDOList));
                        return taskShareDO;
                    }).collect(Collectors.toList());

        }

        //获取数据检查项详情
        //组装成DO
        return result;
    }

    private List<TaskShareInsertDTO> taskShareUserMapping(String eId, List<TaskShareInsertDTO> taskShareInsertDTOList, TaskShareInsertDTO entity, Long taskId, Integer visibleRange, List<String> userIdList, String userId) {
        //分享给全公司
        if (VisibleRangeEnum.ALL_ENTERPRISE.getCode() == visibleRange){
            List<String> finalUserIdList = userIdList;
            taskShareInsertDTOList = taskShareInsertDTOList.stream()
                    .map(TaskShareInsertDTO ->
                            mapTaskShareQueryDTO(finalUserIdList, TaskShareInsertDTO))
                    .collect(Collectors.toList());
        }
        //分享给指定人
        else if (VisibleRangeEnum.DESIGNATED_PERSON.getCode() == visibleRange){
            //先判断是否存在分享人
            boolean exitUser = CollectionUtils.emptyIfNull(userIdList).stream().anyMatch(userId::equals);
            //不包含就加入到list中
            if(!exitUser){
                userIdList.add(userId);
            }
            List<String> finalUserIdList = userIdList;
            taskShareInsertDTOList = taskShareInsertDTOList.stream()
                    .map(TaskShareInsertDTO -> mapTaskShareQueryDTO(finalUserIdList, TaskShareInsertDTO))
                    .collect(Collectors.toList());
        }
        //分享给任务相关人
        else if(VisibleRangeEnum.TASK.getCode() == entity.getVisibleRange()){
            List<String> storeIdList = taskShareInsertDTOList.stream()
                    .map(t -> t.getStoreId())
                    .collect(Collectors.toList());

            //根据门店和父任务id获取人员信息
            List<StorePersonDto> storePersonDtoList  = unifyTaskStoreService.selectTaskPersonByTaskAndStore(eId,storeIdList, taskId);
            Map<String,StorePersonDto> personDtoMap = storePersonDtoList.stream().collect(Collectors.toMap(e -> e.getStoreId() + "#" + e.getLoopCount(), data -> data,(a, b)->a));

            taskShareInsertDTOList = taskShareInsertDTOList.stream()
                    .map(TaskShareInsertDTO -> mapTaskShareQueryDTO(userId, personDtoMap, TaskShareInsertDTO))
                    .collect(Collectors.toList());
        }
        return taskShareInsertDTOList;
    }



    private TaskShareInsertDTO mapTaskShareQueryDTO(List<String> finalUserIdList, TaskShareInsertDTO taskShareInsertDTO) {
        taskShareInsertDTO.setShareId(UUIDUtils.get32UUID());
        taskShareInsertDTO.setUserIdList(finalUserIdList);
        if(Objects.isNull(taskShareInsertDTO.getLoopCount())){
            taskShareInsertDTO.setLoopCount(Constants.LONG_ONE);
        }
        return taskShareInsertDTO;
    }

    private TaskShareInsertDTO mapTaskShareQueryDTO(String userId, Map<String, StorePersonDto> personDtoMap, TaskShareInsertDTO taskShareInsertDTO) {
        StorePersonDto storePersonDto = personDtoMap.get(taskShareInsertDTO.getStoreId()+ "#" + taskShareInsertDTO.getLoopCount());
        List<String> userList =  storePersonDto==null? new ArrayList<>() : storePersonDto.getUserIdList();
        //判断是否包含分享人
        boolean exitUser = userList.stream().anyMatch(u -> userId.equals(u));
        //不包含就加入到list中
        if(!exitUser){
            userList.add(userId);
        }
        return mapTaskShareQueryDTO(userList, taskShareInsertDTO);
    }


    @Override
    public Map<String, Object> getShareList(String eId, String shareId,Integer pageSize,Integer pageNum,String searchKey) {
        String userId = UserHolder.getUser().getUserId();
        List<String> shareIdList = shareUserMappingMapper.getShareIdListByUserId(eId,userId);
        List<String> shareIds = taskShareMapper.getAllVisibleRangeShare(eId);
        shareIdList.addAll(shareIds);
        List<TaskShareDTO> taskShareDOList = getTaskShareDTO(eId,shareIdList,searchKey, pageNum,pageSize);
        if (CollUtil.isEmpty(taskShareDOList)) {
            return PageHelperUtil.getPageInfo(new PageInfo<>());
        }
        return PageHelperUtil.getPageInfo(new PageInfo<>(taskShareDOList));
    }


    @Override
    public Boolean delete(String eId, String shareId) {
        java.lang.String userId = UserHolder.getUser().getUserId();
        return null;
    }

    @Override
    public Boolean update(String eId, String shareId) {
        return null;
    }

    @Override
    public Object batchShare(String eId, TaskShareInsertDTO taskShareInsertDTO) {
        Map<String,Object> resultMap = new HashMap<>();
        List<TaskSubDO> taskSubDOS = taskSubMapper.getTaskSubDOList(eId, taskShareInsertDTO.getTaskId());
        List<TaskShareInsertDTO> taskShareInsertDTOList = taskSubDOS.stream()
                .map(t -> mapTaskShareQueryDTO(taskShareInsertDTO, t)).collect(Collectors.toList());
        if(taskShareInsertDTOList.size()<=0){
            resultMap.put("message","该任务不存在已完成的子任务");
            resultMap.put("isSuccess",false);
            return resultMap;
        }try {
            this.addShare(eId, taskShareInsertDTOList);
        }catch (Exception e){
            log.error("分享失败，",e);
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "分享失败");
        }
        resultMap.put("message","分享成功");
        resultMap.put("isSuccess",true);
        return resultMap;
    }

    private TaskShareInsertDTO mapTaskShareQueryDTO(TaskShareInsertDTO taskShareInsertDTO, TaskSubDO t) {
        TaskShareInsertDTO taskShareQuery = new TaskShareInsertDTO();
        taskShareQuery.setStoreId(t.getStoreId());
        taskShareQuery.setTaskSubId(t.getId());
        taskShareQuery.setShareType(taskShareInsertDTO.getShareType());
        taskShareQuery.setUserIdList(taskShareInsertDTO.getUserIdList());
        taskShareQuery.setVisibleRange(taskShareInsertDTO.getVisibleRange());
        taskShareQuery.setShareStoreLabel(taskShareInsertDTO.getShareStoreLabel());
        taskShareQuery.setTaskId(t.getUnifyTaskId());
        taskShareQuery.setLoopCount(t.getLoopCount());
        return taskShareQuery;
    }


    @Override
    public TaskShareDTO getShareDetail(String eId, String shareId) {

        List<TaskShareDTO> taskShareDTOS = getTaskShareDTO(eId, Arrays.asList(shareId),null,null,null);

       TaskShareDTO taskShareDTO =  taskShareDTOS.get(0);
       String detail = taskShareMapper.getDetail(eId,shareId);
        //标准陈列类型
        List<CheckTableDO> checkTableDOS = JSON.parseArray(detail,CheckTableDO.class);
        taskShareDTO.setCheckTableList(checkTableDOS);
        return  taskShareDTO;
    }



    @Override
    public Boolean singleShare(String eId, TaskShareInsertDTO taskShareInsertDTO) {
        List<TaskShareInsertDTO> taskShareInsertDTOList = new ArrayList();
        //查询子任务状态是否完成
        TaskSubDO taskSubDO = taskSubMapper.getTaskSubDOListById(eId, taskShareInsertDTO.getTaskSubId());
        //只分享已完成的任务
        if(ObjectUtil.isNotNull(taskSubDO)){
            taskShareInsertDTO.setTaskId(taskSubDO.getUnifyTaskId());
            taskShareInsertDTO.setStoreId(taskSubDO.getStoreId());
            taskShareInsertDTO.setLoopCount(taskSubDO.getLoopCount());
            taskShareInsertDTOList.add(taskShareInsertDTO);
            try {
                this.addShare(eId, taskShareInsertDTOList);
            }catch (Exception e){
                log.error("分享失败{}",e);
                return Boolean.FALSE;
            }
        }
        return true;
    }

    @Override
    public void patrolStoreShare(String eId, TaskShareInsertDTO taskShareInsertDTO) {
        List<TaskShareInsertDTO> taskShareInsertDTOList = new ArrayList();
        List<Long> businessIdList = taskShareInsertDTO.getBusinessIdList();
        List<String> userIdList = taskShareInsertDTO.getUserIdList();
        List<TbPatrolStoreRecordDO> recordDOList = tbPatrolStoreRecordMapper.selectByIds(eId,businessIdList);
        recordDOList.stream().filter(data -> data.getSignEndTime()!=null).forEach(data -> {
            TaskShareInsertDTO entity = new TaskShareInsertDTO();
            entity.setStoreId(data.getStoreId());
            entity.setUserIdList(userIdList);
            entity.setTaskSubId(data.getSubTaskId());
            entity.setTaskId(data.getTaskId());
            entity.setBusinessId(data.getId());
            entity.setVisibleRange(taskShareInsertDTO.getVisibleRange());
            entity.setShareStoreLabel(taskShareInsertDTO.getShareStoreLabel());
            entity.setShareType(taskShareInsertDTO.getShareType());
            entity.setLoopCount(data.getLoopCount());
            taskShareInsertDTOList.add(entity);
        });
        if(CollectionUtils.isNotEmpty(taskShareInsertDTOList)){
            this.addShare(eId,taskShareInsertDTOList);
            return;
        }
        throw new ServiceException("分享失败");
    }

    @Override
    public void circlesShare(String eId, TaskShareInsertDTO taskShareInsert) {
        String shareType = taskShareInsert.getShareType();
        ShareTypeEnum shareTypeEnum = ShareTypeEnum.getShareTypeEnumByType(shareType);
        switch (shareTypeEnum){
            case STANDARD:
                break;
            case TB_DISPLAY_STANDARD:
                break;
            case QUESTION_ORDER:
                questionOrderShare(eId, taskShareInsert);
                break;
            case PATROL:
                patrolStoreShare(eId, taskShareInsert);
                break;
            default:
                throw new ServiceException("分享类型不支持");
        }
    }

    private void questionOrderShare(String eId, TaskShareInsertDTO taskShareInsert) {
        //单个分享 storeId taskId loopCount 不能为空
        if(Objects.isNull(taskShareInsert.getTaskId()) || Objects.isNull(taskShareInsert.getStoreId()) || Objects.isNull(taskShareInsert.getLoopCount())){
            return;
        }
        TaskParentDO taskParentDO =taskParentMapper.selectTaskById(eId, taskShareInsert.getTaskId());
        Integer visibleRange = taskShareInsert.getVisibleRange();
        List<TaskShareDO> taskShareDOList = new ArrayList<>();
        TaskShareDO taskShare = new TaskShareDO();
        taskShare.setShareId(UUIDUtils.get32UUID());
        taskShare.setCreateTime(System.currentTimeMillis());
        taskShare.setCreateUser(UserHolder.getUser().getUserId());
        taskShare.setShareStoreLabel(taskShareInsert.getShareStoreLabel());
        taskShare.setBusinessId(taskShareInsert.getBusinessId());
        taskShare.setShareType(taskShareInsert.getShareType());
        taskShare.setLoopCount(taskShareInsert.getLoopCount());
        if(taskParentDO != null){
            taskShare.setShareContent(taskParentDO.getTaskDesc());
            taskShare.setShareTopic(taskParentDO.getTaskName());
        }
        taskShare.setTaskScore(new BigDecimal(Constants.ZERO_STR));
        taskShare.setStoreId(taskShareInsert.getStoreId());
        taskShare.setTaskId(taskShareInsert.getTaskId());
        taskShare.setTaskSubId(taskShareInsert.getTaskSubId());
        if(visibleRange == Constants.ZERO){
            taskShare.setVisibleRange(VISIBLE_RANGE_ALL);
        }else {
            taskShare.setVisibleRange(VISIBLE_RANGE_PART);

        }
        List<String> picList = new ArrayList<>();
        if(CollUtil.isNotEmpty(picList)){
            //taskShare.setSharePicture(CollUtil.join(picList, ","));
        }
        TbQuestionRecordDetailVO detail = questionRecordService.detail(eId, taskShareInsert.getTaskId(), taskShareInsert.getStoreId(), null, taskShareInsert.getLoopCount());
        taskShare.setDetail(JSON.toJSONString(detail));
        taskShareDOList.add(taskShare);
        taskShareMapper.batchInsertTaskShareDO(eId, taskShareDOList);
        if(visibleRange != 0 && CollectionUtils.isNotEmpty(taskShareInsert.getUserIdList())){
            shareUserMappingMapper.batchInsertMapping(eId, taskShare.getShareId(), taskShareInsert.getUserIdList());
        }

    }

    private List<TaskShareDTO> getTaskShareDTO(String eId,List<String> shareIdList,String searchKey, Integer pageNum ,Integer pageSize){
        List<TaskShareDTO> taskShareDOList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(shareIdList)){
            if(pageNum != null && pageSize != null){
                PageHelper.startPage(pageNum,pageSize);
            }
            taskShareDOList = taskShareMapper.selectByShareIdList(eId, shareIdList,searchKey);
        }
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        for (TaskShareDTO taskShareDTO : taskShareDOList) {
            TaskShareDO taskShareDO = taskShareDTO.getTaskShare();
            taskShareDTO.setShareType(taskShareDO.getShareType());
            taskShareDTO.setShareName(ShareTypeEnum.getShareTypeEnumByType(taskShareDO.getShareType()).getShareName());
            taskShareDTO.setShareTime(dateformat.format(taskShareDO.getCreateTime()));
        }
        return taskShareDOList;
    }

    private List<TaskShareDO> tbDisplayStandardShareMap(String eId, List<TaskShareInsertDTO> taskShareInsertDTOList, Long taskId, TaskParentDO taskParentDO,Integer visibleRange) {
        List<TaskShareDO> taskShareDOList = null;

        //通过子任务id获取巡店记录信息
        List<Long> subTaskIdList = taskShareInsertDTOList.stream().map(TaskShareInsertDTO::getTaskSubId).collect(Collectors.toList());


        if(CollectionUtils.isNotEmpty(subTaskIdList)) {
            List<Long> recordIds = new ArrayList<>();
            List<TaskSubDO> taskSubList = taskSubMapper.getDOByIdList(eId, subTaskIdList);
            Map<Long, TbDisplayTableRecordDO> recordTaskMap = new HashMap<>();
            for(TaskSubDO taskSubDO : taskSubList){
                TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(eId,taskSubDO.getUnifyTaskId(),taskSubDO.getStoreId(),taskSubDO.getLoopCount());
                if(tbDisplayTableRecordDO != null){
                    recordIds.add(tbDisplayTableRecordDO.getId());
                    recordTaskMap.put(taskSubDO.getId(), tbDisplayTableRecordDO);
                }
            }

            List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList = tbDisplayTableDataColumnMapper.listByRecordIdList(eId, recordIds, null);

            List<TbDisplayTableDataContentDO> displayTableDataContentList = tbDisplayTableDataContentMapper.listByRecordIdList(eId, recordIds, null);

            Set<Long> dataTableIdList = CollectionUtils.emptyIfNull(tbDisplayTableDataColumnDOList).stream().map(TbDisplayTableDataColumnDO::getDataTableId).collect(Collectors.toSet());

            Set<Long> metaTableIdList = CollectionUtils.emptyIfNull(tbDisplayTableDataColumnDOList).stream().map(TbDisplayTableDataColumnDO::getMetaTableId).collect(Collectors.toSet());

            List<TbMetaDisplayTableColumnDO> metaTableColumnList = tbMetaDisplayTableColumnMapper.selectColumnListByTableIdList(eId, new ArrayList<>(metaTableIdList));

            Map<Long, TbMetaDisplayTableColumnDO> metaTableColumnMap = CollectionUtils.emptyIfNull(metaTableColumnList).stream().collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId,Function.identity()));


            List<TbDataTableDO> dataTableList = tbDataTableMapper.selectListByIdList(eId, new ArrayList<>(dataTableIdList));

            Map<Long, List<TbDataTableDO>> dataTableMap = CollectionUtils.emptyIfNull(dataTableList).stream().collect(Collectors.groupingBy(TbDataTableDO::getBusinessId));

            Map<Long, List<TbDisplayTableDataColumnDO>> tableColumnMap = CollectionUtils.emptyIfNull(tbDisplayTableDataColumnDOList).stream()
                    .collect(Collectors.groupingBy(TbDisplayTableDataColumnDO::getDataTableId));

            Map<Long, List<TbDisplayTableDataContentDO>> tableContentMap = CollectionUtils.emptyIfNull(displayTableDataContentList).stream()
                    .collect(Collectors.groupingBy(TbDisplayTableDataContentDO::getDataTableId));


            List<CheckTableDO> checkTableDOList = new ArrayList<>(taskShareInsertDTOList.size());
            taskShareDOList = taskShareInsertDTOList.stream()
                    .map(t -> {
                        TaskShareDO taskShareDO = new TaskShareDO();
                        taskShareDO.setShareId(t.getShareId());
                        taskShareDO.setCreateTime(System.currentTimeMillis());
                        taskShareDO.setCreateUser(UserHolder.getUser().getUserId());
                        taskShareDO.setShareStoreLabel(t.getShareStoreLabel());
                        TbDisplayTableRecordDO recordDO = recordTaskMap.get(t.getTaskSubId());
                        taskShareDO.setBusinessId( recordDO !=null ? recordDO.getId(): 0L);
                        taskShareDO.setTaskScore(recordDO == null? null :recordDO.getScore());
                        String shareType = ShareTypeEnum.getShareTypeEnumByType(t.getShareType()).getShareType();
                        taskShareDO.setShareType(shareType);
                        if (taskParentDO != null) {
                            taskShareDO.setShareContent(taskParentDO.getTaskDesc());
                            taskShareDO.setShareTopic(taskParentDO.getTaskName());
                        }
                        taskShareDO.setStoreId(t.getStoreId());
                        taskShareDO.setTaskId(t.getTaskId());
                        taskShareDO.setTaskSubId(t.getTaskSubId());
                        if (visibleRange == 0) {
                            taskShareDO.setVisibleRange(VISIBLE_RANGE_ALL);
                        } else {
                            taskShareDO.setVisibleRange(VISIBLE_RANGE_PART);

                        }
                        //组装detail,1.获取检查表，2.获取检查项
                        List<TbDataTableDO> tbDataTableDOList = dataTableMap.get(taskShareDO.getBusinessId());
                        List<String> picList = new ArrayList<>();
                        CollectionUtils.emptyIfNull(tbDataTableDOList)
                                .forEach(tbDataTableDO -> {
                                    CheckTableDO checkTableDO = new CheckTableDO();
                                    checkTableDO.setCheckTableName(tbDataTableDO.getTableName());
                                    List<CheckItemDO> checkItemDOList = new ArrayList<>();
                                    List<TbDisplayTableDataColumnDO> tbDataTableColumnList = tableColumnMap.get(tbDataTableDO.getId());
                                    CollectionUtils.emptyIfNull(tbDataTableColumnList).forEach(tbDataTableColumnDO -> {

                                        TbMetaDisplayTableColumnDO tableColumnDO = metaTableColumnMap.get(tbDataTableColumnDO.getMetaColumnId());
                                        CheckItemDO checkItemDO = new CheckItemDO();
                                        checkItemDO.setCheckItemName(tableColumnDO.getColumnName());
                                        checkItemDO.setDescription(tbDataTableColumnDO.getRemark());
                                        if (StrUtil.isNotBlank(tbDataTableColumnDO.getPhotoArray())) {
                                            String pic = getSharePic(tbDataTableColumnDO.getPhotoArray());
                                            if(org.apache.commons.lang3.StringUtils.isNotBlank(pic)){
                                                picList.add(getSharePic(tbDataTableColumnDO.getPhotoArray()));
                                            }
                                        }
                                        checkItemDO.setPicture(tbDataTableColumnDO.getPhotoArray());
                                        checkItemDO.setCheckItemId(tbDataTableColumnDO.getId());
                                        checkItemDOList.add(checkItemDO);
                                    });
                                    //检查内容
                                    List<TbDisplayTableDataContentDO> tbDataTableContentList = tableContentMap.get(tbDataTableDO.getId());
                                    CollectionUtils.emptyIfNull(tbDataTableContentList).forEach(displayTableDataContentDO -> {
                                        TbMetaDisplayTableColumnDO tableColumnDO = metaTableColumnMap.get(displayTableDataContentDO.getMetaContentId());
                                        CheckItemDO checkItemDO = new CheckItemDO();
                                        checkItemDO.setDescription(displayTableDataContentDO.getRemark());
                                        if(tableColumnDO != null){
                                            checkItemDO.setCheckItemName(tableColumnDO.getColumnName());
                                        }
                                        if (StrUtil.isNotBlank(displayTableDataContentDO.getPhotoArray())) {
                                            String pic = getSharePic(displayTableDataContentDO.getPhotoArray());
                                            if(org.apache.commons.lang3.StringUtils.isNotBlank(pic)){
                                                picList.add(getSharePic(displayTableDataContentDO.getPhotoArray()));
                                            }
                                        }
                                        checkItemDO.setPicture(displayTableDataContentDO.getPhotoArray());
                                        checkItemDO.setCheckItemId(displayTableDataContentDO.getId());
                                        checkItemDOList.add(checkItemDO);
                                    });


                                    checkTableDO.setCheckItemList(checkItemDOList);
                                    checkTableDOList.add(checkTableDO);
                                });
                        if (CollUtil.isNotEmpty(picList)) {
                            taskShareDO.setSharePicture(CollUtil.join(picList, ","));
                        }
                        taskShareDO.setDetail(JSON.toJSONString(checkTableDOList));
                        return taskShareDO;
                    }).collect(Collectors.toList());

        }
        //获取数据检查项详情
        //组装成DO
        return taskShareDOList;
    }


    private String getSharePic(String photoArray){
        List<String> picList = new ArrayList<>();
        List<JSONObject> array = JSONArray.parseArray(photoArray, JSONObject.class);
        for(JSONObject jsonObject : array){
            picList.add(org.apache.commons.lang3.StringUtils.isNotBlank(jsonObject.getStr("finalUrl")) ? jsonObject.getStr("finalUrl") : jsonObject.getStr("handleUrl"));
        }
        if(CollectionUtils.isEmpty(picList)){
            return "";
        }
        return CollUtil.join(picList, ",");
    }
}