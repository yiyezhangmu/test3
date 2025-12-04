package com.coolcollege.intelligent.service.metatable.calscore;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnResultDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaStaColumnDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaStaTableDTO;
import com.coolcollege.intelligent.service.metatable.calscore.column.*;
import com.coolcollege.intelligent.service.metatable.calscore.table.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: AbstractCalScoreColumn
 * @Description: 抽象项观察责
 * @date 2022-04-01 20:50
 */
@Slf4j
public abstract class AbstractColumnObserver {

    /**
     * 项计算分数
     * @param columnScore
     * @return
     */
    public BigDecimal calColumnScore(CalTableScoreDTO tableScore, CalColumnScoreDTO columnScore){
        return columnScore.getScore();
    }

    /**
     * 表计算总得分
     * @param calTableScore
     * @return
     */
    protected void calTableResult(CalTableScoreDTO calTableScore, CalTableResultDTO tableResult){
        //获取所有的表信息
        //不适用规则 false:不计入总项数，true:计入总项数
        boolean noApplicableRule = getNoApplicableRule(calTableScore);
        List<CalColumnScoreDTO> calColumnScoreList = sortColumnList(calTableScore.getCalColumnList());
        int totalCalColumnNum = 0;
        int collectColumnNum = 0;
        BigDecimal calTotalScore = new BigDecimal(Constants.ZERO_STR);
        for (CalColumnScoreDTO calColumnScore : calColumnScoreList) {
            //采集项不算总分  也不计入总项数  直接累计
            if(MetaColumnTypeEnum.COLLECT_COLUMN.equals(calColumnScore.getColumnTypeEnum())){
                //采集项累加
                collectColumnNum++;
                continue;
            }
            //不适用不计入项数
            if(!noApplicableRule && CheckResultEnum.INAPPLICABLE.equals(calColumnScore.getCheckResult())){
                continue;
            }
            totalCalColumnNum++;
            calTotalScore = calTotalScore.add(calColumnScore.getColumnMaxScore());
            AbstractColumnObserver columnCalScoreHandler = getColumnCalScoreHandler(calColumnScore.getColumnTypeEnum());
            if(Objects.isNull(columnCalScoreHandler)){
                continue;
            }
            //项的分数
            BigDecimal columnScore = columnCalScoreHandler.calColumnScore(calTableScore, calColumnScore);
            //表的累计得分
            BigDecimal tableScore = tableResult.getResultScore().add(columnScore);
            tableResult.setResultScore(tableScore);
            //项的奖罚
            BigDecimal columnAward = calColumnScore.getRewardPenaltMoney().multiply(calColumnScore.getAwardTimes());
            tableResult.setResultAward(tableResult.getResultAward().add(columnAward));
        }
        tableResult.setTotalCalColumnNum(totalCalColumnNum);
        tableResult.setCollectColumnNum(collectColumnNum);
        tableResult.setCalTotalScore(calTotalScore);
    }

    /**
     * 多表任务 计算任务检查结果
     * @param calTableList
     * @return
     */
    public static CalResultDTO getMultiTableResult(List<CalTableScoreDTO> calTableList){
        CalResultDTO result = new CalResultDTO();
        if(CollectionUtils.isEmpty(calTableList)){
            return result;
        }
        List<CalTableResultDTO> tableResultList = new ArrayList<>();
        for (CalTableScoreDTO calTableScore : calTableList) {
            CalTableResultDTO tableResult = new CalTableResultDTO();
            //获取表算分的处理器
            AbstractColumnObserver tableCalScoreHandler = AbstractColumnObserver.getTableCalScoreHandler(calTableScore.getTablePropertyEnum());
            if(Objects.isNull(tableCalScoreHandler)){
                continue;
            }
            tableCalScoreHandler.calTableResult(calTableScore, tableResult);
            tableResult.setDataTableId(calTableScore.getDataTableId());
            tableResultList.add(tableResult);
        }
        result.setTableList(tableResultList);
        for (CalTableResultDTO tableResult : tableResultList) {
            result.setResultScore(result.getResultScore().add(tableResult.getResultScore()));
            result.setCollectColumnNum(result.getCollectColumnNum() + tableResult.getCollectColumnNum());
            result.setTotalCalColumnNum(result.getTotalCalColumnNum() + tableResult.getTotalCalColumnNum());
            result.setCalTotalScore(result.getCalTotalScore().add(tableResult.getCalTotalScore()));
            result.setResultAward(result.getResultAward().add(tableResult.getResultAward()));
        }
        return result;
    }

    /**
     * 单表任务计算检查结果
     * @param calTableScore  表及项数据
     * @return 单个表结果
     */
    public static CalTableResultDTO getSingleTableResult(CalTableScoreDTO calTableScore){
        CalTableResultDTO tableResult = new CalTableResultDTO();
        if(Objects.isNull(calTableScore) || Objects.isNull(calTableScore.getMetaTable()) || CollectionUtils.isEmpty(calTableScore.getCalColumnList())){
            return tableResult;
        }
        //获取表算分的处理器
        AbstractColumnObserver tableCalScoreHandler = AbstractColumnObserver.getTableCalScoreHandler(calTableScore.getTablePropertyEnum());
        if(Objects.isNull(tableCalScoreHandler)){
            return tableResult;
        }
        tableCalScoreHandler.calTableResult(calTableScore, tableResult);
        tableResult.setDataTableId(calTableScore.getDataTableId());
        //如果检查表支持负分,则总得可为分为负数
        Integer isSupportNegativeScore = calTableScore.getMetaTable().getIsSupportNegativeScore();
        if(isSupportNegativeScore.equals(Constants.ZERO)){
            if(tableResult.getResultScore().compareTo(new BigDecimal(Constants.ZERO_STR)) < Constants.ZERO){
                tableResult.setResultScore(new BigDecimal(Constants.ZERO_STR));
            }
        }
        if(tableResult.getCalTotalScore().compareTo(new BigDecimal(Constants.ZERO_STR)) < Constants.ZERO){
            tableResult.setCalTotalScore(new BigDecimal(Constants.ZERO_STR));
        }
        return tableResult;
    }

    /**
     * 获取表的总分
     * @param metaTable
     * @param columnResultList
     * @return
     */
    public static BigDecimal getTableTotalScore(TbMetaTableDO metaTable, List<TbMetaColumnResultDO> columnResultList){
        BigDecimal tableScore = new BigDecimal(Constants.ZERO_STR);
        if(Objects.isNull(metaTable)){
            return tableScore;
        }
        if(MetaTablePropertyEnum.DEDUCT_SCORE_TABLE.getCode().equals(metaTable.getTableProperty())){
            return metaTable.getTotalScore();
        }
        if(CollectionUtils.isEmpty(columnResultList)){
            return tableScore;
        }
        if(Objects.nonNull(metaTable.getNoApplicableRule()) && !metaTable.getNoApplicableRule()){
            //不适用不计入总项数 过滤不适用项
            columnResultList = columnResultList.stream().filter(o->!CheckResultEnum.INAPPLICABLE.getCode().equals(o.getMappingResult())).collect(Collectors.toList());
        }

        Map<Long, List<TbMetaColumnResultDO>> columnMap = columnResultList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDO::getMetaColumnId));
        for (List<TbMetaColumnResultDO> resultList : columnMap.values()) {
            BigDecimal maxScore = resultList.stream().map(TbMetaColumnResultDO::getMaxScore).max(Comparator.comparing(o -> o)).orElse(new BigDecimal(Constants.ZERO));
            tableScore= tableScore.add(maxScore);
        }
        return tableScore;
    }

    /**
     * 获取各项的最高分
     * @param metaTable
     * @param columnResultList
     * @return 返回项的最高分Mao
     */
    public static Map<Long, BigDecimal> getColumnMaxScoreMap(TbMetaTableDO metaTable, List<TbMetaColumnResultDO> columnResultList){
        Map<Long, BigDecimal> resultMap = new HashMap<>();
        if(Objects.isNull(metaTable)){
            return resultMap;
        }
        if(CollectionUtils.isEmpty(columnResultList)){
            return resultMap;
        }
        if(Objects.nonNull(metaTable.getNoApplicableRule()) && !metaTable.getNoApplicableRule()){
            //不适用不计入总项数 过滤不适用项
            columnResultList = columnResultList.stream().filter(o->!CheckResultEnum.INAPPLICABLE.getCode().equals(o.getMappingResult())).collect(Collectors.toList());
        }

        Map<Long, List<TbMetaColumnResultDO>> columnMap = columnResultList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDO::getMetaColumnId));
        for (Long metaColumnId : columnMap.keySet()) {
            List<TbMetaColumnResultDO> resultList = columnMap.get(metaColumnId);
            BigDecimal maxScore = resultList.stream().map(TbMetaColumnResultDO::getMaxScore).max(Comparator.comparing(o -> o)).orElse(new BigDecimal(Constants.ZERO));
            resultMap.put(metaColumnId, maxScore);
        }
        return resultMap;
    }

    /**
     * 处理表总分以及项的最高分
     * @param metaTable
     */
    public static void getColumnMaxScoreMap(TbMetaStaTableDTO metaTable){
        if(Objects.isNull(metaTable)){
            return ;
        }
        List<TbMetaStaColumnDTO> staColumnList = metaTable.getStaColumnDTOList();
        if(CollectionUtils.isEmpty(staColumnList)){
            return ;
        }
        BigDecimal totalScore = new BigDecimal(Constants.ZERO_STR);
        for (TbMetaStaColumnDTO tbMetaStaColumn : staColumnList) {
            List<TbMetaColumnResultDTO> columnResultList = tbMetaStaColumn.getColumnResultDTOList();
            if(CollectionUtils.isEmpty(columnResultList)){
                tbMetaStaColumn.setSupportScore(tbMetaStaColumn.getMaxScore());
                tbMetaStaColumn.setLowestScore(tbMetaStaColumn.getMinScore());
                continue;
            }
            if(Objects.nonNull(metaTable.getNoApplicableRule()) && !metaTable.getNoApplicableRule()){
                columnResultList = columnResultList.stream().filter(o->!CheckResultEnum.INAPPLICABLE.getCode().equals(o.getMappingResult())).collect(Collectors.toList());
            }
            BigDecimal maxScore = columnResultList.stream().map(TbMetaColumnResultDTO::getMaxScore).max(Comparator.comparing(o -> o)).orElse(new BigDecimal(Constants.ZERO));
            BigDecimal maxAwardMoney = columnResultList.stream().map(TbMetaColumnResultDTO::getMoney).max(Comparator.comparing(o -> o)).orElse(new BigDecimal(Constants.ZERO));
            tbMetaStaColumn.setSupportScore(maxScore);
            tbMetaStaColumn.setAwardMoney(maxAwardMoney);
            tbMetaStaColumn.setPunishMoney(maxAwardMoney);
            if(!tbMetaStaColumn.getStatus()){
                //非冻结才加总分
                totalScore = totalScore.add(maxScore);
            }
        }
        //扣分表的时候不覆盖总分  其他类型的表冗余总分
        if(!MetaTablePropertyEnum.DEDUCT_SCORE_TABLE.getCode().equals(metaTable.getTableProperty())){
            metaTable.setTotalScore(totalScore);
        }

    }

    /**
     * 获取各项的最高奖项
     * @param metaTable
     * @param columnResultList
     * @return 返回项的最奖金Mao
     */
    public static Map<Long, BigDecimal> getColumnMaxAwardMap(TbMetaTableDO metaTable, List<TbMetaColumnResultDO> columnResultList){
        Map<Long, BigDecimal> resultMap = new HashMap<>();
        if(Objects.isNull(metaTable)){
            return resultMap;
        }
        if(CollectionUtils.isEmpty(columnResultList)){
            return resultMap;
        }
        if(Objects.nonNull(metaTable.getNoApplicableRule()) && !metaTable.getNoApplicableRule()){
            //不适用不计入总项数 过滤不适用项
            columnResultList = columnResultList.stream().filter(o->!CheckResultEnum.INAPPLICABLE.getCode().equals(o.getMappingResult())).collect(Collectors.toList());
        }

        Map<Long, List<TbMetaColumnResultDO>> columnMap = columnResultList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDO::getMetaColumnId));
        for (Long metaColumnId : columnMap.keySet()) {
            List<TbMetaColumnResultDO> resultList = columnMap.get(metaColumnId);
            BigDecimal maxScore = resultList.stream().map(TbMetaColumnResultDO::getMoney).max(Comparator.comparing(o -> o)).orElse(new BigDecimal(Constants.ZERO));
            resultMap.put(metaColumnId, maxScore);
        }
        return resultMap;
    }

    /**
     * 获取不同项的处理器
     * @param columnType  项类型
     * @return 项的处理器
     */
    public static AbstractColumnObserver getColumnCalScoreHandler(MetaColumnTypeEnum columnType){
        //普通项
        if(MetaColumnTypeEnum.STANDARD_COLUMN.equals(columnType)){
            return new StandardColumnObserver();
        }
        //高级项
        if(MetaColumnTypeEnum.HIGH_COLUMN.equals(columnType)){
            return new HighColumnObserver();
        }
        //红线项
        if(MetaColumnTypeEnum.RED_LINE_COLUMN.equals(columnType)){
            return new RedLineColumnObserver();
        }
        //否决项
        if(MetaColumnTypeEnum.VETO_COLUMN.equals(columnType)){
            return new VoteColumnObserver();
        }
        //加倍项
        if(MetaColumnTypeEnum.DOUBLE_COLUMN.equals(columnType)){
            return new DoubleColumnObserver();
        }
        //采集项
        if(MetaColumnTypeEnum.COLLECT_COLUMN.equals(columnType)){
            return new CollectColumnObserver();
        }
        //AI项
        if(MetaColumnTypeEnum.AI_COLUMN.equals(columnType)){
            return new AiColumnObserver();
        }
        return null;
    }

    /**
     * 获取检查表算分处理类
     * @param tablePropertyEnum 表类型
     * @return 表的处理器
     */
    public static AbstractColumnObserver getTableCalScoreHandler(MetaTablePropertyEnum tablePropertyEnum){
        //普通表
        if(MetaTablePropertyEnum.STANDARD_TABLE.equals(tablePropertyEnum)){
            return new StandardTableObserver();
        }
        //高级表
        if(MetaTablePropertyEnum.HIGH_TABLE.equals(tablePropertyEnum)){
            return new HighTableObserver();
        }
        //加分表
        if(MetaTablePropertyEnum.ADD_SCORE_TABLE.equals(tablePropertyEnum)){
            return new AddScoreTableObserver();
        }
        //权重表
        if(MetaTablePropertyEnum.WEIGHT_TABLE.equals(tablePropertyEnum)){
            return new WeightTableObserver();
        }
        //扣分表
        if(MetaTablePropertyEnum.DEDUCT_SCORE_TABLE.equals(tablePropertyEnum)){
            return new DeductScoreTableObserver();
        }
        //AI表
        if(MetaTablePropertyEnum.AI_TABLE.equals(tablePropertyEnum)){
            return new AiTableObserver();
        }
        //用户自定义表
        if(MetaTablePropertyEnum.USER_DEFINED_TABLE.equals(tablePropertyEnum)){
            return new UserDefinedTableObserver();
        }
        return null;
    }

    /**
     * 获取不适用项配置
     * @param calTableScore 表信息
     * @return 不适用规则  false:不计入总项数，true:计入总项数
     */
    protected static boolean getNoApplicableRule(CalTableScoreDTO calTableScore){
        TbMetaTableDO metaTable = calTableScore.getMetaTable();
        if(Objects.isNull(metaTable)){
            return true;
        }
        //不适用规则 false:不计入总项数，true:计入总项数
        return metaTable.getNoApplicableRule();
    }

    /**
     * 项排序
     * @param columnList 项列表
     * @return  根据排序返回的结果
     */
    protected List<CalColumnScoreDTO> sortColumnList(List<CalColumnScoreDTO> columnList){
        if(CollectionUtils.isEmpty(columnList)){
            return columnList;
        }
        log.info("sortColumnList_entity_columnList : {}", JSONObject.toJSONString(columnList));
        return columnList.stream().sorted((o1, o2) -> {
            CheckResultEnum checkResult1 = o1.getCheckResult();
            CheckResultEnum checkResult2 = o2.getCheckResult();

            if(checkResult1 == null && checkResult2 == null){
                return Constants.ZERO;
            }
            if (checkResult1 == null && checkResult2 != null){
                return Constants.NEGATIVE;
            }
            if (checkResult1 != null && checkResult2 == null){
                return Constants.ONE;
            }

            if(checkResult1.getCalScorePriority() > checkResult2.getCalScorePriority()){
                return Constants.INDEX_ONE;
            }
            if(checkResult1.getCalScorePriority().equals(checkResult2.getCalScorePriority())){
                if(o1.getColumnTypeEnum().getCalScorePriority() > o2.getColumnTypeEnum().getCalScorePriority()){
                    return Constants.INDEX_ONE;
                }
                if(o1.getColumnTypeEnum().getCalScorePriority().equals(o2.getColumnTypeEnum().getCalScorePriority())){
                    return Constants.ZERO;
                }
                return Constants.NEGATIVE;
            }
            return Constants.NEGATIVE;
        }).collect(Collectors.toList());
    }


}
