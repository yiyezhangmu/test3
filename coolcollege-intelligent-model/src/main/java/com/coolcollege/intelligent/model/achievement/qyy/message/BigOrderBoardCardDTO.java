package com.coolcollege.intelligent.model.achievement.qyy.message;

import com.coolcollege.intelligent.common.util.DateUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: BigOrderBoardDTO
 * @Description:大单播报
 * @date 2023-03-30 16:14
 */
@Data
public class BigOrderBoardCardDTO {

    /**
     * 门店id
     */
    private Long storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 公司id
     */
    private Long compId;

    /**
     * 公司名称
     */
    private String compName;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userImage;

    /**
     * 大单金额
     */
    private BigDecimal salesAmt;

    /**
     * 订单时间
     */
    private Date salesTm;

    /**
     * 联系他
     */
    private String contactUrl;
    /**
     * 详情
     */
    private String detail;


    public String getSalesAmt() {
        if(Objects.nonNull(salesAmt)){
//            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            DecimalFormat decimalFormat = new DecimalFormat();
            return decimalFormat.format(salesAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
        }
        return "-";
    }

    public String getSalesTm() {
        if(Objects.nonNull(salesTm)){
            return DateUtil.format(salesTm, "yyyy.MM.dd HH:mm");
        }
        return "-";
    }
}
