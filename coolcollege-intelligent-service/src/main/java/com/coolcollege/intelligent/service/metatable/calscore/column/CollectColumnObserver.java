package com.coolcollege.intelligent.service.metatable.calscore.column;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;

import java.math.BigDecimal;

/**
 * @author zhangchenbiao
 * @FileName: CollectColumnCalScore
 * @Description:采集项计分
 * @date 2022-04-01 20:57
 */
public class CollectColumnObserver extends AbstractColumnObserver {

    public CollectColumnObserver() {
    }

    /**
     * 采集项不计入总分
     * @param tableScore
     * @param columnScore
     * @return
     */
    @Override
    public BigDecimal calColumnScore(CalTableScoreDTO tableScore, CalColumnScoreDTO columnScore) {
        return new BigDecimal(Constants.ZERO_STR);
    }
}
