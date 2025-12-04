package com.coolcollege.intelligent.service.safetycheck.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordInfoMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.query.SafetyCheckCountQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsUserDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.SafetyCheckScoreUserDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.SafetyCheckUserDTO;
import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckCountVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.safetycheck.SafetyCheckCountService;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2023-09-08 13:46
 */
@Service
public class SafetyCheckCountServiceImpl implements SafetyCheckCountService {

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;

    @Autowired
    private TbPatrolStoreRecordMapper patrolStoreRecordMapper;

    @Autowired
    private TbDataTableMapper dataTableMapper;

    @Autowired
    private TbPatrolStoreRecordInfoMapper patrolStoreRecordInfoMapper;

    @Autowired
    private SimpleMessageService simpleMessageService;

    @Autowired
    private ImportTaskService importTaskService;

    @Override
    public PageInfo<ScSafetyCheckCountVO> list(String eid, SafetyCheckCountQuery checkCountQuery) {


        List<String> userIdList = checkCountQuery.getUserIdList();
        // 分页
        PageHelper.startPage(checkCountQuery.getPageNum(), checkCountQuery.getPageSize());
        userIdList = enterpriseUserMapper.selectUserIdsByUserList(eid, userIdList);
        PageInfo pageInfo = new PageInfo(userIdList);
        if (CollectionUtils.isEmpty(userIdList)) {
            return pageInfo;
        }
        Long beginTime = checkCountQuery.getBeginTime();
        Long endTime = checkCountQuery.getEndTime();

        String beginDate = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endDate = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_SEC);
        // 获取人员信息
        List<EnterpriseUserDTO> userDTOList = enterpriseUserMapper.getUserDetailList(eid, userIdList);
        Map<String, String> userMap = userDTOList.stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDTO::getUserId, EnterpriseUserDTO::getName, (a, b) -> a));
        //次数
        List<PatrolStoreStatisticsUserDTO> patrolStoreStatisticsUserList =
                patrolStoreRecordMapper.statisticsSafetyCheckUser(eid, userIdList, new Date(beginTime), new Date(endTime));

        Map<String, PatrolStoreStatisticsUserDTO> statisticsUserStoreNumMap = ListUtils.emptyIfNull(patrolStoreStatisticsUserList)
                .stream().collect(Collectors.toMap(PatrolStoreStatisticsUserDTO::getUserId, Function.identity()));

        List<SafetyCheckUserDTO> statisticsSafetyCheckUserList = dataTableMapper.statisticsSafetyCheckUser(eid, userIdList, beginDate, endDate);

        Map<String, SafetyCheckUserDTO> checkUserMap = ListUtils.emptyIfNull(statisticsSafetyCheckUserList)
                .stream().collect(Collectors.toMap(SafetyCheckUserDTO::getUserId, Function.identity()));


        List<SafetyCheckScoreUserDTO> safetyCheckScoreUserList = dataTableMapper.statisticsSafetyCheckUserScore(eid, userIdList, beginDate, endDate);

        Map<String, SafetyCheckScoreUserDTO> safetyCheckScoreUserMap = ListUtils.emptyIfNull(safetyCheckScoreUserList)
                .stream().collect(Collectors.toMap(SafetyCheckScoreUserDTO::getUserId, Function.identity()));

        List<ScSafetyCheckCountVO> scSafetyCheckCountVOList =
                patrolStoreRecordInfoMapper.statisticsSafetyCheckUser(eid, userIdList, beginDate, endDate);
        Map<String, ScSafetyCheckCountVO> safetyCheckCountMap = ListUtils.emptyIfNull(scSafetyCheckCountVOList)
                .stream().collect(Collectors.toMap(ScSafetyCheckCountVO::getUserId, Function.identity()));

        List<ScSafetyCheckCountVO> checkCountVOList = new ArrayList<>();

        userIdList.forEach(userId -> {
            ScSafetyCheckCountVO scSafetyCheckCountVO = new ScSafetyCheckCountVO();
            scSafetyCheckCountVO.setUserId(userId);
            scSafetyCheckCountVO.setUserName(userMap.get(userId));
            PatrolStoreStatisticsUserDTO patrolStoreStatisticsUserDTO = statisticsUserStoreNumMap.get(userId);
            if(patrolStoreStatisticsUserDTO != null){
                scSafetyCheckCountVO.setPatrolStoreNum(patrolStoreStatisticsUserDTO.getPatrolNum());
            }else {
                scSafetyCheckCountVO.setPatrolStoreNum(0);
            }
            SafetyCheckUserDTO safetyCheckUserDTO = checkUserMap.get(userId);
            if(safetyCheckUserDTO != null && safetyCheckUserDTO.getPatrolNum() != 0){
                scSafetyCheckCountVO.setStoreAvgScore(safetyCheckUserDTO.getTotalCheckScore().divide(new BigDecimal(safetyCheckUserDTO.getPatrolNum())
                        , 2, RoundingMode.HALF_UP));
            }else {
                scSafetyCheckCountVO.setStoreAvgScore(BigDecimal.ZERO);
            }

            SafetyCheckScoreUserDTO safetyCheckScoreUserDTO = safetyCheckScoreUserMap.get(userId);
            if(safetyCheckScoreUserDTO != null){
                scSafetyCheckCountVO.setNinetyScoreStoreNum(safetyCheckScoreUserDTO.getNinetyScoreStoreNum());
                scSafetyCheckCountVO.setEightyScoreStoreNum(safetyCheckScoreUserDTO.getEightyScoreStoreNum());
                scSafetyCheckCountVO.setEightyDownScoreStoreNum(safetyCheckScoreUserDTO.getEightyDownScoreStoreNum());
            }else {
                scSafetyCheckCountVO.setNinetyScoreStoreNum(0L);
                scSafetyCheckCountVO.setEightyScoreStoreNum(0L);
                scSafetyCheckCountVO.setEightyDownScoreStoreNum(0L);
            }
            ScSafetyCheckCountVO scSafetyCheckCountNum = safetyCheckCountMap.get(userId);
            if(scSafetyCheckCountNum != null){
                scSafetyCheckCountVO.setAuditRejectNum(scSafetyCheckCountNum.getAuditRejectNum());
                scSafetyCheckCountVO.setStoreAppealPassNum(scSafetyCheckCountNum.getStoreAppealPassNum());
                scSafetyCheckCountVO.setStoreAppealRejectNum(scSafetyCheckCountNum.getStoreAppealRejectNum());
            }else {
                scSafetyCheckCountVO.setAuditRejectNum(0L);
                scSafetyCheckCountVO.setStoreAppealPassNum(0L);
                scSafetyCheckCountVO.setStoreAppealRejectNum(0L);
            }
            checkCountVOList.add(scSafetyCheckCountVO);
        });
        pageInfo.setList(checkCountVOList);
        return pageInfo;
    }

    @Override
    public ImportTaskDO exportList(String eid, SafetyCheckCountQuery checkCountQuery, String dbName) {
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;

        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.SAFETY_CHECK_COUNT_EXPORT.getFileName();
        checkCountQuery.setExportServiceEnum(ExportServiceEnum.SAFETY_CHECK_COUNT_EXPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(eid, fileName, ExportServiceEnum.SAFETY_CHECK_COUNT_EXPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(eid);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(checkCountQuery)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(dbName);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }
}
