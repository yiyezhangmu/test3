package com.coolcollege.intelligent.service.metatable.calscore.column;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: RedLineColumnCalScore
 * @Description: 红线项计分
 * @date 2022-04-01 20:55
 */
public class RedLineColumnObserver extends AbstractColumnObserver {

    public RedLineColumnObserver() {
    }

    /**
     * 红线项某一项不合格时  该项所对应的类都得0分
     * @param columnScore
     * @return
     */
    @Override
    public BigDecimal calColumnScore(CalTableScoreDTO tableScore, CalColumnScoreDTO columnScore) {
        //isContinue 为false的情况下 代表之前被处理过
        if(Objects.nonNull(columnScore.getIsContinue()) && !columnScore.getIsContinue()){
            return columnScore.getScore();
        }
        List<CalColumnScoreDTO> calColumnList = tableScore.getCalColumnList();
        if(CollectionUtils.isEmpty(calColumnList)){
            return columnScore.getScore();
        }
        //过滤出不合格项的红线项
        List<CalColumnScoreDTO> failList = calColumnList.stream().filter(o->o.getColumnTypeEnum().equals(MetaColumnTypeEnum.RED_LINE_COLUMN)).filter(o -> CheckResultEnum.FAIL.equals(o.getCheckResult())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(failList)){
            //如果不合格项不为空，把对应的分类所有的项都置为0分
            List<String> categoryNameList = failList.stream().map(CalColumnScoreDTO::getCategoryName).collect(Collectors.toList());
            Map<String, List<CalColumnScoreDTO>> categoryNameMap = calColumnList.stream().collect(Collectors.groupingBy(CalColumnScoreDTO::getCategoryName));
            for (String categoryName : categoryNameList) {
                List<CalColumnScoreDTO> calColumnScoreList = categoryNameMap.get(categoryName);
                for (CalColumnScoreDTO calColumnScore : calColumnScoreList) {
                    calColumnScore.setScore(new BigDecimal(Constants.ZERO_STR));
                    calColumnScore.setIsContinue(Boolean.FALSE);
                }
            }
        }
        return columnScore.getScore();
    }
}
