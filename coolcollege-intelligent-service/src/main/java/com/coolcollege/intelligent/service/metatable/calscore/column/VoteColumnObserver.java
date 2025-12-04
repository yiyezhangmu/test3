package com.coolcollege.intelligent.service.metatable.calscore.column;

import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: VoteColumnCalStore
 * @Description: 否决项计分
 * @date 2022-04-01 20:55
 */
public class VoteColumnObserver extends AbstractColumnObserver {

    public VoteColumnObserver() {
    }


    /**
     * 否决项某一项不合格时 该表得0分
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
        //过滤出不合格项
        List<CalColumnScoreDTO> failList = calColumnList.stream().filter(o->o.getColumnTypeEnum().equals(MetaColumnTypeEnum.VETO_COLUMN)).filter(o -> CheckResultEnum.FAIL.equals(o.getCheckResult())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(failList)){
            //如果不合格项不为空，把对应项都置为0分
            for (CalColumnScoreDTO calColumnScore : calColumnList) {
                calColumnScore.setScore(new BigDecimal(0));
                calColumnScore.setIsContinue(Boolean.FALSE);
            }
        }
        return columnScore.getScore();
    }
}
