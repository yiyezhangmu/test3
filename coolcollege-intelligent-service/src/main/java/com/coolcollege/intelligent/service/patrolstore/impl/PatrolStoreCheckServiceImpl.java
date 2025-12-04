package com.coolcollege.intelligent.service.patrolstore.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.*;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.*;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSubmitParam;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSubmitTableParam;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableResultDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreCheckService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.*;

/**
 * @author byd
 * @date 2024-09-03 16:10
 */
@Slf4j
@Service
public class PatrolStoreCheckServiceImpl implements PatrolStoreCheckService {

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private TaskMappingMapper taskMappingMapper;

    @Resource
    private TbDataTableMapper dataTableMapper;

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Resource
    private TbPatrolStoreRecordInfoMapper tbPatrolStoreRecordInfoMapper;

    @Resource
    private TbPatrolStoreCheckMapper patrolStoreCheckMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private TbPatrolCheckDataTableMapper patrolCheckDataTableMapper;

    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;

    @Resource
    private PatrolStoreService patrolStoreService;

    @Resource
    private TbCheckDataStaColumnMapper checkDataStaColumnMapper;

    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;

    @Resource
    private UserPersonInfoService userPersonInfoService;

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private TbCheckDataStaColumnMapper tbCheckDataStaColumnMapper;



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void patrolCheck(String enterpriseId, Long businessId, String supervisorId) {
        TbPatrolStoreRecordDO storeRecordD = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        //模板权限
        List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, storeRecordD.getTaskId());

        List<String> reCheckTableList = formDataList.stream().filter(formData -> formData.getCheckTable() != null && formData.getCheckTable())
                .map(UnifyFormDataDTO::getOriginMappingId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(reCheckTableList)) {
            log.info("任务巡店检查表不需要复审,businessId:{}", businessId);
            return;
        }

        //进行中的复审任务
        TbPatrolStoreRecordDO currentRecord = tbPatrolStoreRecordMapper.selectByRecheckBusinessId(enterpriseId, businessId, supervisorId);
        if (currentRecord != null) {
            log.info("当前用户正在复审任务,businessId:{}", businessId);
            return;
        }
        String createDate = DateUtils.getTime(new Date());
        List<TbDataTableDO> dataTableList = dataTableMapper.getListByBusinessIdList(enterpriseId, Collections.singletonList(businessId), PATROL_STORE);

        dataTableList = dataTableList.stream().filter(dataTable -> reCheckTableList.contains(String.valueOf(dataTable.getMetaTableId()))
                && !TableTypeUtil.isUserDefinedTable(dataTable.getTableProperty(), dataTable.getTableType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataTableList)) {
            log.info("任务巡店检查表需要稽核表不存在,businessId:{}", businessId);
            return;
        }
        storeRecordD.setId(null);
        storeRecordD.setTaskId(0L);
        storeRecordD.setSubTaskId(0L);
        storeRecordD.setTaskName(storeRecordD.getTaskName());
        storeRecordD.setStatus(0);
        storeRecordD.setCreateTime(new Date());
        storeRecordD.setCreateUserId(supervisorId);
        storeRecordD.setRecheckBusinessId(businessId);
        storeRecordD.setBusinessCheckType(BusinessCheckType.PATROL_RECHECK.getCode());
        storeRecordD.setSubmitStatus(Constants.INDEX_SIX & storeRecordD.getSubmitStatus());
        storeRecordD.setCreateDate(createDate);
        storeRecordD.setMetaTableIds(Constants.COMMA + (reCheckTableList.stream().map(String::valueOf).collect(Collectors.joining(","))) + Constants.COMMA);
        storeRecordD.setOpenSubmitFirst(true);
        tbPatrolStoreRecordMapper.insertSelective(storeRecordD, enterpriseId);
        dataTableList.forEach(e -> {
            e.setBusinessId(storeRecordD.getId());
            e.setSubmitStatus(0);
            e.setTaskId(0L);
            e.setSubTaskId(0L);
            e.setBusinessType(BusinessCheckType.PATROL_RECHECK.getCode());
            e.setCreateDate(createDate);
            e.setCreateTime(new Date());
            e.setCreateUserId(supervisorId);
            dataTableMapper.insertSelective(e, enterpriseId);
        });

        List<TbDataStaTableColumnDO> dataStaTableColumnDOList = tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId, businessId, PATROL_STORE);

        dataStaTableColumnDOList = dataStaTableColumnDOList.stream().filter(e -> reCheckTableList.contains(String.valueOf(e.getMetaTableId()))).collect(Collectors.toList());

        Map<Long, Long> dataTableIdMap = ListUtils.emptyIfNull(dataTableList).stream().collect(Collectors.toMap(TbDataTableDO::getMetaTableId, TbDataTableDO::getId, (a, b) -> a));
        dataStaTableColumnDOList.forEach(dataStaTableColumnDO -> {
            dataStaTableColumnDO.setBusinessType(BusinessCheckType.PATROL_RECHECK.getCode());
            dataStaTableColumnDO.setBusinessId(storeRecordD.getId());
            dataStaTableColumnDO.setBusinessStatus(0);
            dataStaTableColumnDO.setDataTableId(dataTableIdMap.get(dataStaTableColumnDO.getMetaTableId()));
            dataStaTableColumnDO.setTaskId(0L);
            dataStaTableColumnDO.setSubTaskId(0L);
            dataStaTableColumnDO.setCreateTime(new Date());
            dataStaTableColumnDO.setCreateDate(createDate);
            dataStaTableColumnDO.setId(null);
            dataStaTableColumnDO.setTaskQuestionId(0L);
            dataStaTableColumnDO.setSubmitStatus(0);
            dataStaTableColumnDO.setCreateUserId(supervisorId);
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(dataStaTableColumnDO.getColumnType())) {
                dataStaTableColumnDO.setColumnType(MetaColumnTypeEnum.STANDARD_COLUMN.getCode());
            }
        });
        if (CollectionUtils.isNotEmpty(dataStaTableColumnDOList)) {
            tbDataStaTableColumnMapper.batchInsert(enterpriseId, dataStaTableColumnDOList);
        }

        TbPatrolStoreRecordInfoDO recordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, businessId);
        if (recordInfoDO != null) {
            recordInfoDO.setId(storeRecordD.getId());
            tbPatrolStoreRecordInfoMapper.saveTbPatrolStoreRecordInfo(enterpriseId, recordInfoDO);
        }
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeRecordD.getStoreId());

        EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(enterpriseId, supervisorId);
        String jobnumber = Optional.ofNullable(userDO).map(EnterpriseUserDO::getJobnumber).orElse("");
        PatrolStoreCheckDO patrolStoreCheckDO = PatrolStoreCheckDO.builder()
                .taskId(storeRecordD.getTaskId())
                .businessId(storeRecordD.getId())
                .storeId(storeRecordD.getStoreId())
                .storeName(storeRecordD.getStoreName())
                .storeNum(storeDO.getStoreNum())
                .regionId(storeRecordD.getRegionId())
                .regionWay(storeRecordD.getRegionWay())
                .supervisorId(storeRecordD.getSupervisorId())
                .supervisorName(storeRecordD.getSupervisorName())
                .supervisorJobNum(jobnumber)
                .signEndTime(storeRecordD.getSignEndTime())
                .signStartTime(storeRecordD.getSignStartTime())
                .patrolType(storeRecordD.getPatrolType())
                .metaTableIds(storeRecordD.getMetaTableIds())
                .subBeginTime(storeRecordD.getSubBeginTime())
                .subEndTime(storeRecordD.getSubEndTime())
                .recheckBusinessId(storeRecordD.getRecheckBusinessId())
                .taskName(storeRecordD.getTaskName())
                .bigRegionCheckStatus(0)
                .warZoneCheckStatus(0)
                .businessId(storeRecordD.getId())
                .createTime(new Date())
                .createUserId(supervisorId)
                .deleted(false).build();
        patrolStoreCheckMapper.insertSelective(patrolStoreCheckDO, enterpriseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean storeCheckSubmit(String enterpriseId, PatrolStoreSubmitTableParam param, String userId) {
        if (param.getCheckType() == null) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "稽核类型不能为空");
        }
        if(Objects.nonNull(param.getIsModify()) && param.getIsModify()){
            return storeCheckModify(enterpriseId, param, userId);
        }
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO =
                tbPatrolStoreRecordMapper.selectById(enterpriseId, param.getBusinessId());
        if (tbPatrolStoreRecordDO == null || tbPatrolStoreRecordDO.getDeleted() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    "该businessId无对应记录，businessId:" + param.getBusinessId());
        }

        PatrolStoreCheckDO patrolStoreCheckDO =
                patrolStoreCheckMapper.selectByBusinessId(param.getBusinessId(), enterpriseId);
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(enterpriseId, userId);
        //大区稽核
        if (param.getCheckType() == 1) {
            if (patrolStoreCheckDO.getBigRegionCheckStatus() == 1) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),
                        "该巡店数据大区已稽核，无法再次稽核");
            }
            patrolStoreCheckDO.setBigRegionUserId(userId);
            patrolStoreCheckDO.setBigRegionUserName(enterpriseUserDO.getName());
            patrolStoreCheckDO.setBigRegionUserJobNum(enterpriseUserDO.getJobnumber());
            patrolStoreCheckDO.setBigRegionCheckStatus(1);
            patrolStoreCheckDO.setBigRegionCheckTime(new Date());
        }
        //战区稽核
        if (param.getCheckType() == 2) {
            if (patrolStoreCheckDO.getWarZoneCheckStatus() == 1) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),
                        "该巡店数据战区已稽核，无法再次稽核");
            }
            patrolStoreCheckDO.setWarZoneUserId(userId);
            patrolStoreCheckDO.setWarZoneUserName(enterpriseUserDO.getName());
            patrolStoreCheckDO.setWarZoneUserJobNum(enterpriseUserDO.getJobnumber());
            patrolStoreCheckDO.setWarZoneCheckStatus(1);
            patrolStoreCheckDO.setWarZoneCheckTime(new Date());
        }
        patrolStoreCheckMapper.updateByPrimaryKeySelective(patrolStoreCheckDO, enterpriseId);

        param.getDataTableParamList().forEach(patrolStoreSubmitParam -> {

            TbDataTableDO tbDataTableDo = tbDataTableMapper.selectById(enterpriseId, patrolStoreSubmitParam.getDataTableId());

            TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, tbDataTableDo.getMetaTableId());

            if (CollectionUtils.isEmpty(patrolStoreSubmitParam.getDataStaTableColumnParamList())) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "sta提交内容为空");
            }

            Map<Long, PatrolStoreSubmitParam.DataStaTableColumnParam> columnMap = patrolStoreSubmitParam.getDataStaTableColumnParamList().stream()
                    .collect(Collectors.toMap(PatrolStoreSubmitParam.DataStaTableColumnParam::getId, a -> a));

            List<TbDataStaTableColumnDO> dataStaTableColumnList =
                    tbDataStaTableColumnMapper.selectByDataTableId(enterpriseId, patrolStoreSubmitParam.getDataTableId());

            PatrolCheckDataTableDO patrolCheckDataTableDO = PatrolCheckDataTableDO.builder()
                    .taskId(tbPatrolStoreRecordDO.getTaskId())
                    .subTaskId(tbPatrolStoreRecordDO.getSubTaskId())
                    .checkBusinessId(patrolStoreCheckDO.getId())
                    .businessId(patrolStoreCheckDO.getBusinessId())
                    .metaTableId(metaTableDO.getId())
                    .dataTableId(patrolStoreSubmitParam.getDataTableId())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .tableName(tbDataTableDo.getTableName())
                    .tableType(tbDataTableDo.getTableType())
                    .submitTime(new Date())
                    .submitStatus(1)
                    .deleted(false)
                    .totalScore(metaTableDO.getTotalScore())
                    .tableProperty(metaTableDO.getTableProperty())
                    .businessId(tbPatrolStoreRecordDO.getId())
                    .patrolType(tbPatrolStoreRecordDO.getPatrolType())
                    .checkType(param.getCheckType())
                    .build();
            patrolCheckDataTableMapper.insertSelective(patrolCheckDataTableDO, enterpriseId);

            List<CheckDataStaColumnDO> checkDataStaColumnList = new ArrayList<>();

            dataStaTableColumnList.forEach(staTableColumnDO -> {
                PatrolStoreSubmitParam.DataStaTableColumnParam a = columnMap.get(staTableColumnDO.getId());
                CheckDataStaColumnDO checkDataStaColumnDO = CheckDataStaColumnDO.builder()
                        .taskId(staTableColumnDO.getTaskId())
                        .businessId(staTableColumnDO.getBusinessId())
                        .dataTableId(staTableColumnDO.getDataTableId())
                        .checkDataTableId(patrolCheckDataTableDO.getId())
                        .dataStaColumnId(staTableColumnDO.getId())
                        .metaTableId(metaTableDO.getId())
                        .metaColumnId(staTableColumnDO.getMetaColumnId())
                        .metaColumnName(staTableColumnDO.getMetaColumnName())
                        .description(staTableColumnDO.getDescription())
                        .deleted(false).columnType(staTableColumnDO.getColumnType())
                        .patrolType(staTableColumnDO.getPatrolType())
                        .patrolStoreTime(staTableColumnDO.getPatrolStoreTime())
                        .columnMaxScore(staTableColumnDO.getColumnMaxScore())
                        .columnMaxAward(staTableColumnDO.getColumnMaxAward())
                        .categoryName(staTableColumnDO.getCategoryName()).build();
                checkDataStaColumnDO.setCheckResult(a.getCheckResult());
                checkDataStaColumnDO.setCheckResultId(a.getCheckResultId());
                checkDataStaColumnDO.setCheckResultName(a.getCheckResultName());
                checkDataStaColumnDO.setSupervisorId(userId);
                checkDataStaColumnDO.setCheckPics(a.getCheckPics());
                checkDataStaColumnDO.setCheckVideo(a.getCheckVideo());
                checkDataStaColumnDO.setCheckText(a.getCheckText());
                checkDataStaColumnDO.setAwardTimes(a.getAwardTimes());
                checkDataStaColumnDO.setScoreTimes(a.getScoreTimes());
                checkDataStaColumnDO.setCheckResultReason(a.getCheckResultReason());
                checkDataStaColumnDO.setCheckScore(a.getCheckScore());
                checkDataStaColumnDO.setCheckType(param.getCheckType());
                checkDataStaColumnDO.setRewardPenaltMoney(BigDecimal.ZERO);
                checkDataStaColumnList.add(checkDataStaColumnDO);
            });
            checkDataStaColumnMapper.batchInsert(enterpriseId, checkDataStaColumnList);

            //计算得分
            countCheckTableScore(enterpriseId, tbPatrolStoreRecordDO, metaTableDO, patrolCheckDataTableDO.getId(), checkDataStaColumnList);
        });
        return true;
    }


    public Boolean storeCheckModify(String enterpriseId, PatrolStoreSubmitTableParam param, String userId) {
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, param.getBusinessId());
        if (tbPatrolStoreRecordDO == null || tbPatrolStoreRecordDO.getDeleted() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该businessId无对应记录，businessId:" + param.getBusinessId());
        }
        List<Long> dataTableIds = param.getDataTableParamList().stream().map(PatrolStoreSubmitParam::getDataTableId).distinct().collect(Collectors.toList());
        List<CheckDataStaColumnDO> checkDataStaColumnDOS =tbCheckDataStaColumnMapper.selectDataColumnById(enterpriseId,new ArrayList<>(dataTableIds),param.getCheckType());
        Map<Long, CheckDataStaColumnDO> columnDOMap = ListUtils.emptyIfNull(checkDataStaColumnDOS).stream().collect(Collectors.toMap(CheckDataStaColumnDO::getId, data -> data, (a, b) -> a));
        param.getDataTableParamList().forEach(patrolStoreSubmitParam -> {
            List<CheckDataStaColumnDO> dataStaTableColumnList = new ArrayList<>();
            if(CollectionUtils.isEmpty(patrolStoreSubmitParam.getDataStaCheckTableColumnParamList()) || Objects.isNull(patrolStoreSubmitParam.getCheckDataTableId())){
                return;
            }
            PatrolCheckDataTableDO patrolCheckDataTableDO = patrolCheckDataTableMapper.selectByPrimaryKey(patrolStoreSubmitParam.getCheckDataTableId(), enterpriseId);
            TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, patrolCheckDataTableDO.getMetaTableId());
            patrolStoreSubmitParam.getDataStaCheckTableColumnParamList().forEach(a -> {
                CheckDataStaColumnDO dataStaColumnDO = columnDOMap.get(a.getId());
                CheckDataStaColumnDO checkDataStaColumnDO = new CheckDataStaColumnDO();
                if(Objects.nonNull(dataStaColumnDO)){
                    checkDataStaColumnDO.setMetaColumnId(dataStaColumnDO.getMetaColumnId());
                }
                checkDataStaColumnDO.setId(a.getId());
                checkDataStaColumnDO.setCheckResult(a.getCheckResult());
                checkDataStaColumnDO.setCheckResultId(a.getCheckResultId());
                checkDataStaColumnDO.setCheckResultName(a.getCheckResultName());
                checkDataStaColumnDO.setSupervisorId(userId);
                checkDataStaColumnDO.setCheckPics(a.getCheckPics());
                checkDataStaColumnDO.setCheckVideo(a.getCheckVideo());
                checkDataStaColumnDO.setCheckText(a.getCheckText());
                checkDataStaColumnDO.setAwardTimes(a.getAwardTimes());
                checkDataStaColumnDO.setScoreTimes(a.getScoreTimes());
                checkDataStaColumnDO.setCheckResultReason(a.getCheckResultReason());
                checkDataStaColumnDO.setCheckScore(a.getCheckScore());
                checkDataStaColumnDO.setCheckType(param.getCheckType());
                checkDataStaColumnDO.setRewardPenaltMoney(BigDecimal.ZERO);
                dataStaTableColumnList.add(checkDataStaColumnDO);
            });
            checkDataStaColumnMapper.batchUpdateColumn(enterpriseId, dataStaTableColumnList);
            //计算得分
            countCheckTableScore(enterpriseId, tbPatrolStoreRecordDO, metaTableDO, patrolCheckDataTableDO.getId(), dataStaTableColumnList);
        });
        return true;
    }


    @Override
    public void countCheckTableScore(String eid, TbPatrolStoreRecordDO recordDO, TbMetaTableDO tbMetaTable, Long checkDataTableId,
                                     List<CheckDataStaColumnDO> dataStaTableColumnList) {
        List<CheckDataStaColumnDO> staColumnList = new ArrayList<>();

        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList = tbMetaColumnResultMapper.selectByMetaTableId(eid, tbMetaTable.getId());
        Map<Long, TbMetaColumnResultDO> columnIdResultMap = columnResultDOList.stream().collect(Collectors.toMap(TbMetaColumnResultDO::getId, Function.identity(), (a, b) -> a));
        List<TbMetaStaTableColumnDO> list = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(eid, Collections.singletonList(tbMetaTable.getId()));

        Map<Long, TbMetaStaTableColumnDO> idMetaTableColumnMap = list.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));
        //每一项最高奖金
        Map<Long, BigDecimal> columnMaxAwardMap = AbstractColumnObserver.getColumnMaxAwardMap(tbMetaTable, columnResultDOList);
        AtomicInteger failNum = new AtomicInteger();
        AtomicInteger passNum = new AtomicInteger();
        AtomicInteger inapplicableNum = new AtomicInteger();
        for (CheckDataStaColumnDO a : dataStaTableColumnList) {
            CheckDataStaColumnDO tableColumnDO = CheckDataStaColumnDO.builder().id(a.getId()).build();
            TbMetaStaTableColumnDO tbMetaStaTableColumnDO = idMetaTableColumnMap.get(a.getMetaColumnId());
            // 修改标准检查项
            if (a.getCheckResultId() != null && a.getCheckResultId() > 0) {
                TbMetaColumnResultDO tbMetaColumnResultDO = columnIdResultMap.get(a.getCheckResultId());
                if (tbMetaColumnResultDO != null) {
                    tableColumnDO.setRewardPenaltMoney(tbMetaColumnResultDO.getMoney());
                }
            } else {
                if (tbMetaStaTableColumnDO != null) {
                    if (PASS.equals(a.getCheckResult())) {
                        tableColumnDO.setRewardPenaltMoney(tbMetaStaTableColumnDO.getAwardMoney());
                    }
                    if (FAIL.equals(a.getCheckResult())) {
                        tableColumnDO.setRewardPenaltMoney(tbMetaStaTableColumnDO.getPunishMoney().abs().multiply(new BigDecimal("-1")));
                    }
                }
            }
            BigDecimal columnMaxAward = new BigDecimal(Constants.ZERO_STR);
            if (columnMaxAwardMap.get(a.getMetaColumnId()) != null) {
                columnMaxAward = columnMaxAwardMap.get(a.getMetaColumnId());
            }
            if (tbMetaStaTableColumnDO != null) {
                tableColumnDO.setWeightPercent(tbMetaStaTableColumnDO.getWeightPercent());
                tableColumnDO.setColumnMaxScore(tbMetaStaTableColumnDO.getSupportScore());
                tableColumnDO.setColumnMaxAward(columnMaxAward);
            }
            //获取检查结果
            String checkResult = a.getCheckResult();
            //采集项不参与计算
            if (StringUtils.isNotBlank(checkResult) && tbMetaStaTableColumnDO != null &&
                    !MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(tbMetaStaTableColumnDO.getColumnType())) {
                //计算合格项数
                switch (checkResult) {
                    case PASS:
                        passNum.getAndIncrement();
                        break;
                    case FAIL:
                        failNum.getAndIncrement();
                        break;
                    case INAPPLICABLE:
                        inapplicableNum.getAndIncrement();
                        break;
                    default:
                }
            }
            //用于计算
            a.setWeightPercent(tableColumnDO.getWeightPercent());
            a.setColumnMaxScore(tableColumnDO.getColumnMaxScore());
            a.setColumnMaxAward(tableColumnDO.getColumnMaxAward());
            if (tableColumnDO.getRewardPenaltMoney() != null) {
                a.setRewardPenaltMoney(tableColumnDO.getRewardPenaltMoney());
            }
            if(a.getRewardPenaltMoney() == null){
                a.setRewardPenaltMoney(BigDecimal.ZERO);
            }
            staColumnList.add(tableColumnDO);
        }
        checkDataStaColumnMapper.batchUpdate(eid, recordDO.getId(), PATROL_STORE,
                checkDataTableId, staColumnList);
        //计算得分
        List<CalColumnScoreDTO> calColumnScoreList = this.buildCalColumnStore(dataStaTableColumnList, idMetaTableColumnMap);
        CalTableResultDTO checkResult = AbstractColumnObserver.getSingleTableResult(new CalTableScoreDTO(checkDataTableId, tbMetaTable, calColumnScoreList));
        //计算得分项数--

        PatrolCheckDataTableDO patrolCheckDataTableDO = new PatrolCheckDataTableDO();
        patrolCheckDataTableDO.setId(checkDataTableId);
        patrolCheckDataTableDO.setTaskCalTotalScore(checkResult.getCalTotalScore());
        patrolCheckDataTableDO.setTotalScore(tbMetaTable.getTotalScore());
        patrolCheckDataTableDO.setCheckScore(checkResult.getResultScore());
        patrolCheckDataTableDO.setTotalResultAward(checkResult.getResultAward());
        patrolCheckDataTableDO.setNoApplicableRule(tbMetaTable.getNoApplicableRule());
        patrolCheckDataTableDO.setFailNum(failNum.get());
        patrolCheckDataTableDO.setPassNum(passNum.get());
        patrolCheckDataTableDO.setInapplicableNum(inapplicableNum.get());
        patrolCheckDataTableDO.setTotalCalColumnNum(checkResult.getTotalCalColumnNum());
        patrolCheckDataTableDO.setCollectColumnNum(checkResult.getCollectColumnNum());
        patrolCheckDataTableDO.setCheckResultLevel(patrolStoreService.getCheckResultLevel(passNum.get(), tbMetaTable, checkResult.getResultScore(), checkResult.getCalTotalScore()));
        patrolCheckDataTableMapper.updateByPrimaryKeySelective(patrolCheckDataTableDO, eid);
    }

    @Override
    public boolean canCheck(String enterpriseId, String userId, Integer checkType) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        // 判断是否是管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if (isAdmin) {
            return true;
        }
        String checkUser = null;
        if(checkType !=null && checkType == 1){
            //大区稽核
            checkUser = enterpriseStoreCheckSettingDO.getBigRegionCheckUser();
        }else {
            //战区
            checkUser = enterpriseStoreCheckSettingDO.getWarZoneCheckUser();
        }
        boolean canCheck = false;
        if (StringUtils.isNotBlank(checkUser)) {
            TaskProcessDTO taskProcessDTO = JSONObject.parseObject(checkUser, TaskProcessDTO.class);
            List<String> queryUserIdList = userPersonInfoService.getUserIdListByTaskProcess(enterpriseId, Collections.singletonList(taskProcessDTO));
            if (CollectionUtils.isNotEmpty(queryUserIdList) && new HashSet<>(queryUserIdList).contains(userId)) {
                canCheck = true;
            }
        }
        return canCheck;
    }


    private List<CalColumnScoreDTO> buildCalColumnStore(List<CheckDataStaColumnDO> dataStaTableColumnList, Map<Long, TbMetaStaTableColumnDO> metaTableColumnMap) {
        if (CollectionUtils.isEmpty(dataStaTableColumnList)) {
            return null;
        }
        List<CalColumnScoreDTO> resultList = new ArrayList<>();
        for (CheckDataStaColumnDO dataColumn : dataStaTableColumnList) {
            TbMetaStaTableColumnDO tbMetaStaTableColumn = metaTableColumnMap.get(dataColumn.getMetaColumnId());
            if (Objects.isNull(tbMetaStaTableColumn)) {
                continue;
            }
            CalColumnScoreDTO calColumnScore = CalColumnScoreDTO.builder().score(dataColumn.getCheckScore()).columnName(dataColumn.getMetaColumnName())
                    .scoreTimes(dataColumn.getScoreTimes()).awardTimes(dataColumn.getAwardTimes()).weightPercent(dataColumn.getWeightPercent())
                    .columnTypeEnum(MetaColumnTypeEnum.getColumnType(tbMetaStaTableColumn.getColumnType())).categoryName(tbMetaStaTableColumn.getCategoryName())
                    .checkResult(CheckResultEnum.getCheckResultEnum(dataColumn.getCheckResult())).columnMaxScore(dataColumn.getColumnMaxScore())
                    .rewardPenaltMoney(dataColumn.getRewardPenaltMoney()).build();
            resultList.add(calColumnScore);
        }
        return resultList;
    }
}
