package com.coolcollege.intelligent.service.metatable.calscore.column;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: DoubleColumnCalScore
 * @Description: 加倍项计分
 * @date 2022-04-01 20:56
 */
public class DoubleColumnObserver extends AbstractColumnObserver {

    public DoubleColumnObserver() {
    }

    /**
     * 加倍项计分规则 分数乘以倍数
     * @param tableScore
     * @param columnScore
     * @return
     */
    @Override
    public BigDecimal calColumnScore(CalTableScoreDTO tableScore, CalColumnScoreDTO columnScore) {
        if(Objects.isNull(columnScore.getScoreTimes())){
            columnScore.setScoreTimes(BigDecimal.ONE);
        }
        return columnScore.getScore().multiply(columnScore.getScoreTimes()).setScale(Constants.SCALE, RoundingMode.HALF_UP);
    }
}
