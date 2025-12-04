package com.coolcollege.intelligent.service.operationboard.impl;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.TableTypeConstant.STANDARD;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardRankDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardStatisticDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardTrendDTO;
import com.coolcollege.intelligent.model.operationboard.query.TableBoardQuery;
import com.coolcollege.intelligent.service.operationboard.TableBoardService;
import com.coolcollege.intelligent.service.region.RegionService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yezhe
 * @date 2021-01-08 15:56
 */
@Service
@Slf4j
public class TableBoardServiceImpl implements TableBoardService {

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private StoreMapper storeMapper;
    @Lazy
    @Resource
    private RegionService regionService;
    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Override
    public TableBoardStatisticDTO statistics(String enterpriseId, TableBoardQuery query) {
        TableBoardStatisticDTO result = TableBoardStatisticDTO.builder().build();
        List<Long> metaTableId = query.getMetaTableIds();
        if (metaTableId == null) {
            // 所有方案汇总信息
            // 方案总数
            Integer tableNum = tbMetaTableMapper.count(enterpriseId, STANDARD, null);
            result.setTableNum(tableNum == null ? 0 : tableNum);
        } else {
            // 单个方案汇总信息
            // 检查项总数
            Integer columnNum = tbMetaStaTableColumnMapper.countByMetaTableId(enterpriseId, metaTableId);
            result.setColumnNum(columnNum == null ? 0 : columnNum);
            // 总门店数
            Integer enterpriseStoreNum = storeMapper.countStore(enterpriseId);
            result.setEnterpriseStoreNum(enterpriseStoreNum == null ? 0 : enterpriseStoreNum);
        }
        // 总使用次数、总检查门店数
        TableBoardStatisticDTO statistic = tbDataTableMapper.tableBoardStatistics(enterpriseId,
            metaTableId, query.getBeginDate(), query.getEndDate());
        if (statistic != null) {
            result.setPatrolNum(statistic.getPatrolNum());
            result.setPatrolStoreNum(statistic.getPatrolStoreNum());
        }
        // 创建问题数
        Integer questionNum = tbDataStaTableColumnMapper.getQuestionNum(enterpriseId, metaTableId, query.getBeginDate(),
            query.getEndDate());
        result.setTotalQuestionNum(questionNum == null ? 0 : questionNum);
        return result;
    }

    @Override
    public List<TableBoardRankDTO> rank(String enterpriseId, TableBoardQuery query) {
        // 使用次数、检查门店数
        List<TableBoardRankDTO> ranks =
                tbDataTableMapper.tableBoardRank(enterpriseId, query.getBeginDate(), query.getEndDate(),query.getMetaTableIds());
        // 表名
        List<TbMetaTableDO> tables = tbMetaTableMapper.getAll(enterpriseId, STANDARD,query.getMetaTableIds());
        Map<Long, String> tableIdNameMap =
            tables.stream()
                    .filter(a->a.getId()!=null&&a.getTableName()!=null)
                    .collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName, (a, b) -> a));
        // 创建问题数
        List<TableBoardRankDTO> questionNums = tbDataStaTableColumnMapper.tableBoardRankQuestionNum(enterpriseId,
            query.getBeginDate(), query.getEndDate(),query.getMetaTableIds());
        Map<Long, Integer> tableIdQuestionNumMap = questionNums.stream()
                .filter(a->a.getMetaTableId()!=null)
                .collect(Collectors.toMap(TableBoardRankDTO::getMetaTableId, TableBoardRankDTO::getTotalQuestionNum, (a, b) -> a));
        // 汇总
        ranks.forEach(a -> {
            a.setTableName(tableIdNameMap.get(a.getMetaTableId()));
            a.setTotalQuestionNum(tableIdQuestionNumMap.getOrDefault(a.getMetaTableId(), 0));
            a.setDefaultTableList(query.getDefaultTables());
        });
        return ranks;
    }

    @Override
    public List<TableBoardTrendDTO> trend(String enterpriseId, TableBoardQuery query) {
        Long regionId = query.getRegionId();
        // 根据regionId获取regionPath
        if (regionId != null) {
            String regionPathLeft = StringUtils
                .substringBeforeLast(regionService.getRegionPath(enterpriseId, String.valueOf(regionId)), "]");
            query.setRegionPathLeft(regionPathLeft);
        }
        // 使用次数、检查门店数
        List<TableBoardTrendDTO> trends = tbDataTableMapper.tableBoardTrend(enterpriseId, query);
        // 创建问题数
        List<TableBoardTrendDTO> questionNums =
            tbDataStaTableColumnMapper.tableBoardTrendQuestionNum(enterpriseId, query);
        Map<String, Integer> createDateQuestionNumMap = questionNums.stream()
                .filter(a->a.getCreateDate()!=null)
                .collect(Collectors.toMap(TableBoardTrendDTO::getCreateDate, TableBoardTrendDTO::getTotalQuestionNum, (a, b) -> a));
        // 汇总
        trends.forEach(a -> {
            a.setTotalQuestionNum(createDateQuestionNumMap.getOrDefault(a.getCreateDate(), 0));
            a.setDefaultTables(query.getDefaultTables());
        });
        return trends;
    }
}
