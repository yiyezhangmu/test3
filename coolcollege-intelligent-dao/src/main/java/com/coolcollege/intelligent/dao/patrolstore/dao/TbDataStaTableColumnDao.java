package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnReasonValueDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreReviewVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangnan
 * @date 2020-12-9
 */
@Repository
@Slf4j
public class TbDataStaTableColumnDao {

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    /**
     * 根据id列表查询
     * @param enterpriseId 企业id
     * @param ids id列表
     * @return List<TbDataStaTableColumnDO>
     */
    public List<TbDataStaTableColumnDO> selectByIds(String enterpriseId, List<Long> ids){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return tbDataStaTableColumnMapper.selectByIds(enterpriseId,ids);
    }

    /**
     * 根据id列表查询
     * @param enterpriseId
     * @param id
     * @return
     */
    public TbDataStaTableColumnDO selectById(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return null;
        }
        return tbDataStaTableColumnMapper.selectByPrimaryKey(enterpriseId,id);
    }

    public List<PatrolStoreReviewVO> exportPatrolStoreReviewList(String enterpriseId, int pageNum, int pageSize,List<String> recordIds){
        //0.查询待复审数据,1.查询已复审数据
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, recordIds.size());
        List<String> paginatedRecordIds = new ArrayList<>();
        if (startIndex >= 0 && startIndex < recordIds.size()) {
            paginatedRecordIds = recordIds.subList(startIndex, endIndex);
        } else {
            paginatedRecordIds = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(paginatedRecordIds)){
            return null;
        }
        List<TbDataStaTableColumnDO> dos= tbDataStaTableColumnMapper.selectPatrolStoreReviewListByBusinessId(enterpriseId,paginatedRecordIds);
        return convertToPatrolStoreReviewVO(enterpriseId, dos);
    }
    //do转vo
    private List<PatrolStoreReviewVO> convertToPatrolStoreReviewVO(String eId,List<TbDataStaTableColumnDO> dos){
        if (CollectionUtils.isEmpty(dos)){
            return null;
        }
        List<PatrolStoreReviewVO> vos = Lists.newArrayList();
        List<Long> businessIdList = dos.stream()
                .filter(item -> item.getBusinessId() != null)
                .map(TbDataStaTableColumnDO::getBusinessId)
                .collect(Collectors.toList());
        Map<Long, Date> recordMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(businessIdList)){
            List<TbPatrolStoreRecordDO> recordDOList = tbPatrolStoreRecordMapper.selectByIds(eId, businessIdList);
            if (CollectionUtils.isNotEmpty(recordDOList)){
                recordMap = ListUtils.emptyIfNull(recordDOList).stream().collect(Collectors.toMap(TbPatrolStoreRecordDO::getId, TbPatrolStoreRecordDO::getSignStartTime));
            }
        }
        Map<Long, Date> finalRecordMap = recordMap;
        dos.stream().forEach(columnDO -> {
            PatrolStoreReviewVO vo = new PatrolStoreReviewVO();
            vo.setBusinessId(columnDO.getBusinessId());
            vo.setStoreName(columnDO.getStoreName());
            String regionWay = columnDO.getRegionWay();
            List<String> regionIds = new ArrayList<>(Arrays.asList(regionWay.split("/")));
            if(regionIds.size() > 1){
                regionIds.remove(regionIds.size() - 1);
            }
            List<String> regionsName=regionMapper.getNameByIds(eId,regionIds);
            vo.setRegionPathName(regionsName.stream().collect(Collectors.joining("-")));
            String patrolUserName = enterpriseUserDao.selectNameByUserId(eId, columnDO.getSupervisorId());
            vo.setPatrolUserName(patrolUserName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String patrolDate = dateFormat.format(columnDO.getPatrolStoreTime());
            if (Objects.nonNull(finalRecordMap)){
                if(finalRecordMap.get(columnDO.getBusinessId()) != null){
                    patrolDate = dateFormat.format(finalRecordMap.get(columnDO.getBusinessId()));
                }
            }
            vo.setPatrolDate(patrolDate);
            vo.setTaskId(columnDO.getTaskId());
            //可能为0
            if (Long.valueOf("0").equals(columnDO.getTaskId())){
                vo.setTaskName("");
            }else {
                TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(eId, columnDO.getTaskId());
                if (Objects.isNull(taskParentDO)){
                    vo.setTaskName("");
                }else {
                    vo.setTaskName(taskParentDO.getTaskName());
                }
            }
            String tableName = tbDataTableMapper.selectTableNameById(eId, columnDO.getDataTableId());
            vo.setTableName(tableName);
            vo.setMetaColumnName(columnDO.getMetaColumnName());
            vo.setCheckResultName(columnDO.getCheckResultName());
            String standardPic = tbMetaStaTableColumnMapper.selectByIds(eId, Arrays.asList(columnDO.getMetaColumnId())).get(0).getStandardPic();
            vo.setStandardPics(standardPic);
            vo.setCheckScore(columnDO.getCheckScore());
            vo.setCheckPics(columnDO.getCheckPics());
            //检查是否有复审记录
            List<TbPatrolStoreRecordDO> recheckDO =tbPatrolStoreRecordMapper.getByRecheckBusinessId(eId,columnDO.getBusinessId());
            //只支持三条复审记录
            if(CollectionUtils.isEmpty(recheckDO)){
                vos.add(vo);
            }else {
                switch (recheckDO.size()){
                    case 3:
                        //根据recordId查询column记录
                        Long id3 = recheckDO.get(2).getId();
                        List<TbDataStaTableColumnDO> collect3 = tbDataStaTableColumnMapper.selectByBusinessId(eId, id3, BusinessCheckType.PATROL_RECHECK.getCode())
                                .stream()
                                .filter(c -> c.getMetaColumnId().equals(columnDO.getMetaColumnId()))
                                .collect(Collectors.toList());
                        if(CollectionUtils.isEmpty(collect3)){
                            log.error("复审记录中没有找到对应的column记录");
                        }
                        TbDataStaTableColumnDO columnDO3 = collect3.get(0);
                        vo.setThirdRecheckResultName(columnDO3.getCheckResultName());
                        String checkResultReason3 = columnDO3.getCheckResultReason();
                        checkReason(vo, checkResultReason3);
                        vo.setThirdRecheckScore(columnDO3.getCheckScore());
                        vo.setThirdRecheckUserName(recheckDO.get(2).getRecheckUserName());
                        vo.setThirdRecheckDate(dateFormat.format(recheckDO.get(2).getCreateTime()));
                    case 2:
                        //根据recordId查询column记录
                        Long id2 = recheckDO.get(1).getId();
                        List<TbDataStaTableColumnDO> collect2 = tbDataStaTableColumnMapper.selectByBusinessId(eId, id2, BusinessCheckType.PATROL_RECHECK.getCode())
                                .stream()
                                .filter(c -> c.getMetaColumnId().equals(columnDO.getMetaColumnId()))
                                .collect(Collectors.toList());
                        if(CollectionUtils.isEmpty(collect2)){
                            log.error("复审记录中没有找到对应的column记录");
                        }
                        TbDataStaTableColumnDO columnDO2 = collect2.get(0);
                        vo.setSecondRecheckResultName(columnDO2.getCheckResultName());
                        String checkResultReason2 = columnDO2.getCheckResultReason();
                        checkReason(vo, checkResultReason2);
                        vo.setSecondRecheckScore(columnDO2.getCheckScore());
                        vo.setSecondRecheckUserName(recheckDO.get(1).getRecheckUserName());
                        vo.setSecondRecheckDate(dateFormat.format(recheckDO.get(1).getCreateTime()));
                    case 1:
                        //根据recordId查询column记录
                        Long id1 = recheckDO.get(0).getId();
                        List<TbDataStaTableColumnDO> collect1 = tbDataStaTableColumnMapper.selectByBusinessId(eId, id1, BusinessCheckType.PATROL_RECHECK.getCode())
                                .stream()
                                .filter(c -> c.getMetaColumnId().equals(columnDO.getMetaColumnId()))
                                .collect(Collectors.toList());
                        if(CollectionUtils.isEmpty(collect1)){
                            log.error("复审记录中没有找到对应的column记录");
                        }
                        TbDataStaTableColumnDO columnDO1 = collect1.get(0);
                        vo.setRecheckResultName(columnDO1.getCheckResultName());
                        String checkResultReason1 = columnDO1.getCheckResultReason();
                        checkReason(vo, checkResultReason1);
                        vo.setRecheckScore(columnDO1.getCheckScore());
                        vo.setRecheckUserName(recheckDO.get(0).getRecheckUserName());
                        vo.setRecheckDate(dateFormat.format(recheckDO.get(0).getCreateTime()));
                        vos.add(vo);
                        break;
                }
        }
        });
        return vos;
    }

    private void checkReason(PatrolStoreReviewVO vo, String checkResultReason) {
        if (StringUtils.isBlank(checkResultReason)){
            vo.setThirdCheckResultReason("");
        }else {
            List<ColumnReasonValueDTO> columnReasonValueDTOS3 = JSONObject.parseArray(checkResultReason, ColumnReasonValueDTO.class);
            if (CollectionUtils.isEmpty(columnReasonValueDTOS3)){
                vo.setThirdCheckResultReason("");
            }else {
                String reasons = columnReasonValueDTOS3.stream().map(ColumnReasonValueDTO::getReasonName).collect(Collectors.joining(","));
                vo.setThirdCheckResultReason(reasons);
            }
        }
    }

}