package com.coolcollege.intelligent.service.picture.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.patrol.PatrolAITypeEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.*;
import com.coolcollege.intelligent.dao.patrolstore.dao.AiPictureResultMappingDao;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordExpandMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionHistoryDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataContentMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnResultDTO;
import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import com.coolcollege.intelligent.model.patrolstore.*;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreCheckVO;
import com.coolcollege.intelligent.model.picture.PictureCenterStoreDO;
import com.coolcollege.intelligent.model.picture.query.PictureCenterQuery;
import com.coolcollege.intelligent.model.picture.vo.*;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordExpandDO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionHistoryVO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComptRegionStoreVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataContentDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.picture.PictureCenterService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @Description: 图片中心
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Service
@Slf4j
public class PictureCenterServiceImpl implements PictureCenterService {

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Autowired
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Autowired
    private TbMetaTableMapper tbMetaTableMapper;

    @Autowired
    private TbMetaStaTableColumnMapper metaStaTableColumnMapper;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Autowired
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;

    @Resource
    private TbDisplayTableDataContentMapper displayTableDataContentMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;

    @Resource
    private TbPatrolStorePictureMapper tbPatrolStorePictureMapper;

    @Resource
    private StoreSceneMapper storeSceneMapper;

    @Resource
    private QuestionRecordDao questionRecordDao;

    @Resource
    private QuestionHistoryDao questionHistoryDao;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private AiPictureResultMappingDao aiPictureResultMappingDao;

    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private TbQuestionRecordExpandMapper tbQuestionRecordExpandMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private TbPatrolStoreCheckMapper patrolStoreCheckMapper;

    @Resource
    private SelectionComponentService selectionComponentService;

    @Resource
    private TbCheckDataStaColumnMapper checkDataStaColumnMapper;

    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;

    @Resource
    private TbMetaTableService metaTableService;

    @Override
    public PageInfo<PictureCenterVO> getRecordByTaskName(String enterpriseId, PictureCenterQuery query) {
        setQueryRegionPath(enterpriseId,query);
        //获取巡店记录
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList = tbPatrolStoreRecordMapper.getRecordByTaskName(enterpriseId,query.getRegionPath(),
                query.getStoreIdList(),query.getMetaTableId(),query.getTaskName(),query.getBeginDate(),query.getEndDate(),
                query.getCompleteBeginDate(), query.getCompleteEndDate(),
                query.getTaskType(), query.getRegionPathList(), 1);
        if(CollectionUtils.isEmpty(tbPatrolStoreRecordList)){
            return new PageInfo<>();
        }
        PageInfo pageInfo = new PageInfo(tbPatrolStoreRecordList);

        List<PictureCenterVO> resultList = new ArrayList<>();
        List<Long> recordIdList = tbPatrolStoreRecordList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        List<Long> metaTableIdList = new ArrayList<>();
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, new ArrayList<>(recordIdList), PATROL_STORE);
        if(query.getMetaTableId() != null){
            dataTableList = dataTableList.stream().filter(tbDataTableDO -> tbDataTableDO.getMetaTableId().equals(query.getMetaTableId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(dataTableList)) {
            metaTableIdList = dataTableList.stream().map(TbDataTableDO::getMetaTableId).distinct().collect(Collectors.toList());
        }

        //根据业务id集合获取标准检查项数据
        List<TbDataStaTableColumnDO> tbDataStaTableColumnList = tbDataStaTableColumnMapper.selectByBusinessIdList(enterpriseId,recordIdList,query.getMetaColumnIdList());
        List<TbMetaTableDO> metaTableList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(metaTableIdList)){
            metaTableList = tbMetaTableMapper.selectByMetaTableIdListAll(enterpriseId,null,metaTableIdList);
        }
        List<String> userIdList = tbDataStaTableColumnList.stream().filter(x -> StringUtils.isNotEmpty(x.getSupervisorId())).map(TbDataStaTableColumnDO::getSupervisorId).collect(Collectors.toList());
        List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, userIdList);
        Map<String, String> userMap = enterpriseUserSingleDTOS.stream().collect(Collectors.toMap(EnterpriseUserSingleDTO::getUserId, EnterpriseUserSingleDTO::getUserName));
        Map<Long, TbMetaTableDO> metaTableMap = ListUtils.emptyIfNull(metaTableList).stream()
                .collect(Collectors.toMap(TbMetaTableDO::getId, data -> data, (a, b) -> a));
        if(CollectionUtils.isNotEmpty(tbDataStaTableColumnList)){
            List<PictureCenterColumnVO> pictureCenterColumnVoList = tbDataStaTableColumnList.stream()
                    .map(e -> transPictureCenterColumn(e,userMap)).collect(Collectors.toList());
            if(query.getMetaTableId() != null){
                pictureCenterColumnVoList = tbDataStaTableColumnList.stream().filter(o->query.getMetaTableId().equals(o.getMetaTableId()))
                        .map(e -> transPictureCenterColumn(e,userMap)).collect(Collectors.toList());
            }
            Map<Long, List<PictureCenterColumnVO>> groupMap = pictureCenterColumnVoList.stream()
                    .collect(Collectors.groupingBy(PictureCenterColumnVO::getBusinessId));

            Map<Long, List<PictureCenterColumnVO>> groupDataTableMap = pictureCenterColumnVoList.stream()
                    .collect(Collectors.groupingBy(PictureCenterColumnVO::getDataTableId));

            //构建多表返回参数对象
            Map<Long, List<PictureCenterTableVO>>  pictureCenterTableMap= dataTableList.stream().map(dataTableDO -> PictureCenterTableVO.builder().metaTableId(dataTableDO.getMetaTableId()).businessId(dataTableDO.getBusinessId())
                    .metaTableName(dataTableDO.getTableName()).pictureCenterColumnList(groupDataTableMap.get(dataTableDO.getId())).build()).collect(Collectors.toList()).stream()
                    .collect(Collectors.groupingBy(PictureCenterTableVO::getBusinessId));
            if(query.getMetaTableId() != null){
                TbMetaTableDO tempMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, query.getMetaTableId());
                if(tempMetaTableDO != null){
                    metaTableMap.put(query.getMetaTableId(),tempMetaTableDO);
                }
            }
            resultList = tbPatrolStoreRecordList.stream().map(e -> {
                PictureCenterVO tempVo = PictureCenterVO.builder()
                        .id(e.getId())
                        .taskName(e.getTaskName())
                        .storeId(e.getStoreId())
                        .storeName(e.getStoreName())
                        .pictureCenterColumnList(groupMap.get(e.getId()))
                        .pictureCenterTableVOList(pictureCenterTableMap.get(e.getId()))
                        .completeTime(e.getSignEndTime())
                        .createTime(e.getCreateTime())
                        .patrolType(e.getPatrolType())
                        .build();
                if(metaTableMap.get(e.getMetaTableId()) != null){
                    tempVo.setMetaTableName(metaTableMap.get(e.getMetaTableId()).getTableName());
                    tempVo.setMetaTableId(e.getMetaTableId());
                }
                if(query.getMetaTableId() != null){
                    tempVo.setMetaTableName(metaTableMap.get(query.getMetaTableId()).getTableName());
                    tempVo.setMetaTableId(query.getMetaTableId());
                }
                return tempVo;
            }).collect(Collectors.toList());
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public PageInfo<PictureCenterVO> getDisplayRecordByTaskName(String enterpriseId, PictureCenterQuery query) {
        setQueryRegionPath(enterpriseId,query);
        //获取陈列巡店记录
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<TbDisplayTableRecordDO> tbDisplayTableRecordList = tbDisplayTableRecordMapper.getRecordByTaskName(enterpriseId,query.getRegionPath(),
                query.getStoreIdList(),query.getMetaTableId(),query.getTaskName(),query.getBeginDate(),query.getEndDate(),
                query.getCompleteBeginDate(), query.getCompleteEndDate(), query.getRegionPathList(), query.getStatus());
        if(CollectionUtils.isEmpty(tbDisplayTableRecordList)){
            return new PageInfo<>();
        }
        PageInfo pageInfo = new PageInfo(tbDisplayTableRecordList);


        List<PictureCenterVO> resultList = new ArrayList<>();
        List<Long> recordIdList = tbDisplayTableRecordList.stream().map(TbDisplayTableRecordDO::getId).collect(Collectors.toList());
        List<Long> metaTableIdList = tbDisplayTableRecordList.stream().map(TbDisplayTableRecordDO::getMetaTableId).distinct().collect(Collectors.toList());
        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnList = tbMetaDisplayTableColumnMapper.selectAllColumnListByTableIdList(enterpriseId,metaTableIdList);
        Map<Long, TbMetaDisplayTableColumnDO> displayTableColumnMap = ListUtils.emptyIfNull(tbMetaDisplayTableColumnList).stream()
                .collect(Collectors.toMap(TbMetaDisplayTableColumnDO::getId, data -> data, (a, b) -> a));


        List<TbMetaTableDO> metaTableList = tbMetaTableMapper.selectByMetaTableIdListAll(enterpriseId,null,metaTableIdList);
        Map<Long, TbMetaTableDO> metaTableMap = ListUtils.emptyIfNull(metaTableList).stream()
                .collect(Collectors.toMap(TbMetaTableDO::getId, data -> data, (a, b) -> a));
        //根据业务id集合获取标准检查项数据
        List<TbDisplayTableDataColumnDO> tbDataStaTableColumnList = tbDisplayTableDataColumnMapper.listByRecordIdList(enterpriseId,recordIdList, query.getMetaColumnIdList());

        //根据业务id集合获取标准检查项数据
        List<TbDisplayTableDataContentDO> tbDataStaTableContentnList = displayTableDataContentMapper.listByRecordIdList(enterpriseId,recordIdList, query.getMetaColumnIdList());
        Map<Long, List<PictureCenterColumnVO>> groupContent = new HashMap<>();
        if(CollectionUtils.isNotEmpty(tbDataStaTableContentnList)){
            List<PictureCenterColumnVO> pictureCenterColumnVoList = tbDataStaTableContentnList.stream()
                    .map(e -> transHignDisplayPictureCenterColumn(e,displayTableColumnMap)).collect(Collectors.toList());
            groupContent = pictureCenterColumnVoList.stream().collect(Collectors.groupingBy(PictureCenterColumnVO::getBusinessId));
        }

        if(CollectionUtils.isNotEmpty(tbDataStaTableColumnList) || CollectionUtils.isNotEmpty(tbDataStaTableContentnList)){
            List<PictureCenterColumnVO> pictureCenterColumnVoList = tbDataStaTableColumnList.stream()
                    .map(e -> transDisplayPictureCenterColumn(e, displayTableColumnMap)).collect(Collectors.toList());
            Map<Long, List<PictureCenterColumnVO>> groupMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(tbDataStaTableColumnList)){
                groupMap = pictureCenterColumnVoList.stream().collect(Collectors.groupingBy(PictureCenterColumnVO::getBusinessId));
            }

            Map<Long, List<PictureCenterColumnVO>> finalGroupContent = groupContent;
            Map<Long, List<PictureCenterColumnVO>> finalGroupMap = groupMap;
            resultList = tbDisplayTableRecordList.stream().map(e -> {
                PictureCenterVO tempVo = PictureCenterVO.builder()
                        .id(e.getId())
                        .taskName(e.getTaskName())
                        .storeId(e.getStoreId())
                        .status(e.getStatus())
                        .storeName(e.getStoreName())
                        .pictureCenterColumnList(finalGroupContent.get(e.getId()) == null ? finalGroupMap.get(e.getId()) : finalGroupContent.get(e.getId()))
                        .completeTime(e.getCompleteTime())
                        .createTime(e.getCreateTime())
                        .build();
                if(metaTableMap.get(e.getMetaTableId()) != null){
                    tempVo.setMetaTableName(metaTableMap.get(e.getMetaTableId()).getTableName());
                    tempVo.setMetaTableId(e.getMetaTableId());
                }
                return tempVo;
            }).collect(Collectors.toList());
        }
        pageInfo.setList(resultList);

        return pageInfo;
    }

    @Override
    public PageInfo<PictureCenterVO> getPictureRecordByTaskName(String enterpriseId, PictureCenterQuery query) {
        setQueryRegionPath(enterpriseId,query);
        //获取巡店记录
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList = tbPatrolStoreRecordMapper.getRecordByTaskName(enterpriseId,query.getRegionPath(),
                query.getStoreIdList(),query.getMetaTableId(),query.getTaskName(),query.getBeginDate(),query.getEndDate(),
                query.getCompleteBeginDate(), query.getCompleteEndDate(),
                query.getTaskType(), query.getRegionPathList(), null);
        if(CollectionUtils.isEmpty(tbPatrolStoreRecordList)){
            return new PageInfo<>();
        }
        PageInfo pageInfo = new PageInfo(tbPatrolStoreRecordList);

        List<PictureCenterVO> resultList = new ArrayList<>();
        List<Long> recordIdList = tbPatrolStoreRecordList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());

        List<Long> metaTableIdList = new ArrayList<>();
        Map<Long, List<TbDataTableDO>> dataTableMap = Maps.newHashMap();
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, new ArrayList<>(recordIdList), PATROL_STORE);
        if (CollectionUtils.isNotEmpty(dataTableList)) {
            metaTableIdList = dataTableList.stream().map(TbDataTableDO::getMetaTableId).distinct().collect(Collectors.toList());
            dataTableMap = dataTableList.stream()
                    .collect(Collectors.groupingBy(TbDataTableDO::getBusinessId));
        }

        //根据业务id集合获取图片数据列表
        List<TbPatrolStorePictureDO> tbPatrolStorePictureList = tbPatrolStorePictureMapper.selectByBusinessIdList(enterpriseId,recordIdList);

        if(CollectionUtils.isNotEmpty(tbPatrolStorePictureList)){
            tbPatrolStorePictureList = tbPatrolStorePictureList.stream().filter(e -> !Constants.DEFAULT_PICTURE_URL.equals(e.getPicture())).collect(Collectors.toList());
        }
        List<TbMetaTableDO> metaTableList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(metaTableIdList)){
            metaTableList = tbMetaTableMapper.selectByMetaTableIdListAll(enterpriseId,null, metaTableIdList);
        }
        Map<Long, TbMetaTableDO> metaTableMap = ListUtils.emptyIfNull(metaTableList).stream()
                .collect(Collectors.toMap(TbMetaTableDO::getId, data -> data, (a, b) -> a));

        Map<Long, String> sceneNameMap = new HashMap<>();
        //查询场景名称
        List<StoreSceneDo> sceneDoList = storeSceneMapper.getStoreSceneList(enterpriseId);
        if(CollectionUtils.isNotEmpty(sceneDoList)){
            sceneNameMap = sceneDoList.stream().collect(Collectors.toMap(StoreSceneDo::getId, StoreSceneDo::getName, (a, b) -> a));
        }

        if(CollectionUtils.isNotEmpty(tbPatrolStorePictureList)){
            Map<Long, String> finalSceneNameMap = sceneNameMap;
            List<PictureCenterColumnVO> pictureCenterColumnVoList = tbPatrolStorePictureList.stream()
                    .map(e -> transStorePictureCenterColumn(e, finalSceneNameMap)).collect(Collectors.toList());
            Map<Long, List<PictureCenterColumnVO>> groupMap = pictureCenterColumnVoList.stream()
                    .collect(Collectors.groupingBy(PictureCenterColumnVO::getBusinessId));

            Map<Long, List<TbDataTableDO>> finalDataTableMap = dataTableMap;
            resultList = tbPatrolStoreRecordList.stream().flatMap(e -> finalDataTableMap.get(e.getId()).stream()
                            .map(dataTableDO -> PictureCenterVO.builder().id(e.getId()).taskName(e.getTaskName())
                                    .storeId(e.getStoreId()).storeName(e.getStoreName()).pictureCenterColumnList(groupMap.get(e.getId()))
                                    .metaTableId(dataTableDO.getMetaTableId())
                                    .metaTableName(metaTableMap.get(dataTableDO.getMetaTableId()).getTableName())
                                    .build())).collect(Collectors.toList());

            /*resultList = tbPatrolStoreRecordList.stream().map(e -> {
                PictureCenterVO tempVo = PictureCenterVO.builder()
                        .id(e.getId())
                        .taskName(e.getTaskName())
                        .storeId(e.getStoreId())
                        .storeName(e.getStoreName())
                        .pictureCenterColumnList(groupMap.get(e.getId()))
                        .build();
                if(metaTableMap.get(e.getMetaTableId()) != null){
                    tempVo.setMetaTableName(metaTableMap.get(e.getMetaTableId()).getTableName());
                    tempVo.setMetaTableId(e.getMetaTableId());
                }
                return tempVo;
            }).collect(Collectors.toList());*/
        }
        if(TaskTypeEnum.PATROL_STORE_AI.getCode().equals(query.getTaskType())){
            List<Long> pictureIdList = tbPatrolStorePictureList.stream().map(TbPatrolStorePictureDO::getId).collect(Collectors.toList());
            List<AiPictureResultMappingDO> aiPictureResultMappingDOS = aiPictureResultMappingDao.selectByPictureIdList(enterpriseId, pictureIdList);

            List<Long> columnIdList = aiPictureResultMappingDOS.stream().map(AiPictureResultMappingDO::getMetaColumnId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(columnIdList)){
                List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, columnIdList);
                Map<Long, TbMetaStaTableColumnDO> columnDOMap = ListUtils.emptyIfNull(tbMetaStaTableColumnDOS).stream()
                        .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, data -> data, (a, b) -> a));

                List<PictureCenterColumnAIResultVO> pictureCenterColumnAIResultVOS = aiPictureResultMappingDOS.stream()
                        .map(e -> {
                            PictureCenterColumnAIResultVO pictureCenterColumnAIResultVO = new PictureCenterColumnAIResultVO();
                            pictureCenterColumnAIResultVO.setAiResult(CheckResultEnum.getByCode(e.getAiResult()));
                            pictureCenterColumnAIResultVO.setPictureId(e.getPictureId());
                            pictureCenterColumnAIResultVO.setAiType(PatrolAITypeEnum.getDescByCode(columnDOMap.get(e.getMetaColumnId()).getAiType()));
                            return pictureCenterColumnAIResultVO;
                        }).collect(Collectors.toList());

                Map<Long, List<PictureCenterColumnAIResultVO>> pictureGroupMap = pictureCenterColumnAIResultVOS.stream()
                        .collect(Collectors.groupingBy(PictureCenterColumnAIResultVO::getPictureId));
                for (PictureCenterVO pictureCenterVO : resultList) {
                    List<PictureCenterColumnVO> pictureCenterColumnList = ListUtils.emptyIfNull(pictureCenterVO.getPictureCenterColumnList());
                    for (PictureCenterColumnVO pictureCenterColumnVO : pictureCenterColumnList) {
                        pictureCenterColumnVO.setAiResultVOS(pictureGroupMap.get(pictureCenterColumnVO.getId()));
                    }
                    pictureCenterColumnList = pictureCenterColumnList.stream().filter(data -> data.getAiResultVOS() != null).collect(Collectors.toList());
                    pictureCenterVO.setPictureCenterColumnList(pictureCenterColumnList);
                }
            }
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public List<PictureCenterStoreDO> getStorePicture(String enterpriseId, PictureCenterQuery query) {
        setQueryRegionPath(enterpriseId,query);
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<PictureCenterStoreDO> pictureCenterStoreList = storeMapper.getpictureCenterStore(enterpriseId, query.getRegionPath(), query.getStoreIdList());

        return pictureCenterStoreList;
    }

    @Override
    public PageInfo<PictureQuestionCenterVO> taskQuestionRecord(String enterpriseId, PictureCenterQuery query) {
        setQueryRegionPath(enterpriseId,query);
        //工单记录
        PageHelper.startPage(query.getPageNum(),query.getPageSize());
        List<TbQuestionRecordDO> recordList = questionRecordDao.taskQuestionRecord(enterpriseId,query.getRegionPath(),
                query.getMetaColumnIdList(), query.getStoreIdList(), query.getMetaTableId(),query.getTaskName(),query.getBeginDate(),query.getEndDate(),
                query.getCompleteBeginDate(), query.getCompleteEndDate(), query.getQuestionType(), query.getRegionPathList(), query.getCreateUserIdList());
        if(CollectionUtils.isEmpty(recordList)){
            return new PageInfo<>();
        }

        PageInfo pageInfo = new PageInfo(recordList);
        //工单记录id
        List<Long> recordIdList = recordList.stream().map(TbQuestionRecordDO::getId).collect(Collectors.toList());
        //任务id
        Set<Long> taskIdSet = recordList.stream().map(TbQuestionRecordDO::getUnifyTaskId).collect(Collectors.toSet());

        //检查表id
        Set<Long> metaTableIdList = recordList.stream().map(TbQuestionRecordDO::getMetaTableId).collect(Collectors.toSet());
        List<TbMetaTableDO> metaTableList = tbMetaTableMapper.selectByMetaTableIdListAll(enterpriseId,null, new ArrayList<>(metaTableIdList));
        Map<Long, String> metaTableMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(metaTableList)){
            metaTableMap = ListUtils.emptyIfNull(metaTableList).stream()
                    .collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName, (a, b) -> a));
        }

        //检查项id
        Set<Long> metaColumnIdList = recordList.stream().map(TbQuestionRecordDO::getMetaColumnId).collect(Collectors.toSet());

        List<TbMetaStaTableColumnDO> metaStaColumnList =
                metaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaColumnIdList));
        Map<Long, String> idMetaStaColumnMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(metaStaColumnList)){
            idMetaStaColumnMap = metaStaColumnList.stream()
                    .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId,TbMetaStaTableColumnDO::getColumnName, (a, b) -> a));
        }
        //任务列表
        List<TaskParentDO> taskParentDOList = taskParentMapper.selectParentTaskBatch(enterpriseId, new ArrayList<>(taskIdSet));
        List<TbQuestionRecordExpandDO> tbQuestionRecordExpandDOS = tbQuestionRecordExpandMapper.selectByQuestionRecordIds(enterpriseId, recordIdList);
        if(CollectionUtils.isEmpty(taskParentDOList)){
            return new PageInfo<>();
        }

        Map<Long, String> taskInfoMap =  tbQuestionRecordExpandDOS.stream().collect(Collectors.toMap(TbQuestionRecordExpandDO::getRecordId, TbQuestionRecordExpandDO::getTaskInfo, (a, b) -> a));
        //用户
        Set<String> userIdSet = taskParentDOList.stream().map(TaskParentDO::getCreateUserId).collect(Collectors.toSet());

        Map<String, String> userIdNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(userIdSet));
        List<PictureCenterQuestionColumnVO> columnList = new ArrayList<>();
        Map<String, String> finalUserIdNameMap = userIdNameMap;
        if (query.getNodeNo()==null||query.getNodeNo().equals(Integer.valueOf(UnifyNodeEnum.ZERO_NODE.getCode()))) {
            recordList.forEach(e -> {
                PictureCenterQuestionColumnVO pictureCenterColumn = new PictureCenterQuestionColumnVO();
                pictureCenterColumn.setBusinessId(e.getId());
                String taskInfo = taskInfoMap.get(e.getId());
                JSONObject videoUrl = new JSONObject();
                if (StringUtils.isNotBlank(taskInfo)) {
                    JSONObject taskInfoObj = JSONObject.parseObject(taskInfo);
                    String videos = taskInfoObj.getString(UnifyTaskConstant.TaskInfo.VIDEOS);
                    if (StringUtils.isNotEmpty(videos)){
                        videoUrl =  JSONObject.parseObject(taskInfoObj.getString(UnifyTaskConstant.TaskInfo.VIDEOS));
                    }
                    videoUrl.put(UnifyTaskConstant.TaskInfo.SOUND_RECORDING_LIST, taskInfoObj.getJSONArray(UnifyTaskConstant.TaskInfo.SOUND_RECORDING_LIST));
                    pictureCenterColumn.setBusinessId(e.getId());
                    pictureCenterColumn.setPictureUrl(taskInfoObj.getString(UnifyTaskConstant.TaskInfo.PHOTOS));
                    pictureCenterColumn.setVideoUrl(videoUrl.toJSONString());
                    pictureCenterColumn.setNodeNo(UnifyNodeEnum.ZERO_NODE.getCode());
                    pictureCenterColumn.setId(e.getId());
                    pictureCenterColumn.setUserName(finalUserIdNameMap.get(e.getCreateUserId()));
                    columnList.add(pictureCenterColumn);
                }
            });
        }
        //NodeNO 为null的时候查询全部  如果是不为null 但是NodeNo为0的时候不需要审批数据
        if (query.getNodeNo()==null||!query.getNodeNo().equals(Integer.valueOf(UnifyNodeEnum.ZERO_NODE.getCode()))){
            List<TbQuestionHistoryVO> questionHistoryList = questionHistoryDao.selectHistoryListByRecordIdList(enterpriseId, recordIdList,query.getNodeNo());
            questionHistoryList.forEach(e -> {
                PictureCenterQuestionColumnVO pictureCenterColumn = new PictureCenterQuestionColumnVO();
                pictureCenterColumn.setBusinessId(e.getRecordId());
                pictureCenterColumn.setPictureUrl(e.getPhoto());
                pictureCenterColumn.setVideoUrl(e.getVideo());
                pictureCenterColumn.setNodeNo(e.getNodeNo());
                pictureCenterColumn.setUserName(e.getOperateUserName());
                pictureCenterColumn.setId(e.getId());
                columnList.add(pictureCenterColumn);
            });
        }

        Map<Long, List<PictureCenterQuestionColumnVO>> mapColumn = ListUtils.emptyIfNull(columnList)
                .stream()
                .collect(Collectors.groupingBy(PictureCenterQuestionColumnVO::getBusinessId));

        List<PictureQuestionCenterVO> centerList = new ArrayList<>();

        Map<Long, String> finalMetaTableMap = metaTableMap;
        Map<Long, String> finalIdMetaStaColumnMap = idMetaStaColumnMap;
        recordList.forEach(e -> {
            PictureQuestionCenterVO pictureQuestionCenterVO = new PictureQuestionCenterVO();
            pictureQuestionCenterVO.setId(e.getId());
            pictureQuestionCenterVO.setStoreId(e.getStoreId());
            pictureQuestionCenterVO.setStoreName(e.getStoreName());
            pictureQuestionCenterVO.setTaskName(e.getTaskName());
            pictureQuestionCenterVO.setMetaTableId(e.getMetaTableId());
            pictureQuestionCenterVO.setParentQuestionId(e.getParentQuestionId());
            pictureQuestionCenterVO.setParentQuestionName(e.getParentQuestionName());
            pictureQuestionCenterVO.setQuestionType(e.getQuestionType());
            pictureQuestionCenterVO.setMetaTableName(finalMetaTableMap.get(e.getMetaTableId()));
            pictureQuestionCenterVO.setMetaColumnId(e.getMetaColumnId());
            if(e.getMetaTableId() != null){
                pictureQuestionCenterVO.setMetaColumnName(finalIdMetaStaColumnMap.get(e.getMetaColumnId()));
            }
            pictureQuestionCenterVO.setPictureCenterColumnList(mapColumn.get(e.getId()));
            centerList.add(pictureQuestionCenterVO);
        });
        pageInfo.setList(centerList);
        return pageInfo;
    }

    @Override
    public PageInfo<PictureCenterVO> getCheckRecordByTaskName(String enterpriseId, PatrolStoreCheckQuery query, String userId) {

        //检查输入是否为空
        if(CollectionUtils.isEmpty(query.getRegionIdList())  && CollectionUtils.isEmpty(query.getStoreIdList())){
            SelectComptRegionStoreVO regionAndStore = selectionComponentService.getRegionAndStore(enterpriseId, null, userId, null);
            //如果管辖为空返回空数组
            if (CollectionUtils.isEmpty(regionAndStore.getAllRegionList())){
                return new PageInfo(Lists.newArrayList());
            }
            query.setRegionIdList(regionAndStore.getAllRegionList().stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }

        if(query.getRegionId() != null){
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            query.setRegionPath(regionPath);
        }

        if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                query.setRegionPathList(regionPathList);
            }
        }

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PatrolStoreCheckVO> vos = patrolStoreCheckMapper.getPatrolStoreCheckList(enterpriseId,query);
        if(CollectionUtils.isEmpty(vos)){
            return new PageInfo(new ArrayList<>());
        }

        PageInfo pageInfo = new PageInfo(vos);

        List<PictureCenterVO> resultList = new ArrayList<>();
        List<Long> recordIdList = vos.stream().map(PatrolStoreCheckVO::getBusinessId).collect(Collectors.toList());
        List<Long> metaTableIdList = new ArrayList<>();
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, new ArrayList<>(recordIdList), BusinessCheckType.PATROL_RECHECK.getCode());
//        if(query.getMetaTableId() != null){
//            dataTableList = dataTableList.stream().filter(tbDataTableDO -> tbDataTableDO.getMetaTableId().equals(query.getMetaTableId())).collect(Collectors.toList());
//        }
        if (CollectionUtils.isNotEmpty(dataTableList)) {
            metaTableIdList = dataTableList.stream().map(TbDataTableDO::getMetaTableId).distinct().collect(Collectors.toList());
        }

        //根据业务id集合获取标准检查项数据
        List<TbDataStaTableColumnDO> tbDataStaTableColumnList = tbDataStaTableColumnMapper.selectByBusinessIdList(enterpriseId, recordIdList, null);
        List<TbMetaTableDO> metaTableList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(metaTableIdList)) {
            metaTableList = tbMetaTableMapper.selectByMetaTableIdListAll(enterpriseId, null, metaTableIdList);
        }

        List<Long> dataColumnIdList = tbDataStaTableColumnList.stream().map(TbDataStaTableColumnDO::getId).distinct().collect(Collectors.toList());

        List<Long> metaStaColumnIdList = tbDataStaTableColumnList.stream().map(TbDataStaTableColumnDO::getMetaColumnId).distinct().collect(Collectors.toList());

        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList =
                tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, new ArrayList<>(metaStaColumnIdList));
        List<TbMetaColumnResultDTO> columnResultDTOList = metaTableService.getMetaColumnResultList(enterpriseId, columnResultDOList);
        Map<Long, List<TbMetaColumnResultDTO>> columnIdResultDOsMap =
                columnResultDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDTO::getMetaColumnId));


        List<TbMetaStaTableColumnDO> metaStaColumnList =
                tbMetaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaStaColumnIdList));

        List<MetaStaColumnVO> metaStaColumnVOList = metaStaColumnList.stream().map(a -> {
            MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
            BeanUtils.copyProperties(a, metaStaColumnVO);
            //如果是采集项
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())){
                metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
            }
            metaStaColumnVO
                    .setColumnResultList(columnIdResultDOsMap.getOrDefault(a.getId(), new ArrayList<>()));
            // 填充结果项
            metaStaColumnVO.fillColumnResultList();
            return metaStaColumnVO;
        }).collect(Collectors.toList());
        Map<Long, List<MetaStaColumnVO>> metaTableIdStaColumnsMap = new HashMap<>(metaStaColumnVOList.stream().collect(Collectors.groupingBy(MetaStaColumnVO::getMetaTableId)));


        List<CheckDataStaColumnDO> checkDataStaColumnList = new ArrayList<>();
        if(query.getCheckType() != null && query.getCheckType() == 2 && query.getWarZoneCheckStatus()!= null && query.getWarZoneCheckStatus() == 1){
            checkDataStaColumnList = checkDataStaColumnMapper.warCheckDataStaColumnDOList(enterpriseId, dataColumnIdList);
        }
        if(query.getCheckType() != null && query.getCheckType() == 1 && query.getBigRegionCheckStatus()!= null && query.getBigRegionCheckStatus() == 1){
            checkDataStaColumnList = checkDataStaColumnMapper.checkDataStaColumnDOList(enterpriseId, dataColumnIdList);
        }

        Map<Long, CheckDataStaColumnDO> checkDataStaColumnMap = checkDataStaColumnList.stream().collect(Collectors.toMap(CheckDataStaColumnDO::getDataStaColumnId, data -> data, (a, b) -> a));


        List<String> userIdList = tbDataStaTableColumnList.stream().filter(x -> StringUtils.isNotEmpty(x.getSupervisorId())).map(TbDataStaTableColumnDO::getSupervisorId).collect(Collectors.toList());
        List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, userIdList);
        Map<String, String> userMap = enterpriseUserSingleDTOS.stream().collect(Collectors.toMap(EnterpriseUserSingleDTO::getUserId, EnterpriseUserSingleDTO::getUserName));
        Map<Long, TbMetaTableDO> metaTableMap = ListUtils.emptyIfNull(metaTableList).stream()
                .collect(Collectors.toMap(TbMetaTableDO::getId, data -> data, (a, b) -> a));
        if(CollectionUtils.isNotEmpty(tbDataStaTableColumnList)){
            List<PictureCenterColumnVO> pictureCenterColumnVoList = tbDataStaTableColumnList.stream()
                    .map(e -> transCheckPictureCenterColumn(e,userMap, checkDataStaColumnMap)).collect(Collectors.toList());
//            if(query.getMetaTableId() != null){
//                pictureCenterColumnVoList = tbDataStaTableColumnList.stream().filter(o->query.getMetaTableId().equals(o.getMetaTableId()))
//                        .map(e -> transCheckPictureCenterColumn(e,userMap, checkDataStaColumnMap)).collect(Collectors.toList());
//            }
            Map<Long, List<PictureCenterColumnVO>> groupMap = pictureCenterColumnVoList.stream()
                    .collect(Collectors.groupingBy(PictureCenterColumnVO::getBusinessId));

            Map<Long, List<PictureCenterColumnVO>> groupDataTableMap = pictureCenterColumnVoList.stream()
                    .collect(Collectors.groupingBy(PictureCenterColumnVO::getDataTableId));
            //标准表
            //构建多表返回参数对象
            Map<Long, List<PictureCenterTableVO>>  pictureCenterTableMap= dataTableList.stream().map(dataTableDO -> PictureCenterTableVO.builder().metaTableId(dataTableDO.getMetaTableId()).businessId(dataTableDO.getBusinessId())
                            .metaTableName(dataTableDO.getTableName()).
                            pictureCenterColumnList(groupDataTableMap.get(dataTableDO.getId()))
                            .metaStaColumns(metaTableIdStaColumnsMap.get(dataTableDO.getMetaTableId())).build()).collect(Collectors.toList()).stream()
                    .collect(Collectors.groupingBy(PictureCenterTableVO::getBusinessId));
            if(query.getMetaTableId() != null){
                TbMetaTableDO tempMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, query.getMetaTableId());
                if(tempMetaTableDO != null){
                    metaTableMap.put(query.getMetaTableId(),tempMetaTableDO);
                }
            }
            resultList = vos.stream().map(e -> {
                PictureCenterVO tempVo = PictureCenterVO.builder()
                        .id(e.getBusinessId())
                        .taskName(e.getTaskName())
                        .storeId(e.getStoreId())
                        .storeName(e.getStoreName())
                        .pictureCenterColumnList(groupMap.get(e.getBusinessId()))
                        .pictureCenterTableVOList(pictureCenterTableMap.get(e.getBusinessId()))
                        .completeTime(e.getSignEndTime())
                        .signStartTime(e.getSignStartTime())
                        .supervisorId(e.getSupervisorId())
                        .supervisorName(e.getSupervisorName())
                        .supervisorJobNum(e.getSupervisorJobNum())
                        .createTime(e.getCreateTime())
                        .patrolType(e.getPatrolType())
                        .bigRegionUserId(e.getBigRegionUserId())
                        .bigRegionCheckTime(e.getBigRegionCheckTime())
                        .bigRegionUserJobNum(e.getBigRegionUserJobNum())
                        .bigRegionUserName(e.getBigRegionUserName())
                        .warZoneCheckTime(e.getWarZoneCheckTime())
                        .warZoneUserId(e.getWarZoneUserId())
                        .warZoneUserJobNum(e.getWarZoneUserJobNum())
                        .warZoneUserName(e.getWarZoneUserName())
                        .build();
                if(query.getMetaTableId() != null){
                    tempVo.setMetaTableName(metaTableMap.get(query.getMetaTableId()).getTableName());
                    tempVo.setMetaTableId(query.getMetaTableId());
                }
                return tempVo;
            }).collect(Collectors.toList());
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }


    public void setQueryRegionPath(String enterpriseId, PictureCenterQuery query){
        if(StringUtils.isNotBlank(query.getRegionId())){
            List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, Collections.singletonList(query.getRegionId()));
            if(CollectionUtils.isNotEmpty(regionPathList)){
                //只能选择一个区域
                query.setRegionPath(regionPathList.get(0).getRegionPath());
            }else {
                query.setRegionPath(Constants.STORE_PATH_SPILT + query.getRegionId() + Constants.STORE_PATH_SPILT);
            }
        }else if(query.getCurrentUser() != null){
            query.setRegionPathList(getUserRegionPathList(enterpriseId, query.getRegionId(), query.getCurrentUser(), query.getStoreIdList()));
        }
    }
    public PictureCenterColumnVO transStorePictureCenterColumn(TbPatrolStorePictureDO tbPatrolStorePictureDO, Map<Long, String> storeSceneName){
        return PictureCenterColumnVO.builder()
                .id(tbPatrolStorePictureDO.getId())
                .pictureUrl(tbPatrolStorePictureDO.getPicture())
                .businessId(tbPatrolStorePictureDO.getBusinessId())
                .storeSceneName(storeSceneName.get(tbPatrolStorePictureDO.getStoreSceneId()))
                .build();
    }

    public PictureCenterColumnVO transPictureCenterColumn(TbDataStaTableColumnDO tbDataStaTableColumn,Map<String, String> userMap){
        return PictureCenterColumnVO.builder()
                .id(tbDataStaTableColumn.getId())
                .metaColumnId(tbDataStaTableColumn.getMetaColumnId())
                .pictureUrl(tbDataStaTableColumn.getCheckPics())
                .videoUrl(tbDataStaTableColumn.getCheckVideo())
                .businessId(tbDataStaTableColumn.getBusinessId())
                .dataTableId(tbDataStaTableColumn.getDataTableId())
                .metaColumnName(tbDataStaTableColumn.getMetaColumnName())
                .checkText(tbDataStaTableColumn.getCheckText())
                .checkResult(tbDataStaTableColumn.getCheckResult())
                .checkResultName(tbDataStaTableColumn.getCheckResultName())
                .checkResultId(tbDataStaTableColumn.getCheckResultId())
                .checkScore(tbDataStaTableColumn.getCheckScore())
                .supervisionName(userMap.get(tbDataStaTableColumn.getSupervisorId()))
                .build();
    }

    public PictureCenterColumnVO transCheckPictureCenterColumn(TbDataStaTableColumnDO tbDataStaTableColumn,Map<String, String> userMap, Map<Long, CheckDataStaColumnDO> checkDataStaColumnMap){
        PictureCenterColumnVO pictureCenterColumnVO = PictureCenterColumnVO.builder()
                .id(tbDataStaTableColumn.getId())
                .metaColumnId(tbDataStaTableColumn.getMetaColumnId())
                .pictureUrl(tbDataStaTableColumn.getCheckPics())
                .videoUrl(tbDataStaTableColumn.getCheckVideo())
                .businessId(tbDataStaTableColumn.getBusinessId())
                .dataTableId(tbDataStaTableColumn.getDataTableId())
                .metaColumnName(tbDataStaTableColumn.getMetaColumnName())
                .checkText(tbDataStaTableColumn.getCheckText())
                .checkResult(tbDataStaTableColumn.getCheckResult())
                .checkResultName(tbDataStaTableColumn.getCheckResultName())
                .checkResultId(tbDataStaTableColumn.getCheckResultId())
                .checkScore(tbDataStaTableColumn.getCheckScore())
                .supervisionName(userMap.get(tbDataStaTableColumn.getSupervisorId()))
                .checkResultReason(tbDataStaTableColumn.getCheckResultReason())
                .columnMaxScore(tbDataStaTableColumn.getColumnMaxScore())
                .build();
        CheckDataStaColumnDO checkDataStaColumnDO = checkDataStaColumnMap.get(tbDataStaTableColumn.getId());
        if(checkDataStaColumnDO != null){
            pictureCenterColumnVO.setCheckText(checkDataStaColumnDO.getCheckText());
            pictureCenterColumnVO.setCheckResult(checkDataStaColumnDO.getCheckResult());
            pictureCenterColumnVO.setCheckResultName(checkDataStaColumnDO.getCheckResultName());
            pictureCenterColumnVO.setCheckResultId(checkDataStaColumnDO.getCheckResultId());
            pictureCenterColumnVO.setCheckScore(checkDataStaColumnDO.getCheckScore());
            pictureCenterColumnVO.setPictureUrl(checkDataStaColumnDO.getCheckPics());
            pictureCenterColumnVO.setVideoUrl(checkDataStaColumnDO.getCheckVideo());
        }
        return pictureCenterColumnVO;
    }
    public PictureCenterColumnVO transDisplayPictureCenterColumn(TbDisplayTableDataColumnDO tbDisplayTableDataColumn,Map<Long, TbMetaDisplayTableColumnDO> displayTableColumnMap){
        String picture = tbDisplayTableDataColumn.getPhotoArray();
        List<String> urlList = new ArrayList<>();
        if(StringUtils.isNotEmpty(picture)){
            JSONArray jsonArray = JSONArray.parseArray(picture);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                urlList.add(jsonObject.getString(Constants.PHOTO_HANDLE_URL));
            }
        }
        String columnName = "";
        if(displayTableColumnMap.get(tbDisplayTableDataColumn.getMetaColumnId()) != null){
            columnName = displayTableColumnMap.get(tbDisplayTableDataColumn.getMetaColumnId()).getColumnName();
        }
        return PictureCenterColumnVO.builder()
                .id(tbDisplayTableDataColumn.getId())
                .metaColumnId(tbDisplayTableDataColumn.getMetaColumnId())
                .displayPictureUrls(urlList)
                .businessId(tbDisplayTableDataColumn.getRecordId())
                .metaColumnName(columnName)
                .videoUrl(tbDisplayTableDataColumn.getCheckVideo())
                .build();
    }

    public PictureCenterColumnVO transHignDisplayPictureCenterColumn(TbDisplayTableDataContentDO displayTableDataContentDO,Map<Long, TbMetaDisplayTableColumnDO> displayTableColumnMap){
        String picture = displayTableDataContentDO.getPhotoArray();
        List<String> urlList = new ArrayList<>();
        if(StringUtils.isNotEmpty(picture)){
            JSONArray jsonArray = JSONArray.parseArray(picture);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                urlList.add(jsonObject.getString(Constants.PHOTO_HANDLE_URL));
            }
        }
        String columnName = "";
        if(displayTableColumnMap.get(displayTableDataContentDO.getMetaContentId()) != null){
            columnName = displayTableColumnMap.get(displayTableDataContentDO.getMetaContentId()).getColumnName();
        }
        return PictureCenterColumnVO.builder()
                .id(displayTableDataContentDO.getId())
                .metaColumnId(displayTableDataContentDO.getMetaContentId())
                .displayPictureUrls(urlList)
                .businessId(displayTableDataContentDO.getRecordId())
                .metaColumnName(columnName)
                .videoUrl(displayTableDataContentDO.getCheckVideo())
                .build();
    }

    /**
     * 获取用户权限区域路径列表
     * @param enterpriseId
     * @param regionId
     * @param currentUser
     * @param storeIdList
     * @return
     */
    private List<String> getUserRegionPathList(String enterpriseId, String regionId, CurrentUser currentUser, List<String> storeIdList) {
        List<String> regionPathList = new ArrayList<>();
        SysRoleDO currUserRole = currentUser.getSysRoleDO();
        if (currUserRole != null && !AuthRoleEnum.ALL.getCode().equals(currUserRole.getRoleAuth()) && CollectionUtils.isEmpty(storeIdList)
                && StringUtils.isBlank(regionId)) {
            List<UserAuthMappingDO> region = new ArrayList<>();
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, currentUser.getUserId());
            ListUtils.emptyIfNull(userAuthMappingList)
                    .forEach(data -> {
                        if (data.getType().equals(UserAuthMappingTypeEnum.REGION.getCode())) {
                            region.add(data);
                        }
                    });
            List<String> regionIdList = ListUtils.emptyIfNull(region).stream()
                    .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(regionIdList)) {
                List<RegionPathDTO> regionPathByList = regionService.getRegionPathByList(enterpriseId, regionIdList);
                regionPathList = ListUtils.emptyIfNull(regionPathByList)
                        .stream()
                        .map(RegionPathDTO::getRegionPath)
                        .collect(Collectors.toList());
            }
            //如果查询用户权限为空，则不能查询数据
            if(CollectionUtils.isEmpty(regionPathList)){
                regionPathList.add(Constants.ROOT_DELETE_REGION_PATH);
            }
        }
        //如果只有一条记录，且为根节点，则返回空查询全部
        if (CollectionUtils.isNotEmpty(regionPathList) && regionPathList.size() == 1 && Constants.ROOT_REGION_PATH.equals(regionPathList.get(0))) {
            return new ArrayList<>();
        }
        return regionPathList;
    }
}
