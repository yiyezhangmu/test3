package com.coolcollege.intelligent.model.patrolstore.statistics;

import com.coolcollege.intelligent.common.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 乐乐茶得分率
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsTableRankLeLeTeaDTO {
    /**
     * 区域名称
     */
    String regionName;

    /**
     * 门店名称
     */
    String storeName;

    /**
     * 得分率
     */
    BigDecimal score;

    String regionId;

    public String getScore() {
        if (Objects.nonNull(score)){
            BigDecimal multiply = score.multiply(BigDecimal.valueOf(100));
            return  multiply.setScale(2,BigDecimal.ROUND_HALF_UP)+ "%";
        }
        return Constants.LINE+"%";
    }

    public BigDecimal getRealScore(){
        return score;
    }
}
